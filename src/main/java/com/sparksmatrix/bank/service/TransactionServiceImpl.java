package com.sparksmatrix.bank.service;

import com.sparksmatrix.bank.dto.TransactionRequestDto;
import com.sparksmatrix.bank.enums.TransactionType;
import com.sparksmatrix.bank.error.exception.BadRequestException;
import com.sparksmatrix.bank.model.Account;
import com.sparksmatrix.bank.model.Transaction;
import com.sparksmatrix.bank.repository.AccountRepository;
import com.sparksmatrix.bank.repository.TransactionRepository;
import com.sparksmatrix.bank.utils.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Value("${deposit.daily-max-amount}")
    private int dailyDepositMaxAmount;

    @Value("${deposit.transaction-max-amount}")
    private int maxDepositTransactionAmount;

    @Value("${deposit.daily-max-frequency}")
    private int dailyMaxDepositFrequency;

    @Value("${withdrawal.daily-max-amount}")
    private int dailyWithdrawalMaxAmount;

    @Value("${withdrawal.transaction-max-amount}")
    private int maxWithdrawalTransactionAmount;

    @Value("${withdrawal.daily-max-frequency}")
    private int dailyMaxWithdrawalFrequency;

    @Override
    @Transactional
    public ResponseWrapper deposit(TransactionRequestDto transactionRequestDto) {
        //check account exists
        Optional<Account> optionalAccount = accountRepository
                .findByAccountNumber(transactionRequestDto.getAccountNumber());
        if(optionalAccount.isEmpty()) {
            return ResponseWrapper.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("Account not found")
                    .build();
        }

        //check max deposit amount per transaction isn't exceeded
        if(transactionRequestDto.getAmount() > maxDepositTransactionAmount) {
            throw new BadRequestException("You have exceeded the maximum deposit amount.");
        }

        //validate deposit frequency
        Account account = optionalAccount.get();
        long depositsToday = transactionRepository.countTransactionForTodayByType(account.getId(), TransactionType.DEPOSIT);
        if(depositsToday >= dailyMaxDepositFrequency) {
            throw new BadRequestException("You have reached the maximum number of transactions for today.");
        }

        //validate max daily deposit
        BigDecimal todayDepositedSum =
                transactionRepository.sumTransactionForTodayByType(account.getId(), TransactionType.DEPOSIT);
        BigDecimal totalSumAfterDeposit = todayDepositedSum.add(BigDecimal.valueOf(transactionRequestDto.getAmount()));
        BigDecimal dailyDepositMaxAmountBigDecimal = BigDecimal.valueOf(dailyDepositMaxAmount);
        if (totalSumAfterDeposit.compareTo(dailyDepositMaxAmountBigDecimal) >= 0) {
            // Today’s deposit sum is greater than or equal to the max daily deposit amount
            throw new BadRequestException("You have exceeded the maximum daily deposit limit");
        }

        Transaction savedTransaction = persistTransactionAndUpdateBalance(account,
                BigDecimal.valueOf(transactionRequestDto.getAmount()), TransactionType.DEPOSIT);

        return ResponseWrapper.builder()
                .code(HttpStatus.OK.value())
                .message("Deposit successful")
                .data(savedTransaction).build();
    }

    @Override
    @Transactional
    public ResponseWrapper withdraw(TransactionRequestDto transactionRequestDto) {
        //check account exists
        Optional<Account> optionalAccount = accountRepository
                .findByAccountNumber(transactionRequestDto.getAccountNumber());
        if(optionalAccount.isEmpty()) {
            return ResponseWrapper.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("Account not found")
                    .build();
        }

        //check max withdrawal amount per transaction isn't exceeded
        if(transactionRequestDto.getAmount() > maxWithdrawalTransactionAmount) {
            throw new BadRequestException("You have exceeded the maximum withdrawal amount.");
        }

        //validate withdrawal frequency
        Account account = optionalAccount.get();
        long withdrawalsToday = transactionRepository.countTransactionForTodayByType(account.getId(), TransactionType.WITHDRAWAL);
        if(withdrawalsToday >= dailyMaxWithdrawalFrequency) {
            throw new BadRequestException("You have reached the maximum number of transactions for today.");
        }

        //validate max daily withdrawal
        BigDecimal todayWithdrawnSum = transactionRepository.sumTransactionForTodayByType(account.getId(), TransactionType.WITHDRAWAL);
        BigDecimal totalWithdrawnPlusAmount = todayWithdrawnSum.add(BigDecimal.valueOf(transactionRequestDto.getAmount()));
        BigDecimal dailyWithdrawalMaxAmountBigDecimal = BigDecimal.valueOf(dailyWithdrawalMaxAmount);
        if (totalWithdrawnPlusAmount.compareTo(dailyWithdrawalMaxAmountBigDecimal) >= 0) {
            // Today’s deposit sum is greater than or equal to the max daily deposit amount
            throw new BadRequestException("You have exceeded the maximum daily withdrawal limit");
        }

        //check balance is greater than or equal to amount
        BigDecimal withdrawalAmount = BigDecimal.valueOf(transactionRequestDto.getAmount());
        if (account.getBalance().compareTo(withdrawalAmount) < 0) {
            throw new BadRequestException("Insufficient balance.");
        }

        Transaction savedTransaction = persistTransactionAndUpdateBalance(account,
                BigDecimal.valueOf(transactionRequestDto.getAmount()), TransactionType.WITHDRAWAL);

        return ResponseWrapper.builder()
                .code(HttpStatus.OK.value())
                .message("Withdrawal successful")
                .data(savedTransaction).build();
    }

    public Transaction persistTransactionAndUpdateBalance(Account account, BigDecimal amount, TransactionType transactionType) {
        //Persist the new transaction
        Transaction transaction = Transaction.builder()
                .account(account)
                .type(transactionType)
                .amount(amount)
                .build();
        Transaction savedTransaction = transactionRepository.save(transaction);

        //Update the account balance
        BigDecimal balance = account.getBalance();
        balance = switch (transactionType) {
            case TransactionType.DEPOSIT -> balance.add(amount);
            case TransactionType.WITHDRAWAL -> balance.subtract(amount);
        };
        account.setBalance(balance);
        accountRepository.save(account);

        return savedTransaction;
    }
}

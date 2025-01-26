package com.sparksmatrix.bank.service;

import com.sparksmatrix.bank.dto.AccountBalanceResponseDto;
import com.sparksmatrix.bank.dto.AccountCreateDto;
import com.sparksmatrix.bank.model.Account;
import com.sparksmatrix.bank.repository.AccountRepository;
import com.sparksmatrix.bank.utils.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountServiceImpl implements  AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public ResponseWrapper createAccount(AccountCreateDto accountCreateDto) {
        Optional<Account> optionalAccount = accountRepository
                .findByAccountNumber(accountCreateDto.getAccountNumber());
        if (optionalAccount.isPresent()) {
            return ResponseWrapper.builder()
                    .code(HttpStatus.CONFLICT.value())
                    .message("Account already exists")
                    .build();
        }

        var account = Account.builder()
                .name(accountCreateDto.getName())
                .accountNumber(accountCreateDto.getAccountNumber())
                .balance(BigDecimal.ZERO)
                .build();

        var savedAccount = accountRepository.save(account);

        return ResponseWrapper.builder()
                .code(HttpStatus.CREATED.value())
                .message("Account created successfully")
                .data(savedAccount).build();
    }

    @Override
    public ResponseWrapper getAccountBalance(String accountNumber) {
        Optional<BigDecimal> optionalBalance = accountRepository.getAccountBalance(accountNumber);
        if(optionalBalance.isEmpty()) {
            return ResponseWrapper.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("Account not found")
                    .build();
        }

        AccountBalanceResponseDto accountBalanceResponseDto = AccountBalanceResponseDto
                .builder()
                .balance(optionalBalance.get())
                .build();

        return ResponseWrapper.builder()
                .code(HttpStatus.OK.value())
                .message("Balance fetched successfully")
                .data(accountBalanceResponseDto).build();
    }
}

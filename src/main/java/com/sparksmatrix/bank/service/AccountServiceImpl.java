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
        if (ObjectUtils.isEmpty(savedAccount)) {
            return ResponseWrapper.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Error occurred creating account. Kindly try again")
                    .build();
        }

        return ResponseWrapper.builder()
                .code(HttpStatus.CREATED.value())
                .message("Account created successfully")
                .data(savedAccount).build();
    }

    @Override
    public ResponseWrapper getAccountBalance(String accountNumber) {
        Optional<Account> optionalAccount = accountRepository
                .findByAccountNumber(accountNumber);
        if(optionalAccount.isEmpty()) {
            return ResponseWrapper.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("Account not found")
                    .build();
        }

        Account account = optionalAccount.get();
        AccountBalanceResponseDto accountBalanceResponseDto = AccountBalanceResponseDto
                .builder()
                .balance(account.getBalance())
                .build();

        return ResponseWrapper.builder()
                .code(HttpStatus.OK.value())
                .message("Balance fetched successfully")
                .data(accountBalanceResponseDto).build();
    }
}

package com.sparksmatrix.bank.service;

import com.sparksmatrix.bank.dto.AccountCreateDto;
import com.sparksmatrix.bank.model.Account;
import com.sparksmatrix.bank.repository.AccountRepository;
import com.sparksmatrix.bank.utils.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;

@Service
public class AccountServiceImpl implements  AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public ResponseWrapper createAccount(AccountCreateDto accountCreateDto) {
        var account = Account.builder()
                .name(accountCreateDto.getName())
                .accountNumber(accountCreateDto.getAccountNumber())
                .balance(BigDecimal.ZERO)
                .build();

        var savedAccount = accountRepository.save(account);
        if (ObjectUtils.isEmpty(savedAccount)) {
            return ResponseWrapper.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Error occurred creating account. Kindly try again").build();
        }

        return ResponseWrapper.builder()
                .code(HttpStatus.CREATED.value())
                .message("Account created successfully")
                .data(savedAccount).build();
    }
}

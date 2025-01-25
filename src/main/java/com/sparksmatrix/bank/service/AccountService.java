package com.sparksmatrix.bank.service;

import com.sparksmatrix.bank.dto.AccountCreateDto;
import com.sparksmatrix.bank.model.Account;
import com.sparksmatrix.bank.utils.ResponseWrapper;

public interface AccountService {
    ResponseWrapper createAccount(AccountCreateDto accountCreateDto);
}

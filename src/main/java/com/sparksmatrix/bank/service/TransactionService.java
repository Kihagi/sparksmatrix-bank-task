package com.sparksmatrix.bank.service;

import com.sparksmatrix.bank.dto.TransactionRequestDto;
import com.sparksmatrix.bank.utils.ResponseWrapper;

public interface TransactionService {
    ResponseWrapper deposit(TransactionRequestDto transactionRequestDto);
    ResponseWrapper withdraw(TransactionRequestDto transactionRequestDto);
}

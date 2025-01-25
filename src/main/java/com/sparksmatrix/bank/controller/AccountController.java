package com.sparksmatrix.bank.controller;

import com.sparksmatrix.bank.dto.AccountCreateDto;
import com.sparksmatrix.bank.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity createAccount(@Valid @RequestBody AccountCreateDto accountCreateDto) {
        var response = accountService.createAccount(accountCreateDto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity getAccountBalance(@PathVariable String accountNumber) {
        var response = accountService.getAccountBalance(accountNumber);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}

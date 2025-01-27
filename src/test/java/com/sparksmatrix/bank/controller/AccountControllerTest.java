package com.sparksmatrix.bank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparksmatrix.bank.configuration.JpaAuditingConfig;
import com.sparksmatrix.bank.dto.AccountBalanceResponseDto;
import com.sparksmatrix.bank.dto.AccountCreateDto;
import com.sparksmatrix.bank.dto.TransactionRequestDto;
import com.sparksmatrix.bank.service.AccountService;
import com.sparksmatrix.bank.service.TransactionService;
import com.sparksmatrix.bank.utils.ResponseWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@ImportAutoConfiguration(exclude = JpaAuditingConfig.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAccount() throws Exception {
        //Build request object
        AccountCreateDto accountCreateDto = new AccountCreateDto();
        accountCreateDto.setName("John Doe");
        accountCreateDto.setAccountNumber("333454356");

        ResponseWrapper responseWrapper = ResponseWrapper.builder()
                .code(HttpStatus.CREATED.value())
                .message("Account created successfully")
                .data(accountCreateDto)
                .build();

        when(accountService.createAccount(accountCreateDto))
                .thenReturn(responseWrapper);

        mockMvc.perform(post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountCreateDto)))
                .andExpect(status().isCreated());

        verify(accountService, times(1)).createAccount(any(AccountCreateDto.class));
    }

    @Test
    void testGetAccountBalance() throws Exception {
        // Arrange
        String accountNumber = "123456789";

        AccountBalanceResponseDto accountBalanceResponseDto = AccountBalanceResponseDto
                .builder()
                .balance(BigDecimal.valueOf(1000.0))
                .build();

        ResponseWrapper responseWrapper = ResponseWrapper.builder()
                .code(HttpStatus.OK.value())
                .message("Balance fetched successfully")
                .data(accountBalanceResponseDto)
                .build();

        when(accountService.getAccountBalance(accountNumber)).thenReturn(responseWrapper);

        mockMvc.perform(get("/api/account/balance/{accountNumber}", accountNumber))
                .andExpect(status().isOk());

        verify(accountService, times(1)).getAccountBalance(accountNumber);
    }

    @Test
    void testDepositFunds() throws Exception {
        // Arrange
        TransactionRequestDto requestDto = new TransactionRequestDto();
        requestDto.setAccountNumber("123456789");
        requestDto.setAmount(500);

        ResponseWrapper response =ResponseWrapper.builder()
                .code(HttpStatus.OK.value())
                .message("Deposit successful")
                .data(null)
                .build();

        when(transactionService.deposit(requestDto)).thenReturn(response);

        mockMvc.perform(post("/api/account/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(transactionService, times(1)).deposit(requestDto);
    }

    @Test
    void testWithdrawFunds() throws Exception {
        TransactionRequestDto requestDto = new TransactionRequestDto();
        requestDto.setAccountNumber("123456789");
        requestDto.setAmount(200);

        ResponseWrapper response = ResponseWrapper.builder()
                .code(HttpStatus.OK.value())
                .message("Withdrawal successful")
                .data(null)
                .build();

        when(transactionService.withdraw(requestDto)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/account/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(transactionService, times(1)).withdraw(requestDto);
    }
}
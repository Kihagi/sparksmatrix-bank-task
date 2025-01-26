package com.sparksmatrix.bank.service;

import com.sparksmatrix.bank.dto.AccountBalanceResponseDto;
import com.sparksmatrix.bank.dto.AccountCreateDto;
import com.sparksmatrix.bank.model.Account;
import com.sparksmatrix.bank.repository.AccountRepository;
import com.sparksmatrix.bank.utils.ResponseWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private AccountCreateDto accountCreateDto;

    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        accountCreateDto = AccountCreateDto.builder()
                .name("John Doe")
                .accountNumber("123456")
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldCreateAccountWhenAccountDoesNotExist() {
        Mockito.when(accountRepository.findByAccountNumber("123456"))
                .thenReturn(Optional.empty());

        Account savedAccount = Account.builder()
                .id(1L)
                .name("John Doe")
                .accountNumber("123456")
                .balance(BigDecimal.ZERO)
                .build();

        Mockito.when(accountRepository.save(Mockito.any(Account.class)))
                .thenReturn(savedAccount);

        ResponseWrapper response = accountService.createAccount(accountCreateDto);

        assertThat(response.getCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getMessage()).isEqualTo("Account created successfully");
        assertThat(response.getData()).isEqualTo(savedAccount);

        Mockito.verify(accountRepository, times(1)).findByAccountNumber("123456");
        Mockito.verify(accountRepository, times(1)).save(Mockito.any(Account.class));
    }

    @Test
    void shouldReturnConflictWhenAccountAlreadyExists() {
        Account existingAccount = Account.builder()
                .name("Existing User")
                .accountNumber("123456")
                .balance(BigDecimal.TEN)
                .build();

        Mockito.when(accountRepository.findByAccountNumber("123456"))
                .thenReturn(Optional.of(existingAccount));

        // Act
        ResponseWrapper response = accountService.createAccount(accountCreateDto);

        // Assert
        assertThat(response.getCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.getMessage()).isEqualTo("Account already exists");
        Mockito.verify(accountRepository, times(1)).findByAccountNumber("123456");
        Mockito.verify(accountRepository, Mockito.never()).save(Mockito.any(Account.class));
    }

    @Test
    void shouldReturnBalanceSuccessfullyWhenAccountExists() {
        // Arrange
        String accountNumber = "123456";
        BigDecimal expectedBalance = BigDecimal.valueOf(5000.00);
        AccountBalanceResponseDto expectedResponse = AccountBalanceResponseDto.builder()
                .balance(expectedBalance)
                .build();

        Mockito.when(accountRepository.getAccountBalance(accountNumber))
                .thenReturn(Optional.of(expectedBalance));

        ResponseWrapper response = accountService.getAccountBalance(accountNumber);

        assertThat(response.getCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo("Balance fetched successfully");
        assertThat(response.getData()).isInstanceOf(AccountBalanceResponseDto.class);

        AccountBalanceResponseDto actualResponse = (AccountBalanceResponseDto) response.getData();
        assertThat(actualResponse).usingRecursiveComparison().isEqualTo(expectedResponse);

        Mockito.verify(accountRepository, times(1)).getAccountBalance(accountNumber);
    }

    @Test
    void shouldReturnNotFoundWhenAccountDoesNotExist() {
        String accountNumber = "999999";

        Mockito.when(accountRepository.getAccountBalance(accountNumber))
                .thenReturn(Optional.empty());

        ResponseWrapper response = accountService.getAccountBalance(accountNumber);

        // Assert
        assertThat(response.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getMessage()).isEqualTo("Account not found");
        assertThat(response.getData()).isNull();

        // Verify repository call
        Mockito.verify(accountRepository, times(1)).getAccountBalance(accountNumber);
    }
}
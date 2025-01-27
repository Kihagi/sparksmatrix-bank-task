package com.sparksmatrix.bank.service;

import com.sparksmatrix.bank.dto.TransactionRequestDto;
import com.sparksmatrix.bank.enums.TransactionType;
import com.sparksmatrix.bank.error.exception.BadRequestException;
import com.sparksmatrix.bank.model.Account;
import com.sparksmatrix.bank.model.Transaction;
import com.sparksmatrix.bank.repository.AccountRepository;
import com.sparksmatrix.bank.repository.TransactionRepository;
import com.sparksmatrix.bank.utils.ResponseWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    TransactionRepository transactionRepository;

    @InjectMocks
    TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set the field values
        ReflectionTestUtils.setField(transactionService, "maxDepositTransactionAmount", 40000);
        ReflectionTestUtils.setField(transactionService, "dailyMaxDepositFrequency", 4);
        ReflectionTestUtils.setField(transactionService, "dailyDepositMaxAmount", 150000);
        ReflectionTestUtils.setField(transactionService, "dailyWithdrawalMaxAmount", 50000);
        ReflectionTestUtils.setField(transactionService, "maxWithdrawalTransactionAmount", 20000);
        ReflectionTestUtils.setField(transactionService, "dailyMaxWithdrawalFrequency", 3);
    }

    @Test
    public void testDeposit_AccountNotFound() {
        // Arrange
        TransactionRequestDto request = new TransactionRequestDto();
        request.setAccountNumber("123456789");
        request.setAmount(100);

        when(accountRepository.findByAccountNumber("123456789")).thenReturn(Optional.empty());

        // Act
        ResponseWrapper response = transactionService.deposit(request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
        assertEquals("Account not found", response.getMessage());
        verify(accountRepository, times(1)).findByAccountNumber("123456789");
    }

    @Test
    public void testDeposit_ExceedMaxDepositAmount() {
        // Arrange
        TransactionRequestDto request = new TransactionRequestDto();
        request.setAccountNumber("123456789");
        request.setAmount(50000); // Exceeds max deposit amount

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("123456789");

        when(accountRepository.findByAccountNumber("123456789")).thenReturn(Optional.of(account));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            transactionService.deposit(request);
        });

        assertEquals("You have exceeded the maximum deposit amount.", exception.getMessage());
        verify(accountRepository, times(1)).findByAccountNumber("123456789");
    }

    @Test
    public void testDeposit_ExceedDailyDepositFrequency() {
        // Arrange
        TransactionRequestDto request = new TransactionRequestDto();
        request.setAccountNumber("123456789");
        request.setAmount(100);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("123456789");

        when(accountRepository.findByAccountNumber("123456789")).thenReturn(Optional.of(account));
        when(transactionRepository.countTransactionForTodayByType(1L, TransactionType.DEPOSIT)).thenReturn(4);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            transactionService.deposit(request);
        });

        assertEquals("You have reached the maximum number of transactions for today.", exception.getMessage());
        verify(accountRepository, times(1)).findByAccountNumber("123456789");
        verify(transactionRepository, times(1)).countTransactionForTodayByType(1L, TransactionType.DEPOSIT);
    }

    @Test
    public void testDeposit_ExceedDailyDepositLimit() {
        // Arrange
        TransactionRequestDto request = new TransactionRequestDto();
        request.setAccountNumber("123456789");
        request.setAmount(11000);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("123456789");

        when(accountRepository.findByAccountNumber("123456789")).thenReturn(Optional.of(account));
        when(transactionRepository.sumTransactionForTodayByType(1L, TransactionType.DEPOSIT))
                .thenReturn(BigDecimal.valueOf(140000)); // Today's deposit sum

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            transactionService.deposit(request);
        });

        assertEquals("You have exceeded the maximum daily deposit limit", exception.getMessage());
        verify(accountRepository, times(1)).findByAccountNumber("123456789");
        verify(transactionRepository, times(1)).sumTransactionForTodayByType(1L, TransactionType.DEPOSIT);
    }

    @Test
    public void testDeposit_Success() {
        // Arrange
        TransactionRequestDto request = new TransactionRequestDto();
        request.setAccountNumber("123456789");
        request.setAmount(100);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("123456789");
        account.setBalance(BigDecimal.valueOf(1000.0));

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAccount(account);
        transaction.setAmount(BigDecimal.valueOf(100.0));
        transaction.setType(TransactionType.DEPOSIT);

        when(accountRepository.findByAccountNumber("123456789")).thenReturn(Optional.of(account));
        when(transactionRepository.countTransactionForTodayByType(1L, TransactionType.DEPOSIT)).thenReturn(0);
        when(transactionRepository.sumTransactionForTodayByType(1L, TransactionType.DEPOSIT))
                .thenReturn(BigDecimal.valueOf(0.0));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // Act
        ResponseWrapper response = transactionService.deposit(request);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertEquals("Deposit successful", response.getMessage());
        assertNotNull(response.getData());
        verify(accountRepository, times(1)).findByAccountNumber("123456789");
        verify(transactionRepository, times(1)).countTransactionForTodayByType(1L, TransactionType.DEPOSIT);
        verify(transactionRepository, times(1)).sumTransactionForTodayByType(1L, TransactionType.DEPOSIT);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    public void testWithdraw_ExceedMaxWithdrawalAmount() {
        // Arrange
        TransactionRequestDto request = new TransactionRequestDto();
        request.setAccountNumber("123456789");
        request.setAmount(50000); // Exceeds max withdrawal amount

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("123456789");

        when(accountRepository.findByAccountNumber("123456789")).thenReturn(Optional.of(account));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            transactionService.withdraw(request);
        });

        assertEquals("You have exceeded the maximum withdrawal amount.", exception.getMessage());
        verify(accountRepository, times(1)).findByAccountNumber("123456789");
    }

    @Test
    public void testWithdraw_ExceedDailyWithdrawalFrequency() {
        // Arrange
        TransactionRequestDto request = new TransactionRequestDto();
        request.setAccountNumber("123456789");
        request.setAmount(100);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("123456789");

        when(accountRepository.findByAccountNumber("123456789")).thenReturn(Optional.of(account));
        when(transactionRepository.countTransactionForTodayByType(1L, TransactionType.WITHDRAWAL)).thenReturn(3); // Exceeds frequency

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            transactionService.withdraw(request);
        });

        assertEquals("You have reached the maximum number of transactions for today.", exception.getMessage());
        verify(accountRepository, times(1)).findByAccountNumber("123456789");
        verify(transactionRepository, times(1)).countTransactionForTodayByType(1L, TransactionType.WITHDRAWAL);
    }

    @Test
    public void testWithdraw_ExceedDailyWithdrawalLimit() {
        // Arrange
        TransactionRequestDto request = new TransactionRequestDto();
        request.setAccountNumber("123456789");
        request.setAmount(10000);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("123456789");

        when(accountRepository.findByAccountNumber("123456789")).thenReturn(Optional.of(account));
        when(transactionRepository.sumTransactionForTodayByType(1L, TransactionType.WITHDRAWAL))
                .thenReturn(BigDecimal.valueOf(45000.0)); // Today's withdrawal sum

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            transactionService.withdraw(request);
        });

        assertEquals("You have exceeded the maximum daily withdrawal limit", exception.getMessage());
        verify(accountRepository, times(1)).findByAccountNumber("123456789");
        verify(transactionRepository, times(1)).sumTransactionForTodayByType(1L, TransactionType.WITHDRAWAL);
    }

    @Test
    public void testWithdraw_InsufficientBalance() {
        // Arrange
        TransactionRequestDto request = new TransactionRequestDto();
        request.setAccountNumber("123456789");
        request.setAmount(1000);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("123456789");
        account.setBalance(BigDecimal.valueOf(500.0)); // Insufficient balance

        when(accountRepository.findByAccountNumber("123456789")).thenReturn(Optional.of(account));
        when(transactionRepository.sumTransactionForTodayByType(1L, TransactionType.WITHDRAWAL))
                .thenReturn(BigDecimal.valueOf(1000));
        when(transactionRepository.countTransactionForTodayByType(1L, TransactionType.WITHDRAWAL)).thenReturn(1);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            transactionService.withdraw(request);
        });

        assertEquals("Insufficient balance.", exception.getMessage());
        verify(accountRepository, times(1)).findByAccountNumber("123456789");
    }

    @Test
    public void testWithdraw_Success() {
        // Arrange
        TransactionRequestDto request = new TransactionRequestDto();
        request.setAccountNumber("123456789");
        request.setAmount(100);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("123456789");
        account.setBalance(BigDecimal.valueOf(1000.0));

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAccount(account);
        transaction.setAmount(BigDecimal.valueOf(100.0));
        transaction.setType(TransactionType.WITHDRAWAL);

        when(accountRepository.findByAccountNumber("123456789")).thenReturn(Optional.of(account));
        when(transactionRepository.countTransactionForTodayByType(1L, TransactionType.WITHDRAWAL)).thenReturn(0);
        when(transactionRepository.sumTransactionForTodayByType(1L, TransactionType.WITHDRAWAL))
                .thenReturn(BigDecimal.valueOf(0.0));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // Act
        ResponseWrapper response = transactionService.withdraw(request);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertEquals("Withdrawal successful", response.getMessage());
        assertNotNull(response.getData());
        verify(accountRepository, times(1)).findByAccountNumber("123456789");
        verify(transactionRepository, times(1)).countTransactionForTodayByType(1L, TransactionType.WITHDRAWAL);
        verify(transactionRepository, times(1)).sumTransactionForTodayByType(1L, TransactionType.WITHDRAWAL);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(accountRepository, times(1)).save(any(Account.class));
    }
}
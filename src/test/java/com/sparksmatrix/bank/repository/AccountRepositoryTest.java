package com.sparksmatrix.bank.repository;

import com.sparksmatrix.bank.model.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    Account account;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .name("Anthony Mathenge")
                .accountNumber("000553245765")
                .balance(BigDecimal.ZERO)
                .build();
        accountRepository.save(account);
    }

    @AfterEach
    void tearDown() {
        account = null;
        accountRepository.deleteAll();
    }

    @Test
    void testFindByAccountNumber_WhenAccountExists() {
        String accountNumber = "000553245765";

       Optional<Account> result = accountRepository
               .findByAccountNumber(accountNumber);

        assertTrue(result.isPresent());
        assertEquals(account.getAccountNumber(), result.get().getAccountNumber());
        assertEquals(account.getName(), result.get().getName());
        assertEquals(account.getBalance(), result.get().getBalance());
    }

    @Test
    void testFindByAccountNumber_WhenAccountDoesNotExist() {
        String accountNumber = "12345678";

        Optional<Account> result = accountRepository.findByAccountNumber(accountNumber);

        assertFalse(result.isPresent());
    }

    @Test
    void testShouldReturnBalanceForValidAccount() {
        String accountNumber = "000553245765";

        Optional<BigDecimal> balance = accountRepository.getAccountBalance(accountNumber);

        assertTrue(balance.isPresent());
        assertThat(balance.get().compareTo(account.getBalance())).isZero();
    }

    @Test
    void testShouldReturnEmptyForNonExistentAccount() {
        Optional<BigDecimal> balance = accountRepository.getAccountBalance("nonexistent");

        assertThat(balance).isEmpty();
    }
}
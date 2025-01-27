package com.sparksmatrix.bank.repository;

import com.sparksmatrix.bank.enums.TransactionType;
import com.sparksmatrix.bank.model.Account;
import com.sparksmatrix.bank.model.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(TransactionRepositoryTest.class);

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    Account account;
    Transaction transaction;

    @BeforeEach
    void setUp() {
        // Create and persist Account
        account = Account.builder()
                .name("Test Account")
                .accountNumber("123456789")
                .balance(BigDecimal.valueOf(1000))
                .build();
        account = entityManager.persistAndFlush(account);
        logger.info("Inserted Account: {}", account);

        // Create and persist transactions for today
        Transaction transaction1 = Transaction.builder()
                .account(account)
                .amount(BigDecimal.valueOf(500))
                .type(TransactionType.DEPOSIT)
                .build();
        entityManager.persistAndFlush(transaction1);

        Transaction transaction2 = Transaction.builder()
                .account(account)
                .amount(BigDecimal.valueOf(100))
                .type(TransactionType.DEPOSIT)
                .build();
        entityManager.persistAndFlush(transaction2);

        Transaction transaction3 = Transaction.builder()
                .account(account)
                .amount(BigDecimal.valueOf(200))
                .type(TransactionType.WITHDRAWAL)
                .build();
        entityManager.persistAndFlush(transaction3);
    }

    @AfterEach
    void tearDown() {
        account = null;
        transaction = null;
        transactionRepository.deleteAll();
    }

    @Test
    void testCountTransactionForTodayByType() {
        int count = transactionRepository.countTransactionForTodayByType(account.getId(), TransactionType.DEPOSIT);

        assertThat(count).isEqualTo(2);
    }

    @Test
    void testCountTransactionForTodayByTypeWhenEmpty() {
        transactionRepository.deleteAll();

        int count = transactionRepository.countTransactionForTodayByType(account.getId(), TransactionType.DEPOSIT);

        assertThat(count).isEqualTo(0);
    }

    @Test
    void shouldReturnSumOfTransactionsForTodayByType() {
        BigDecimal sum = transactionRepository.sumTransactionForTodayByType(account.getId(), TransactionType.DEPOSIT);

        assertThat(sum).isEqualByComparingTo(BigDecimal.valueOf(600));
    }

    @Test
    void shouldReturnZeroIfNoTransaction() {
        transactionRepository.deleteAll();

        BigDecimal sum = transactionRepository.sumTransactionForTodayByType(account.getId(), TransactionType.DEPOSIT);

        assertThat(sum).isEqualByComparingTo(BigDecimal.valueOf(0));
    }
}
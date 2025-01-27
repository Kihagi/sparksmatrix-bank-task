package com.sparksmatrix.bank.repository;

import com.sparksmatrix.bank.enums.TransactionType;
import com.sparksmatrix.bank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.account.id = :accountId AND t.type = :transactionType AND CAST(t.createdAt AS DATE) = CURRENT_DATE")
    int countTransactionForTodayByType(@Param("accountId") Long accountId, @Param("transactionType") TransactionType transactionType);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.account.id = :accountId AND t.type = :transactionType AND CAST(t.createdAt AS DATE) = CURRENT_DATE")
    BigDecimal sumTransactionForTodayByType(@Param("accountId") Long accountId, @Param("transactionType") TransactionType transactionType);
}

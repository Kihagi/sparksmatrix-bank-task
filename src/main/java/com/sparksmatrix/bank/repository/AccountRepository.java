package com.sparksmatrix.bank.repository;

import com.sparksmatrix.bank.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);

    @Query("SELECT a.balance FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<BigDecimal> getAccountBalance(@Param("accountNumber") String accountNumber);
}

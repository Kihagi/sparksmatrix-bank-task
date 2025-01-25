package com.sparksmatrix.bank.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@Table(name = "accounts")
public class Account extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "balance")
    private BigDecimal balance;
}

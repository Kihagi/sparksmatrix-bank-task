package com.sparksmatrix.bank.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountBalanceResponseDto {
    private BigDecimal balance;
}

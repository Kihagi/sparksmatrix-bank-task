package com.sparksmatrix.bank.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequestDto {

    @NotNull(message = "Please provide the account name")
    @NotEmpty(message = "Please provide the the account name")
    private String accountNumber;

    @NotNull(message = "Please provide amount")
    @Min(value = 1, message = "Amount must be greater than 0")
    private int amount;
}

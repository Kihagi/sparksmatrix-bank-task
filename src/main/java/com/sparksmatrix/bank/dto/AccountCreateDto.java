package com.sparksmatrix.bank.dto;

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
public class AccountCreateDto {

    @NotNull(message = "Please provide the account name")
    @NotEmpty(message = "Please provide the the account name")
    private String name;

    @NotNull(message = "Please provide the account number")
    @NotEmpty(message = "Please provide the the account number")
    private String accountNumber;
}

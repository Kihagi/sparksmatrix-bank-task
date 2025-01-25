package com.sparksmatrix.bank.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountCreateDto {

    @NotNull(message = "Please provide the account name")
    @NotEmpty(message = "Please provide the the account name")
    private String name;

    @NotNull(message = "Please provide the account number")
    @NotEmpty(message = "Please provide the the account number")
    private String accountNumber;
}

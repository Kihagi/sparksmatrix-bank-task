package com.sparksmatrix.bank.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@ToString
public class ResponseWrapper<T> {
    private final int code;
    private final String message;
    private T data;

    /**
     * Check if the transaction is successful
     *
     * @return
     */
    public boolean isSuccessful() {
        if ((this.code == HttpStatus.OK.value()) || (this.code == HttpStatus.CREATED.value())) {
            return true;
        }
        return false;
    }
}

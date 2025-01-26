package com.sparksmatrix.bank.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import static java.text.MessageFormat.format;


@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "There is an error in your request")
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(String message, Object... arguments) {
        super(format(message, arguments));
    }

    public BadRequestException(Throwable cause, String message, Object... arguments) {
        super(format(message, arguments), cause);
    }
}

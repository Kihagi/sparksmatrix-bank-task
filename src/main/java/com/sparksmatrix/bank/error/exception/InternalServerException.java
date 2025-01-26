package com.sparksmatrix.bank.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import static java.text.MessageFormat.format;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error processing request")
public class InternalServerException extends RuntimeException {
    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerException(String message, Object... arguments) {
        super(format(message, arguments));
    }

    public InternalServerException(Throwable cause, String message, Object... arguments) {
        super(format(message, arguments), cause);
    }
}

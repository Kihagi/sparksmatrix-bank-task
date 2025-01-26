package com.sparksmatrix.bank.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import static java.text.MessageFormat.format;


@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Entity not found")
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotFoundException(String message, Object... arguments) {
        super(format(message, arguments));
    }

    public EntityNotFoundException(Throwable cause, String message, Object... arguments) {
        super(format(message, arguments), cause);
    }
}

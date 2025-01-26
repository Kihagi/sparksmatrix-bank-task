package com.sparksmatrix.bank.error.exception;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class PersistenceException extends RuntimeException {

    public PersistenceException(Class clazz, Object obj) {
        super(PersistenceException.generateMessage(clazz, obj));
    }

    private static String generateMessage(Class clazz, Object obj) {
        String base = StringUtils.capitalize(clazz.getSimpleName()) + " could not be persisted. Request body: ";

        if (Objects.isNull(obj)) {
            base = base + "\"null\"";
        } else {
            base = base + clazz.cast(obj).toString();
        }

        return base;
    }
}

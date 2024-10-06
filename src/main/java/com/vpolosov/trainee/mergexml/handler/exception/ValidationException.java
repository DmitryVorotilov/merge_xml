package com.vpolosov.trainee.mergexml.handler.exception;

import java.util.Set;

public class ValidationException extends RuntimeException {

    private final Set<String> messages;

    public ValidationException(Set<String> messages) {
        super(String.join("; ", messages));
        this.messages = messages;
    }

    public String messages() {
        return String.join("; ", messages);
    }
}

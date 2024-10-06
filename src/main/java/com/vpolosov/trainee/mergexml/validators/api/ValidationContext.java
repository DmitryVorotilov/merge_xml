package com.vpolosov.trainee.mergexml.validators.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationContext {

    private final List<FailMessage> failMessages;

    public ValidationContext() {
        this.failMessages = new ArrayList<>();
    }

    public void addMessage(String message, String... fields) {
        failMessages.add(new FailMessage(message, fields));
    }

    public String[] failFields() {
        return failMessages.stream()
            .flatMap(failMessage -> Arrays.stream(failMessage.fields()))
            .toArray(String[]::new);
    }

    public Set<String> messages() {
        return failMessages.stream()
            .map(failMessage -> String.format("%s - %s", failMessage.message(), String.join(",", failMessage.fields())))
            .collect(Collectors.toSet());
    }

    public boolean isEmpty() {
        return failMessages.isEmpty();
    }
}

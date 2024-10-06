package com.vpolosov.trainee.mergexml.validators.api;

public interface Validation<T> {

    boolean validate(T t, ValidationContext context);
}

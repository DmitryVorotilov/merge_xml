package com.vpolosov.trainee.mergexml.validators.api;

public interface BiValidation<T1, T2> {

    boolean validate(T1 t1, T2 t2, ValidationContext context);
}

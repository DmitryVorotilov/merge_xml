package com.vpolosov.trainee.mergexml.handler.exception;

import java.util.Set;

/**
 * Исключение выбрасываемое при превышении размера файла в 500 кб.
 *
 * @author Ali Takushinov
 */
public class MoreFiveHundredKbException extends ValidationException {

    /**
     * Конструктор с одним параметром.
     *
     * @param message текст ошибки.
     */
    public MoreFiveHundredKbException(String message) {
        super(Set.of(message));
    }

}

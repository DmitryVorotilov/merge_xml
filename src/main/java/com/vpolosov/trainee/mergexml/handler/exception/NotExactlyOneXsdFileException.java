package com.vpolosov.trainee.mergexml.handler.exception;

import java.util.Set;

/**
 * Исключение выбрасываемо когда XSD файл не один.
 *
 * @author Ali Takushinov
 */
public class NotExactlyOneXsdFileException extends ValidationException {

    /**
     * Конструктор с одним параметром.
     *
     * @param message текст ошибки.
     */
    public NotExactlyOneXsdFileException(String message) {
        super(Set.of(message));
    }
}

package com.vpolosov.trainee.mergexml.handler.exception;

import java.util.Set;

/**
 * Исключение выбрасываемое когда количество файлов не соответствует 10.
 *
 * @author ALi Takushinov
 */
public class NotExactlyTenFilesException extends ValidationException {

    /**
     * Конструктор с одним параметром.
     *
     * @param message текст ошибки.
     */
    public NotExactlyTenFilesException(String message) {
        super(Set.of(message));
    }
}

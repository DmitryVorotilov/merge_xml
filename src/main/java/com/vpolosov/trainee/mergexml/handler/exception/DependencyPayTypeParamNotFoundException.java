package com.vpolosov.trainee.mergexml.handler.exception;

/**
 * Исключение выбрасываемое при отсутствии информации по типу платежа.
 *
 * @author Maksim Litvinenko
 */
public class DependencyPayTypeParamNotFoundException extends RuntimeException {

    /**
     * Конструктор с одним параметром.
     *
     * @param message текст ошибки.
     */
    public DependencyPayTypeParamNotFoundException(String message) {
        super(message);
    }
}

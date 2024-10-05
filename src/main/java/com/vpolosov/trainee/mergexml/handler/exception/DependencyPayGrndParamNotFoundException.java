package com.vpolosov.trainee.mergexml.handler.exception;

/**
 * Исключение выбрасываемое при отсутствии информации по основанию платежа.
 *
 * @author Maksim Litvinenko
 */
public class DependencyPayGrndParamNotFoundException extends RuntimeException {

    /**
     * Конструктор с одним параметром.
     *
     * @param message текст ошибки.
     */
    public DependencyPayGrndParamNotFoundException(String message) {
        super(message);
    }
}

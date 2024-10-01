package com.vpolosov.trainee.mergexml.event;

import com.vpolosov.trainee.mergexml.model.ValidationFileHistory;
import com.vpolosov.trainee.mergexml.model.ValidationProcess;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * Событие, представляющее процесс валидации платежных документов.
 * Содержит информацию о процессе валидации и, при необходимости, историю валидации отдельных файлов.
 *
 * @author Artyom Bogaichuk
 */
@Getter
public class ValidationProcessEvent extends ApplicationEvent {
    /**
     * Объект процесса валидации.
     */
    private final ValidationProcess validationProcess;

    /**
     * Список историй валидации отдельных файлов.
     */
    private final List<ValidationFileHistory> validationFileHistories;

    /**
     * Конструктор события с процессом валидации и историей файлов.
     *
     * @param source                  источник события.
     * @param validationProcess       объект процесса валидации.
     * @param validationFileHistories список историй валидации файлов.
     */
    public ValidationProcessEvent(
            Object source,
            ValidationProcess validationProcess,
            List<ValidationFileHistory> validationFileHistories
    ) {
        super(source);
        this.validationProcess = validationProcess;
        this.validationFileHistories = validationFileHistories;
    }

    /**
     * Конструктор события с процессом валидации без истории файлов.
     *
     * @param source            источник события.
     * @param validationProcess объект процесса валидации.
     */
    public ValidationProcessEvent(
            Object source,
            ValidationProcess validationProcess
    ) {
        super(source);
        this.validationProcess = validationProcess;
        this.validationFileHistories = null;
    }
}
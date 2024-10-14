package com.vpolosov.trainee.mergexml.event;

import com.vpolosov.trainee.mergexml.model.ValidationProcess;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Событие, представляющее успешное создание итогового документа.
 * Содержит информацию о процессе валидации, включая ссылку на итоговый документ.
 *
 * @author Artyom Bogaichuk
 */
@Getter
public class TotalDocumentGeneratedEvent extends ApplicationEvent {

    /**
     * Объект процесса валидации с обновленным totalDocRef.
     */
    private final ValidationProcess validationProcess;

    /**
     * Путь к итоговому документу.
     */
    private final String totalDocReference;

    /**
     * Конструктор события.
     *
     * @param source            источник события.
     * @param validationProcess объект процесса валидации.
     * @param totalDocReference путь к итоговому документу.
     */
    public TotalDocumentGeneratedEvent(Object source, ValidationProcess validationProcess, String totalDocReference) {
        super(source);
        this.validationProcess = validationProcess;
        this.totalDocReference = totalDocReference;
    }
}

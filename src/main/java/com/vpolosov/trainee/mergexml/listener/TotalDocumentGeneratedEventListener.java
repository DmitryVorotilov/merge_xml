package com.vpolosov.trainee.mergexml.listener;

import com.vpolosov.trainee.mergexml.event.TotalDocumentGeneratedEvent;
import com.vpolosov.trainee.mergexml.model.ValidationProcess;
import com.vpolosov.trainee.mergexml.repository.ValidationProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Слушатель событий успешного создания итогового документа.
 * Обрабатывает события {@link TotalDocumentGeneratedEvent}
 * и обновляет процесс валидации в базе данных, добавляя totalDocRef.
 *
 * @author Artyom Bogaichuk
 */
@Component
@RequiredArgsConstructor
public class TotalDocumentGeneratedEventListener {

    /**
     * Репозиторий для операций с {@link ValidationProcess}.
     */
    private final ValidationProcessRepository validationProcessRepository;

    /**
     * Обрабатывает событие {@link TotalDocumentGeneratedEvent}.
     * Обновляет процесс валидации, добавляя ссылку на итоговый документ.
     *
     * @param event событие успешного создания итогового документа.
     */
    @Transactional
    @EventListener
    public void handleTotalDocumentGeneratedEvent(TotalDocumentGeneratedEvent event) {
        ValidationProcess validationProcess = event.getValidationProcess();
        validationProcess.setTotalDocRef(event.getTotalDocReference());
        validationProcessRepository.save(validationProcess);
    }
}

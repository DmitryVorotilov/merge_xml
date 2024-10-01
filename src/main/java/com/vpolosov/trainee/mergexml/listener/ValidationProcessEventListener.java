package com.vpolosov.trainee.mergexml.listener;

import com.vpolosov.trainee.mergexml.event.ValidationProcessEvent;
import com.vpolosov.trainee.mergexml.model.ValidationFileHistory;
import com.vpolosov.trainee.mergexml.model.ValidationProcess;
import com.vpolosov.trainee.mergexml.repository.ValidationFileHistoryRepository;
import com.vpolosov.trainee.mergexml.repository.ValidationProcessRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Слушатель событий процесса валидации.
 * Обрабатывает события {@link ValidationProcessEvent},
 * сохраняет информацию о процессе валидации и историях файлов в базу данных.
 *
 * @author Artyom Bogaichuk
 */
@Component
@RequiredArgsConstructor
public class ValidationProcessEventListener {
    /**
     * Репозиторий для операций с {@link ValidationProcess}.
     */
    private final ValidationProcessRepository validationProcessRepository;

    /**
     * Репозиторий для операций с {@link ValidationFileHistory}.
     */
    private final ValidationFileHistoryRepository validationFileHistoryRepository;

    /**
     * Обрабатывает событие {@link ValidationProcessEvent}.
     * Сохраняет процесс валидации и связанные с ним истории файлов в базе данных.
     *
     * @param event событие процесса валидации.
     */
    @Transactional
    @EventListener
    public void handleValidationProcessEvent(ValidationProcessEvent event) {
        ValidationProcess validationProcess = event.getValidationProcess();
        List<ValidationFileHistory> validationFileHistories = event.getValidationFileHistories();

        ValidationProcess savedValidationProcess = validationProcessRepository.save(validationProcess);
        if (validationFileHistories != null && !validationFileHistories.isEmpty()) {
            validationFileHistories.forEach(history -> history.setValidationProcess(savedValidationProcess));
            validationFileHistoryRepository.saveAll(validationFileHistories);
        }
    }
}

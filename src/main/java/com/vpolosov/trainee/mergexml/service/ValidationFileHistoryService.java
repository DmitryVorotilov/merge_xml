package com.vpolosov.trainee.mergexml.service;

import com.vpolosov.trainee.mergexml.dtos.ValidationFileHistoryDto;
import com.vpolosov.trainee.mergexml.mappers.ValidationFileHistoryMapper;
import com.vpolosov.trainee.mergexml.model.ValidationFileHistory;
import com.vpolosov.trainee.mergexml.model.ValidationProcess;
import com.vpolosov.trainee.mergexml.repository.ValidationFileHistoryRepository;
import com.vpolosov.trainee.mergexml.repository.ValidationProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Сервисный слой для работы с историей валидации файлов.
 *
 * @author Samat Hamzin
 */
@Service
@RequiredArgsConstructor
public class ValidationFileHistoryService {

    /**
     * Репозиторий для работы с БД истории валидаций XML файлов.
     */
    private final ValidationFileHistoryRepository validationFileHistoryRepository;

    /**
     * Репозиторий для работы с БД процесса валидации.
     */
    private final ValidationProcessRepository validationProcessRepository;

    /**
     * Маппер для преобразования из сущностей в ДТО и обратно.
     */
    private final ValidationFileHistoryMapper mapper;

    /**
     * Сохраняет процесс валидации в отдельной транзакции.
     *
     * @param validationProcess процесс валидации для сохранения.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveValidationProcess(ValidationProcess validationProcess) {
        validationProcessRepository.save(validationProcess);
    }

    /**
     * Сохраняет процесс валидации и историю валидации файлов в отдельной транзакции.
     *
     * @param validationProcess       процесс валидации.
     * @param validationFileHistories список историй валидации файлов.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveValidationHistory(
            ValidationProcess validationProcess,
            List<ValidationFileHistory> validationFileHistories
    ) {
        validationProcessRepository.save(validationProcess);
        validationFileHistoryRepository.saveAll(validationFileHistories);
    }

    /**
     * Получение и преобразование всех провалидируемых файлов из сущностей в дто.
     *
     * @return Список отсортированных по убыванию провалидируемых файлов по дате валидации.
     */
    public List<ValidationFileHistoryDto> getAllValidatedFiles() {
        return mapper.toDtoList(validationFileHistoryRepository.findAllByOrderByValidationDateDesc());
    }
}

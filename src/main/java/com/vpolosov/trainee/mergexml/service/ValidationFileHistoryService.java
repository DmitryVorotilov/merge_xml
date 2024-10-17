package com.vpolosov.trainee.mergexml.service;

import com.vpolosov.trainee.mergexml.dtos.ValidationFileHistoryDto;
import com.vpolosov.trainee.mergexml.mappers.ValidationFileHistoryMapper;
import com.vpolosov.trainee.mergexml.repository.ValidationFileHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
     * Репозиторий для работы с БД.
     */
    private final ValidationFileHistoryRepository repository;
    /**
     * Маппер для преобразования из сущностей в ДТО и обратно.
     */
    private final ValidationFileHistoryMapper mapper;

    /**
     * Получение и преобразование всех провалидируемых файлов из сущностей в дто.
     *
     * @return Список отсортированных по убыванию провалидируемых файлов по дате валидации.
     */
    public List<ValidationFileHistoryDto> getAllValidatedFiles() {
        return mapper.toDtoList(repository.findAllByOrderByValidationDateDesc());
    }
}

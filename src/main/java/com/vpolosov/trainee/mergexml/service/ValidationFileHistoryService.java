package com.vpolosov.trainee.mergexml.service;

import com.vpolosov.trainee.mergexml.aspect.Loggable;
import com.vpolosov.trainee.mergexml.dtos.ValidationFileHistoryDto;
import com.vpolosov.trainee.mergexml.handler.filter.ValidationFileHistoryFilter;
import com.vpolosov.trainee.mergexml.mappers.ValidationFileHistoryMapper;
import com.vpolosov.trainee.mergexml.model.ValidationFileHistory;
import com.vpolosov.trainee.mergexml.model.ValidationFileHistory_;
import com.vpolosov.trainee.mergexml.repository.ValidationFileHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


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
     * Получение и преобразование всех провалидированных файлов из сущностей в дто.
     *
     * @return Список отсортированных по убыванию, либо в зависимости от параметра провалидированных файлов по дате валидации.
     */
    @Loggable
    @Transactional(readOnly = true)
    public Page<ValidationFileHistoryDto> getAllValidatedFiles(Pageable pageable, Sort.Direction sortDirection) {
        Sort sort = (sortDirection == Sort.Direction.ASC)
                ? Sort.by(ValidationFileHistory_.VALIDATION_DATE).ascending()
                : Sort.by(ValidationFileHistory_.VALIDATION_DATE).descending();
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<ValidationFileHistory> historyPage = repository.findAll(sortedPageable);
        return historyPage.map(mapper::toDto);
    }

    /**
     * Получение страницы провалидированных файлов на основе фильтрации.
     *
     * @return Страница объектов {@code ValidationFileHistoryDto}, соответствующих фильтру и пагинации.
     */
    @Loggable
    @Transactional(readOnly = true)
    public Page<ValidationFileHistoryDto> getFilteredHistory(ValidationFileHistoryFilter filter, Pageable pageable) {
        Specification<ValidationFileHistory> spec = filter.getSpecification();
        Page<ValidationFileHistory> histories = repository.findAll(spec, pageable);
        return histories.map(mapper::toDto);
    }

    /**
     * Извлекает историю файла проверки по его уникальному идентификатору.
     *
     * @return {@link ValidationFileHistoryDto}, соответствующий данному идентификатору.
     * @throws EntityNotFoundException если история файлов проверки с данным идентификатором не найдена.
     */
    @Transactional(readOnly = true)
    public ValidationFileHistoryDto getValidationFileHistoryById(UUID id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Validation file history with id %s not found.".formatted(id))));
    }
}

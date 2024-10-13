package com.vpolosov.trainee.mergexml.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.vpolosov.trainee.mergexml.dtos.ValidationFileHistoryDto;
import com.vpolosov.trainee.mergexml.dtos.views.ValidationFileHistoryDtoViews;
import com.vpolosov.trainee.mergexml.handler.filter.ValidationFileHistoryFilter;
import com.vpolosov.trainee.mergexml.service.ValidationFileHistoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST API контроллер для получения информации по истории валидации файлов.
 *
 * @author Samat Hamzin
 */
@RestController
@RequestMapping("/fileHistory")
@RequiredArgsConstructor
public class ValidationFileHistoryController {

    /**
     * Сервис по истории валидации файлов.
     */
    private final ValidationFileHistoryService service;


    /**
     * GET: получение полного списка провалидированных файлов.
     *
     * @param pageable      Объект с параметрами фильтрации, содержащий спецификации.
     * @param sortDirection Объект с параметром для определения направления сортировки.
     * @return Статус ОК и страница объектов {@code ValidationFileHistoryDto}.
     */
    @JsonView(ValidationFileHistoryDtoViews.Output.class)
    @GetMapping("/all")
    public ResponseEntity<Page<ValidationFileHistoryDto>> getAll(
            @ParameterObject Pageable pageable,
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection) {

        return ResponseEntity.ok(service.getAllValidatedFiles(pageable, sortDirection));
    }

    /**
     * GET: получение провалидированных файлов по фильтру.
     *
     * @param filter   Объект с параметрами фильтрации, содержащий спецификации.
     * @param pageable Объект, содержащий параметры пагинации (номер страницы, размер страницы и сортировка).
     * @return Страница объектов {@code ValidationFileHistoryDto}, соответствующих фильтру и пагинации.
     */
    @GetMapping
    public Page<ValidationFileHistoryDto> getFilteredHistory(
            @ParameterObject @ModelAttribute @Valid ValidationFileHistoryFilter filter,
            @ParameterObject Pageable pageable) {
        return service.getFilteredHistory(filter, pageable);
    }

    /**
     * Извлекает историю файла проверки по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор истории файла проверки, которую нужно получить, представленный как UUID.
     * @return {@link ValidationFileHistoryDto}, соответствующий данному идентификатору.
     * @throws EntityNotFoundException если история файлов проверки с данным идентификатором не найдена.
     */
    @GetMapping({"/{id}"})
    public ValidationFileHistoryDto getById(@PathVariable UUID id) {
        return service.getValidationFileHistoryById(id);
    }
}

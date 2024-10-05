package com.vpolosov.trainee.mergexml.controller;

import com.vpolosov.trainee.mergexml.aspect.Loggable;
import com.vpolosov.trainee.mergexml.service.HistoryService;
import com.vpolosov.trainee.mergexml.service.MergeService;
import com.vpolosov.trainee.mergexml.service.ValidationProcessService;
import com.vpolosov.trainee.mergexml.service.ValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * REST API для сбора и объединения файлов рублевых платежей в один документ.
 *
 * @author Ali Takushinov
 * @author Andrei Stalybka
 * @author Maksim Litvinenko
 */
@RestController
@RequestMapping("/xml")
@RequiredArgsConstructor
@Tag(name = "MergeController", description = "Контроллер для слияния документов и получения логов.")
public class MergeController {

    /**
     *
     */
    private final ValidationProcessService validationProcessService;

    /**
     *
     */
    private final ValidationService validationService;

    /**
     * Сервис объединения платёжных документов.
     */
    private final MergeService mergeService;

    /**
     * Сервис хранения истории операций.
     */
    private final HistoryService historyService;

    /**
     * POST : объединение платёжных документов.
     *
     * @param path путь до каталога с платёжными документами.
     * @return статус OK и сообщение об успешном выполнении операции.
     */
    @PostMapping
    @Loggable
    @Operation(
            summary = "Объедениение документов.",
            description = "Позволят объеденить несколько платежных докментов."
    )
    public String patchXml(@Parameter(description = "Путь к директории с документами для объединения.",
            required = true)
                           @RequestBody String path) {
        var startValidationProcessDto = validationProcessService.startValidationProcess(path);
        var validatedDocumentsDto = validationService.validate(startValidationProcessDto);
        var total = mergeService.merge(validatedDocumentsDto);
        historyService.addHistoryFromTotal(total);
        validationProcessService.successValidationProcess(startValidationProcessDto.id(), total);
        return "Total.xml was created!";
    }

    /**
     * GET : возвращает список логов пользователя.
     *
     * @return список логов пользователя.
     * @throws IOException если не удалось прочитать данные с файла.
     */
    @GetMapping("/logs")
    @Loggable
    @Operation(
            summary = "Получение логов.",
            description = "Позволят посмотреть пользовательские логи."
    )
    public String getLogs() throws IOException {
        String path = "logs/user-logs.log";
        return Files.readString(Path.of(path));
    }
}

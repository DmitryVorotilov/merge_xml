package com.vpolosov.trainee.mergexml.service;

import com.vpolosov.trainee.mergexml.config.ConfigProperties;
import com.vpolosov.trainee.mergexml.dtos.FilesInfo;
import com.vpolosov.trainee.mergexml.event.ValidationProcessEvent;
import com.vpolosov.trainee.mergexml.handler.exception.NotExactlyOneXsdFileException;
import com.vpolosov.trainee.mergexml.handler.exception.NotExactlyTenFilesException;
import com.vpolosov.trainee.mergexml.model.ValidationFileHistory;
import com.vpolosov.trainee.mergexml.model.ValidationProcess;
import com.vpolosov.trainee.mergexml.utils.DocumentUtil;
import com.vpolosov.trainee.mergexml.utils.FileUtil;
import com.vpolosov.trainee.mergexml.validators.Validators;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.validation.Validator;
import java.io.File;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.vpolosov.trainee.mergexml.utils.Constant.FIRST_ELEMENT;
import static com.vpolosov.trainee.mergexml.utils.XmlTags.PAYER;

/**
 * Сервис для валидации XML файлов платежных документов.
 *
 * @author Artyom Bogaichuk
 */
@Service
@RequiredArgsConstructor
public class ValidationService {

    /**
     * Логирование для пользователя.
     */
    private final Logger loggerForUser;

    /**
     * Вспомогательный класс для работы с {@link Document}.
     */
    private final DocumentUtil documentUtil;

    /**
     * Вспомогательный класс для работы с файлами.
     */
    private final FileUtil fileUtil;

    /**
     * Свойства приложения.
     */
    private final ConfigProperties configProperties;

    /**
     * Валидаторы XML документа.
     */
    private final Validators validators;

    /**
     * Часы для корректировки времени.
     */
    private final Clock clock;

    /**
     * Публикатор событий приложения.
     */
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Валидирует XML файлы в указанном пути.
     *
     * @param path              путь до каталога с платёжными документами.
     * @param validationProcess объект процесса валидации.
     * @return список валидных XML файлов.
     * @throws Exception если возникли ошибки в процессе валидации.
     */
    public List<File> validateFiles(String path, ValidationProcess validationProcess) {
        initializeValidationProcess(path, validationProcess);
        Validator validator;
        FilesInfo files;
        try {
            files = listFiles(path);
            validator = validators.createValidator(files.xsdFile());
        } catch (Exception e) {
            eventPublisher.publishEvent(new ValidationProcessEvent(this, validationProcess));
            throw e;
        }

        List<ValidationFileHistory> validationFileHistories = new ArrayList<>();

        List<File> xmlFiles = files.xmlFiles();
        var payer = documentUtil.getValueByTagName(xmlFiles.get(FIRST_ELEMENT), PAYER);

        for (File xmlFile : xmlFiles) {
            ValidationFileHistory validationFileHistory
                    = initializeValidationFileHistory(xmlFile.getName(), xmlFile.getAbsolutePath(), validationProcess);

            try {
                Document document = documentUtil.parse(xmlFile);

                validators.validate(document, validator, payer);
                loggerForUser.info("Файл {} прошел проверку.", documentUtil.getFileName(document));

                validationFileHistory.setIsSuccess(true);
                validationFileHistory.setFailureReason(null);
            } catch (Exception e) {
                validationFileHistory.setIsSuccess(false);
                validationFileHistory.setFailureReason(e.getMessage());

                validationFileHistories.add(validationFileHistory);
                eventPublisher.publishEvent(
                        new ValidationProcessEvent(this, validationProcess, validationFileHistories)
                );
                throw e;
            }

            validationFileHistories.add(validationFileHistory);
        }

        validationProcess.setIsSuccess(true);
        eventPublisher.publishEvent(
                new ValidationProcessEvent(this, validationProcess, validationFileHistories)
        );
        return xmlFiles;
    }

    /**
     * Инициализирует процесс валидации конкретного платёжного документа.
     *
     * @param fileName          название документа.
     * @param path              путь к документу.
     * @param validationProcess общий процесс валидации, в рамках которого проверяется этот документ.
     * @return объект {@link ValidationFileHistory} с установленными начальными значениями.
     */
    private ValidationFileHistory initializeValidationFileHistory(
            String fileName,
            String path,
            ValidationProcess validationProcess
    ) {
        ValidationFileHistory validationFileHistory = new ValidationFileHistory();
        validationFileHistory.setFileName(fileName);
        validationFileHistory.setDocRef(path);
        validationFileHistory.setValidationDate(LocalDateTime.now(clock));
        validationFileHistory.setValidationProcess(validationProcess);
        return validationFileHistory;
    }

    /**
     * Инициализирует процесс валидации.
     *
     * @param path              путь до каталога с платёжными документами.
     * @param validationProcess объект процесса валидации.
     */
    private void initializeValidationProcess(String path, ValidationProcess validationProcess) {
        validationProcess.setDirRef(path);
        validationProcess.setValidationProcessDate(LocalDateTime.now(clock));
        validationProcess.setIsSuccess(false);
    }

    /**
     * Получает список файлов XML и XSD из указанного каталога.
     *
     * @param path путь до каталога с файлами.
     * @return объект {@link FilesInfo}, содержащий список XML файлов и XSD файл.
     * @throws NotExactlyTenFilesException   если количество XML файлов в директории не равно 10.
     * @throws NotExactlyOneXsdFileException если количество XSD файлов в директории не равно 1.
     * @throws RuntimeException              если возникла ошибка при получении файлов.
     */
    private FilesInfo listFiles(String path) {
        try {
            List<File> xmlFiles = fileUtil.listXml(
                    path, configProperties.getMinCountFiles(), configProperties.getMaxCountFiles()
            );
            File xsdFile = fileUtil.xsd(path);
            return new FilesInfo(xmlFiles, xsdFile);
        } catch (Exception e) {
            loggerForUser.error("Не удалось получить список файлов: {}", e.getMessage());
            throw e;
        }
    }
}

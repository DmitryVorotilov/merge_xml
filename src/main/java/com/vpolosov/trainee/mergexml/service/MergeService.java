package com.vpolosov.trainee.mergexml.service;

import com.vpolosov.trainee.mergexml.aspect.Loggable;
import com.vpolosov.trainee.mergexml.config.ConfigProperties;
import com.vpolosov.trainee.mergexml.handler.exception.MoreFiveHundredKbException;
import com.vpolosov.trainee.mergexml.model.ValidationFileHistory;
import com.vpolosov.trainee.mergexml.model.ValidationProcess;
import com.vpolosov.trainee.mergexml.utils.DocumentUtil;
import com.vpolosov.trainee.mergexml.utils.FileUtil;
import com.vpolosov.trainee.mergexml.utils.TransformerUtil;
import com.vpolosov.trainee.mergexml.validators.Validators;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Validator;
import java.io.File;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.vpolosov.trainee.mergexml.utils.Constant.EMPTY_SIZE;
import static com.vpolosov.trainee.mergexml.utils.Constant.FIRST_ELEMENT;
import static com.vpolosov.trainee.mergexml.utils.XmlTags.BS_HEAD;
import static com.vpolosov.trainee.mergexml.utils.XmlTags.BS_MESSAGE;
import static com.vpolosov.trainee.mergexml.utils.XmlTags.DATE_TIME;
import static com.vpolosov.trainee.mergexml.utils.XmlTags.DOCUMENTS;
import static com.vpolosov.trainee.mergexml.utils.XmlTags.ID;
import static com.vpolosov.trainee.mergexml.utils.XmlTags.PAYER;

/**
 * Сервис объединения платёжных документов.
 *
 * @author Ali Takushinov
 * @author Maksim Litvinenko
 * @author Artyom Bogaichuk
 */
@Service
@RequiredArgsConstructor
public class MergeService {

    /**
     * Логирование для пользователя.
     */
    private final Logger loggerForUser;

    /**
     * Вспомогательный класс для работы с файлами.
     */
    private final FileUtil fileUtil;

    /**
     * Вспомогательный класс для работы с {@link Document}.
     */
    private final DocumentUtil documentUtil;

    /**
     * Вспомогательный класс для работы с {@link Transformer}.
     */
    private final TransformerUtil transformerUtil;

    /**
     * Валидаторы XML документа.
     */
    private final Validators validators;

    /**
     * Свойства приложения.
     */
    private final ConfigProperties configProperties;

    /**
     * Часы для корректировки времени.
     */
    private final Clock clock;

    /**
     * Парсер для результирующего файла.
     */
    private final DateTimeFormatter totalTimeFormat;

    /**
     * Сервис для сохранения истории валидации.
     */
    private final ValidationFileHistoryService validationFileHistoryService;

    /**
     * Объединяет XML файлы в каталоге для создания платёжного документа.
     *
     * @param path путь до каталога с платёжными документами.
     * @return объединённый документ платёжных операций.
     */
    @Loggable
    public Document merge(String path) {
        ValidationProcess validationProcess = initializeValidationProcess(path);
        List<ValidationFileHistory> validationFileHistories = new ArrayList<>();

        Document targetDocument = documentUtil.create();

        try {
            FilesInfo files = listFiles(path);
            var validator = validators.createValidator(files.xsdFile());

            validateAndAggregateFiles(
                    files.xmlFiles(),
                    validator,
                    targetDocument,
                    validationProcess,
                    validationFileHistories
            );

            generateTotalDocument(targetDocument, path, validationProcess);

            validationFileHistoryService.saveValidationHistory(validationProcess, validationFileHistories);
            return targetDocument;
        } catch (Exception e) {
            validationProcess.setIsSuccess(false);
            validationFileHistoryService.saveValidationHistory(validationProcess, validationFileHistories);
            throw e;
        }
    }

    /**
     * Инициализирует процесс валидации.
     *
     * @param path путь до каталога с платёжными документами.
     * @return объект {@link ValidationProcess} с установленными начальными значениями.
     */
    private ValidationProcess initializeValidationProcess(String path) {
        ValidationProcess validationProcess = new ValidationProcess();
        validationProcess.setDirRef(path);
        validationProcess.setValidationProcessDate(LocalDateTime.now(clock));
        validationProcess.setIsSuccess(false);
        return validationProcess;
    }

    /**
     * Получает список файлов XML и XSD из указанного каталога.
     *
     * @param path путь до каталога с файлами.
     * @return объект {@link FilesInfo}, содержащий список XML файлов и XSD файл.
     * @throws RuntimeException если возникла ошибка при получении файлов.
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

    /**
     * Валидирует и агрегирует XML файлы.
     *
     * @param xmlFiles                 список XML файлов для обработки.
     * @param validator                валидатор XSD схемы.
     * @param targetDocument           результирующий документ для агрегирования.
     * @param validationProcess        процесс валидации.
     * @param validationFileHistories  список историй валидации файлов.
     */
    private void validateAndAggregateFiles(List<File> xmlFiles, Validator validator, Document
            targetDocument, ValidationProcess validationProcess, List<ValidationFileHistory> validationFileHistories) {
        for (File xmlFile : xmlFiles) {
            ValidationFileHistory validationFileHistory = new ValidationFileHistory();
            validationFileHistory.setFileName(xmlFile.getName());
            validationFileHistory.setDocRef(xmlFile.getAbsolutePath());
            validationFileHistory.setValidationDate(LocalDateTime.now(clock));
            validationFileHistory.setValidationProcess(validationProcess);

            var payer = documentUtil.getValueByTagName(xmlFiles.get(FIRST_ELEMENT), PAYER);

            try {
                Document document = documentUtil.parse(xmlFile);

                validators.validate(document, validator, payer);
                loggerForUser.info("Файл {} прошел проверку.", documentUtil.getFileName(document));
                aggregateTotal(document, targetDocument);

                validationFileHistory.setIsSuccess(true);
                validationFileHistory.setFailureReason(null);
            } catch (Exception e) {
                validationFileHistory.setIsSuccess(false);
                validationFileHistory.setFailureReason(e.getMessage());

                validationFileHistories.add(validationFileHistory);
                validationFileHistoryService.saveValidationHistory(validationProcess, validationFileHistories);
                throw e;
            }

            validationFileHistories.add(validationFileHistory);
        }

        validationProcess.setIsSuccess(true);
    }

    /**
     * Генерирует итоговый документ с платежными операциями.
     *
     * @param targetDocument    результирующий документ, содержащий объединённые данные.
     * @param path              путь до каталога для сохранения итогового файла.
     * @param validationProcess процесс валидации, в который записывается информация о результате.
     * @throws MoreFiveHundredKbException если размер итогового файла превышает 500 КБ.
     */
    private void generateTotalDocument(Document targetDocument, String path, ValidationProcess validationProcess) {
        try {
            targetDocument.normalizeDocument();
            targetDocument.getElementsByTagName(BS_MESSAGE)
                    .item(FIRST_ELEMENT)
                    .getAttributes()
                    .getNamedItem(ID)
                    .setNodeValue(UUID.randomUUID().toString());
            targetDocument.getElementsByTagName(BS_MESSAGE)
                    .item(FIRST_ELEMENT)
                    .getAttributes()
                    .getNamedItem(DATE_TIME)
                    .setNodeValue(LocalDateTime.now().toString());

            targetDocument.normalize();
            DOMSource dom = new DOMSource(targetDocument);

            var fileName = fileUtil.fileNameWithTime(configProperties.getFileName(), clock, totalTimeFormat);
            var total = new File(path, fileName);
            transformerUtil.transform(dom, new StreamResult(total));

            if (validators.checkFileSize().isMoreThanFiveKb(total)) {
                validationProcess.setIsSuccess(false);
                validationFileHistoryService.saveValidationProcess(validationProcess);

                fileUtil.delete(total);
                throw new MoreFiveHundredKbException("There are more than 500 kb files");
            }

            validationProcess.setTotalDocRef(total.getAbsolutePath());
        } catch (Exception e) {
            loggerForUser.error("Failed to generate total document: {}", e.getMessage());

            validationProcess.setIsSuccess(false);
            validationFileHistoryService.saveValidationProcess(validationProcess);

            throw e;
        }
    }

    /**
     * Формирование общего документа с информацией о платёжных операциях.
     *
     * @param document       содержит информацию о платёжной операции.
     * @param targetDocument конечный документ, в который объединяется информация о платёжных операциях.
     */
    @Loggable
    private void aggregateTotal(Document document, Document targetDocument) {
        document.getDocumentElement().normalize();
        if (targetDocument.getElementsByTagName(DOCUMENTS).getLength() == EMPTY_SIZE) {
            NodeList documentNodeList = document.getChildNodes();
            Node targetNode = targetDocument.importNode(documentNodeList.item(FIRST_ELEMENT), true);
            targetDocument.appendChild(targetNode);
        } else {
            NodeList headerNodeList = document.getElementsByTagName(BS_HEAD)
                    .item(FIRST_ELEMENT)
                    .getChildNodes();
            for (int i = FIRST_ELEMENT; i < headerNodeList.getLength(); i++) {
                Node headerNode = headerNodeList.item(i);
                Node targetHeaderNode = targetDocument.importNode(headerNode, true);

                Element targetHeaderElement = (Element) targetDocument
                        .getElementsByTagName(BS_HEAD)
                        .item(FIRST_ELEMENT);
                targetHeaderElement.appendChild(targetHeaderNode);
            }

            NodeList documentNodeList = document.getElementsByTagName(DOCUMENTS)
                    .item(FIRST_ELEMENT)
                    .getChildNodes();

            for (int i = FIRST_ELEMENT; i < documentNodeList.getLength(); i++) {
                Node documentNode = documentNodeList.item(i);
                Node targetDocumentNode = targetDocument.importNode(documentNode, true);
                targetDocument.normalize();
                Element targetDocumentElement = (Element) targetDocument
                        .getElementsByTagName(DOCUMENTS)
                        .item(FIRST_ELEMENT);
                targetDocumentElement.appendChild(targetDocumentNode);
            }
        }
    }
}

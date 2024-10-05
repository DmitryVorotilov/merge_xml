package com.vpolosov.trainee.mergexml.service;

import com.vpolosov.trainee.mergexml.config.ConfigProperties;
import com.vpolosov.trainee.mergexml.dtos.StartValidationProcessDto;
import com.vpolosov.trainee.mergexml.dtos.ValidateDocumentDto;
import com.vpolosov.trainee.mergexml.dtos.ValidatedDocumentsDto;
import com.vpolosov.trainee.mergexml.utils.DocumentUtil;
import com.vpolosov.trainee.mergexml.utils.FileUtil;
import com.vpolosov.trainee.mergexml.validators.Validators;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.io.File;
import java.util.List;

import static com.vpolosov.trainee.mergexml.utils.Constant.FIRST_ELEMENT;
import static com.vpolosov.trainee.mergexml.utils.XmlTags.PAYER;

@Service
@RequiredArgsConstructor
public class ValidationService {

    /**
     * Логирование для пользователя.
     */
    private final Logger loggerForUser;

    /**
     * Валидаторы XML документа.
     */
    private final Validators validators;

    /**
     * Вспомогательный класс для работы с файлами.
     */
    private final FileUtil fileUtil;

    /**
     * Вспомогательный класс для работы с {@link Document}.
     */
    private final DocumentUtil documentUtil;

    /**
     * Свойства приложения.
     */
    private final ConfigProperties configProperties;

    /**
     *
     *
     * @param startValidationProcessDto путь до каталога с платёжными документами.
     * @return список проваледированных xml файлов.
     */
    public ValidatedDocumentsDto validate(StartValidationProcessDto startValidationProcessDto) {
        List<File> xmlFiles = fileUtil.listXml(
            startValidationProcessDto.path(),
            configProperties.getMinCountFiles(),
            configProperties.getMaxCountFiles()
        );
        File xsdFile = fileUtil.xsd(startValidationProcessDto.path());

        var payer = documentUtil.getValueByTagName(xmlFiles.get(FIRST_ELEMENT), PAYER);
        var validator = validators.createValidator(xsdFile);
        var documents = xmlFiles.stream()
            .map(documentUtil::parse)
            .filter(document -> validators.validateDocument(new ValidateDocumentDto(startValidationProcessDto.id(), document), validator, payer))
            .peek(document -> loggerForUser.info("Файл {} прошел проверку.", documentUtil.getFileName(document)))
            .toList();
        return ValidatedDocumentsDto.builder()
            .validationProcessId(startValidationProcessDto.id())
            .path(startValidationProcessDto.path())
            .documents(documents)
            .build();

    }
}

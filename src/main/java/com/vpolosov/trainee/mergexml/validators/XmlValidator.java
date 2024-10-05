package com.vpolosov.trainee.mergexml.validators;

import com.vpolosov.trainee.mergexml.aspect.Loggable;
import com.vpolosov.trainee.mergexml.dtos.ValidateDocumentDto;
import com.vpolosov.trainee.mergexml.handler.exception.IncorrectXmlFileException;
import com.vpolosov.trainee.mergexml.service.PublishValidationFileEvent;
import com.vpolosov.trainee.mergexml.utils.DocumentUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.util.function.BiPredicate;

/**
 * Валидатор XML документа по XSD схеме.
 *
 * @author Ali Takushinov
 * @author Andrei Stalybka
 * @author Maksim Litvinenko
 */
@Component
@RequiredArgsConstructor
public class XmlValidator implements BiPredicate<ValidateDocumentDto, Validator> {

    /**
     * Логирование для пользователя.
     */
    private final Logger loggerForUser;

    /**
     * Вспомогательный класс для работы с {@link Document}.
     */
    private final DocumentUtil documentUtil;

    /**
     * Публикация события ошибки валидации.
     */
    private final PublishValidationFileEvent publishValidationFileEvent;

    /**
     * {@inheritDoc}
     *
     * @throws IncorrectXmlFileException если файл не прошёл проверку XSD схемы.
     */
    @Loggable
    @Override
    public boolean test(ValidateDocumentDto validateDocumentDto, Validator validator) {
        try {
            validator.validate(new DOMSource(validateDocumentDto.document()));
        } catch (SAXException | IOException e) {
            publishValidationFileEvent.publishFailed(validateDocumentDto);
            var fileName = documentUtil.getFileName(validateDocumentDto.document());
            loggerForUser.error("Файл {} не прошел проверку.", fileName);
            throw new IncorrectXmlFileException("Invalid XML file with name: " + fileName);
        }
        publishValidationFileEvent.publishSuccess(validateDocumentDto);
        return true;
    }
}
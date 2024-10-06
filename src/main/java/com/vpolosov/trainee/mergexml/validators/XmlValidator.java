package com.vpolosov.trainee.mergexml.validators;

import com.vpolosov.trainee.mergexml.aspect.Loggable;
import com.vpolosov.trainee.mergexml.dtos.ValidateDocumentDto;
import com.vpolosov.trainee.mergexml.utils.DocumentUtil;
import com.vpolosov.trainee.mergexml.validators.api.BiValidation;
import com.vpolosov.trainee.mergexml.validators.api.ValidationContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Validator;
import java.io.IOException;

/**
 * Валидатор XML документа по XSD схеме.
 *
 * @author Ali Takushinov
 * @author Andrei Stalybka
 * @author Maksim Litvinenko
 */
@Component
@RequiredArgsConstructor
public class XmlValidator implements BiValidation<ValidateDocumentDto, Validator> {

    /**
     * Логирование для пользователя.
     */
    private final Logger loggerForUser;

    /**
     * Вспомогательный класс для работы с {@link Document}.
     */
    private final DocumentUtil documentUtil;

    @Loggable
    @Override
    public boolean validate(ValidateDocumentDto validateDocumentDto, Validator validator, ValidationContext context) {
        try {
            validator.validate(new DOMSource(validateDocumentDto.document()));
        } catch (SAXException | IOException e) {
            var fileName = documentUtil.getFileName(validateDocumentDto.document());
            loggerForUser.error("Файл {} не прошел проверку.", fileName);
            context.addMessage("Invalid XML file with name: " + fileName);
            return false;
        }
        return true;
    }
}
package com.vpolosov.trainee.mergexml.validators;

import com.vpolosov.trainee.mergexml.aspect.Loggable;
import com.vpolosov.trainee.mergexml.dtos.ValidateDocumentDto;
import com.vpolosov.trainee.mergexml.handler.exception.InvalidSchemaException;
import com.vpolosov.trainee.mergexml.handler.exception.ValidationException;
import com.vpolosov.trainee.mergexml.service.PublishValidationFileEvent;
import com.vpolosov.trainee.mergexml.validators.api.BiValidation;
import com.vpolosov.trainee.mergexml.validators.api.Validation;
import com.vpolosov.trainee.mergexml.validators.api.ValidationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.util.List;

/**
 * Объединяет все валидаторы XML документа в один класс.
 *
 * @author Maksim Litvinenko
 */
@Component
@RequiredArgsConstructor
public class Validators {

    /**
     * Список валидаторов с одним параметром для проверки XML файла.
     */
    private final List<Validation<ValidateDocumentDto>> singleParamValidators;

    /**
     * Валидатор для проверки одного плательщика.
     */
    private final BiValidation<String, ValidateDocumentDto> paymentValidator;

    /**
     * Валидатор для проверки XML файла по XSD схеме.
     */
    private final BiValidation<ValidateDocumentDto, Validator> xmlValidator;

    /**
     * Публикация события ошибки валидации.
     */
    private final PublishValidationFileEvent publishValidationFileEvent;

    /**
     * Прогоняет XML документ по всем валидаторам.
     *
     * @param validateDocumentDto документ для объединения платежа.
     * @param validator           проверяет схему документа платежа.
     * @param payer               плательщик.
     * @return true если все проверки прошли успешно иначе выбрасывает соответствующее исключение.
     */
    @Loggable
    public boolean validateDocument(ValidateDocumentDto validateDocumentDto, Validator validator, String payer) {
        var validationContext = new ValidationContext();

        if (!xmlValidator.validate(validateDocumentDto, validator, validationContext)) {
            publishValidationFileEvent.publishFailed(validateDocumentDto);
            throw new ValidationException(validationContext.messages());
        }

        paymentValidator.validate(payer, validateDocumentDto, validationContext);
        for (var singleParamValidate : singleParamValidators) {
            singleParamValidate.validate(validateDocumentDto, validationContext);
        }
        if (!validationContext.isEmpty()) {
            publishValidationFileEvent.publishFailed(validateDocumentDto, String.join(",", validationContext.failFields()));
            throw new ValidationException(validationContext.messages());
        }

        publishValidationFileEvent.publishSuccess(validateDocumentDto);
        return true;
    }

    /**
     * Создать {@link Validator} по схеме XSD файла.
     *
     * @param xsdFile схема XSD файла.
     * @return валидатор для проверки XML файлов.
     * @throws InvalidSchemaException когда не удалось создать схему по XSD файлу.
     */
    @Loggable
    public Validator createValidator(File xsdFile) {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaFile = new StreamSource(xsdFile);
        Schema schema;
        try {
            schema = factory.newSchema(schemaFile);
        } catch (SAXException e) {
            throw new InvalidSchemaException(e);
        }
        return schema.newValidator();
    }
}

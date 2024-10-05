package com.vpolosov.trainee.mergexml.validators;

import com.vpolosov.trainee.mergexml.aspect.Loggable;
import com.vpolosov.trainee.mergexml.config.ConfigProperties;
import com.vpolosov.trainee.mergexml.dtos.ValidateDocumentDto;
import com.vpolosov.trainee.mergexml.handler.exception.InvalidCurrencyCodeValueException;
import com.vpolosov.trainee.mergexml.service.PublishValidationFileEvent;
import com.vpolosov.trainee.mergexml.utils.DocumentUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.util.function.Predicate;

import static com.vpolosov.trainee.mergexml.utils.XmlTags.CURRCODE;

/**
 * Валидация валюты.
 *
 * @author Daria Koval
 */
@Component
@RequiredArgsConstructor
public class CurrentCodeValidator implements Predicate<ValidateDocumentDto> {

    /**
     * Свойства приложения.
     */
    private final ConfigProperties configProperties;

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
     * @throws InvalidCurrencyCodeValueException когда значение кода валюты не соответствует.
     */
    @Loggable
    @Override
    public boolean test(ValidateDocumentDto validateDocumentDto) {
        var currCode = documentUtil.getValueByTagName(validateDocumentDto.document(), CURRCODE);
        var validCurrCode = String.valueOf(configProperties.getCurrencyCode());
        if (!currCode.equals(validCurrCode)) {
            publishValidationFileEvent.publishFailed(validateDocumentDto, CURRCODE);
            throw new InvalidCurrencyCodeValueException("Допустимое значение кода валюты " + validCurrCode);
        }
        publishValidationFileEvent.publishSuccess(validateDocumentDto);
        return true;
    }
}

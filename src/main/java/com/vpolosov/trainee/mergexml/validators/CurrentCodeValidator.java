package com.vpolosov.trainee.mergexml.validators;

import com.vpolosov.trainee.mergexml.aspect.Loggable;
import com.vpolosov.trainee.mergexml.config.ConfigProperties;
import com.vpolosov.trainee.mergexml.dtos.ValidateDocumentDto;
import com.vpolosov.trainee.mergexml.utils.DocumentUtil;
import com.vpolosov.trainee.mergexml.validators.api.Validation;
import com.vpolosov.trainee.mergexml.validators.api.ValidationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import static com.vpolosov.trainee.mergexml.utils.XmlTags.CURRCODE;

/**
 * Валидация валюты.
 *
 * @author Daria Koval
 */
@Component
@RequiredArgsConstructor
public class CurrentCodeValidator implements Validation<ValidateDocumentDto> {

    /**
     * Свойства приложения.
     */
    private final ConfigProperties configProperties;

    /**
     * Вспомогательный класс для работы с {@link Document}.
     */
    private final DocumentUtil documentUtil;

    @Loggable
    @Override
    public boolean validate(ValidateDocumentDto validateDocumentDto, ValidationContext context) {
        var currCode = documentUtil.getValueByTagName(validateDocumentDto.document(), CURRCODE);
        var validCurrCode = String.valueOf(configProperties.getCurrencyCode());
        if (!currCode.equals(validCurrCode)) {
            context.addMessage("Допустимое значение кода валюты " + validCurrCode, CURRCODE);
            return false;
        }
        return true;
    }
}

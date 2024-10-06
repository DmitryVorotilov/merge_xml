package com.vpolosov.trainee.mergexml.validators;

import com.vpolosov.trainee.mergexml.aspect.Loggable;
import com.vpolosov.trainee.mergexml.dtos.ValidateDocumentDto;
import com.vpolosov.trainee.mergexml.utils.DocumentUtil;
import com.vpolosov.trainee.mergexml.validators.api.BiValidation;
import com.vpolosov.trainee.mergexml.validators.api.ValidationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import static com.vpolosov.trainee.mergexml.utils.XmlTags.PAYER;

/**
 * Проверяет что в XML документах один и тот же плательщик.
 *
 * @author Maksim Litvinenko
 */
@Component
@RequiredArgsConstructor
public class SinglePayerValidator implements BiValidation<String, ValidateDocumentDto> {

    /**
     * Вспомогательный класс для работы с {@link Document}.
     */
    private final DocumentUtil documentUtil;

    @Loggable
    @Override
    public boolean validate(String payer, ValidateDocumentDto validateDocumentDto, ValidationContext context) {
        var nextPayer = documentUtil.getValueByTagName(validateDocumentDto.document(), PAYER);
        if (!payer.equals(nextPayer)) {
            context.addMessage("Данные файлы не могут быть объединены, т.к. обнаружены разные плательщики", PAYER);
            return false;
        }
        return true;
    }
}

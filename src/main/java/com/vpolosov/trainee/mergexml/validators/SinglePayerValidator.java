package com.vpolosov.trainee.mergexml.validators;

import com.vpolosov.trainee.mergexml.aspect.Loggable;
import com.vpolosov.trainee.mergexml.dtos.ValidateDocumentDto;
import com.vpolosov.trainee.mergexml.handler.exception.DifferentPayerException;
import com.vpolosov.trainee.mergexml.service.PublishValidationFileEvent;
import com.vpolosov.trainee.mergexml.utils.DocumentUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.util.function.BiPredicate;

import static com.vpolosov.trainee.mergexml.utils.XmlTags.PAYER;

/**
 * Проверяет что в XML документах один и тот же плательщик.
 *
 * @author Maksim Litvinenko
 */
@Component
@RequiredArgsConstructor
public class SinglePayerValidator implements BiPredicate<String, ValidateDocumentDto> {

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
     * @throws DifferentPayerException когда разные плательщики.
     */
    @Loggable
    @Override
    public boolean test(String payer, ValidateDocumentDto validateDocumentDto) {
        var nextPayer = documentUtil.getValueByTagName(validateDocumentDto.document(), PAYER);
        if (!payer.equals(nextPayer)) {
            publishValidationFileEvent.publishFailed(validateDocumentDto, PAYER);
            throw new DifferentPayerException(
                "Данные файлы не могут быть объединены, т.к. обнаружены разные плательщики"
            );
        }
        publishValidationFileEvent.publishSuccess(validateDocumentDto);
        return true;
    }
}

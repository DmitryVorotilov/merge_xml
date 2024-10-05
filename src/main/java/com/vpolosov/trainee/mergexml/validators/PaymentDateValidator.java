package com.vpolosov.trainee.mergexml.validators;

import com.vpolosov.trainee.mergexml.aspect.Loggable;
import com.vpolosov.trainee.mergexml.dtos.ValidateDocumentDto;
import com.vpolosov.trainee.mergexml.handler.exception.IncorrectDateException;
import com.vpolosov.trainee.mergexml.service.PublishValidationFileEvent;
import com.vpolosov.trainee.mergexml.utils.DocumentUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

import static com.vpolosov.trainee.mergexml.utils.XmlTags.DOCUMENTDATE;

/**
 * Проверяет что текущая дата равна дате совершения платежа.
 * <p>
 * Использует дату создания документа, как то с чем будет происходить сравнение.
 *
 * @author Maksim Litvinenko
 */
@Component
@RequiredArgsConstructor
public class PaymentDateValidator implements Predicate<ValidateDocumentDto> {

    /**
     * Часы для корректировки времени.
     */
    private final Clock clock;

    /**
     * Парсер даты.
     */
    private final DateTimeFormatter localDateFormat;

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
     * @throws IncorrectDateException когда дата платежа не равна текущей дате.
     */
    @Loggable
    @Override
    public boolean test(ValidateDocumentDto validateDocumentDto) {
        var nowDate = LocalDate.now(clock);
        var dateStr = documentUtil.getValueByTagName(validateDocumentDto.document(), DOCUMENTDATE);
        var date = LocalDate.parse(dateStr, localDateFormat);
        if (!date.equals(nowDate)) {
            publishValidationFileEvent.publishFailed(validateDocumentDto, DOCUMENTDATE);
            throw new IncorrectDateException("Дата платежного документа должна быть равна текущей дате");
        }
        publishValidationFileEvent.publishSuccess(validateDocumentDto);
        return true;
    }
}

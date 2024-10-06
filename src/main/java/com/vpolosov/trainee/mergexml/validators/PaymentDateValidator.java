package com.vpolosov.trainee.mergexml.validators;

import com.vpolosov.trainee.mergexml.aspect.Loggable;
import com.vpolosov.trainee.mergexml.dtos.ValidateDocumentDto;
import com.vpolosov.trainee.mergexml.utils.DocumentUtil;
import com.vpolosov.trainee.mergexml.validators.api.Validation;
import com.vpolosov.trainee.mergexml.validators.api.ValidationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
public class PaymentDateValidator implements Validation<ValidateDocumentDto> {

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

    @Loggable
    @Override
    public boolean validate(ValidateDocumentDto validateDocumentDto, ValidationContext context) {
        var nowDate = LocalDate.now(clock);
        var dateStr = documentUtil.getValueByTagName(validateDocumentDto.document(), DOCUMENTDATE);
        var date = LocalDate.parse(dateStr, localDateFormat);
        if (!date.equals(nowDate)) {
            context.addMessage("Дата платежного документа должна быть равна текущей дате", DOCUMENTDATE);
            return false;
        }
        return true;
    }
}

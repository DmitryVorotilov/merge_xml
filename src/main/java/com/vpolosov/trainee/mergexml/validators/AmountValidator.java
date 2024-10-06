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

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.vpolosov.trainee.mergexml.utils.XmlTags.AMOUNT;

/**
 * Валидатор минимальной суммы платежа.
 *
 * @author Andrei Stalybka
 */
@Component
@RequiredArgsConstructor
public class AmountValidator implements Validation<ValidateDocumentDto> {

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
        var amountStr = documentUtil.getValueByTagName(validateDocumentDto.document(), AMOUNT);
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (Exception e) {
            context.addMessage("В файле %s не найдена сумма платежа или сумма некорректна"
                .formatted(documentUtil.getFileName(validateDocumentDto.document())), AMOUNT);
            return false;
        }
        if (amount.compareTo(configProperties.getMinPayment()) < BigInteger.ZERO.intValue()) {
            context.addMessage("В файле %s сумма платежа не соответствует минимальной"
                .formatted(documentUtil.getFileName(validateDocumentDto.document())), AMOUNT);
            return false;
        }
        if (amount.compareTo(configProperties.getMaxPayment()) > BigInteger.ZERO.intValue()) {
            context.addMessage("В файле %s сумма платежа не соответствует максимальной"
                .formatted(documentUtil.getFileName(validateDocumentDto.document())), AMOUNT);
            return false;
        }
        return true;
    }
}
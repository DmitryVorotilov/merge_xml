package com.vpolosov.trainee.mergexml.validators;

import com.vpolosov.trainee.mergexml.aspect.Loggable;
import com.vpolosov.trainee.mergexml.config.ConfigProperties;
import com.vpolosov.trainee.mergexml.dtos.ValidateDocumentDto;
import com.vpolosov.trainee.mergexml.handler.exception.IncorrectMaxAmountException;
import com.vpolosov.trainee.mergexml.handler.exception.IncorrectMinAmountException;
import com.vpolosov.trainee.mergexml.handler.exception.IncorrectValueException;
import com.vpolosov.trainee.mergexml.service.PublishValidationFileEvent;
import com.vpolosov.trainee.mergexml.utils.DocumentUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Predicate;

import static com.vpolosov.trainee.mergexml.utils.XmlTags.AMOUNT;

/**
 * Валидатор минимальной суммы платежа.
 *
 * @author Andrei Stalybka
 */
@Component
@RequiredArgsConstructor
public class AmountValidator implements Predicate<ValidateDocumentDto> {

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
     * @throws IncorrectValueException     если в файле не найдена сумма платежа или она некорректна.
     * @throws IncorrectMinAmountException если сумма платежа меньше минимально допустимой.
     * @throws IncorrectMaxAmountException если сумма платежа больше максимально допустимой.
     */
    @Loggable
    @Override
    public boolean test(ValidateDocumentDto validateDocumentDto) {
        var amountStr = documentUtil.getValueByTagName(validateDocumentDto.document(), AMOUNT);
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (Exception e) {
            publishValidationFileEvent.publishFailed(validateDocumentDto, AMOUNT);
            throw new IncorrectValueException(
                "В файле %s не найдена сумма платежа или сумма некорректна"
                    .formatted(documentUtil.getFileName(validateDocumentDto.document()))
            );
        }
        if (amount.compareTo(configProperties.getMinPayment()) < BigInteger.ZERO.intValue()) {
            publishValidationFileEvent.publishFailed(validateDocumentDto, AMOUNT);
            throw new IncorrectMinAmountException(
                "В файле %s сумма платежа не соответствует минимальной"
                    .formatted(documentUtil.getFileName(validateDocumentDto.document()))
            );
        }
        if (amount.compareTo(configProperties.getMaxPayment()) > BigInteger.ZERO.intValue()) {
            publishValidationFileEvent.publishFailed(validateDocumentDto, AMOUNT);
            throw new IncorrectMaxAmountException(
                "В файле %s сумма платежа не соответствует максимальной"
                    .formatted(documentUtil.getFileName(validateDocumentDto.document()))
            );
        }
        publishValidationFileEvent.publishSuccess(validateDocumentDto);
        return true;
    }
}
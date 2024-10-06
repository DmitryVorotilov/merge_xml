package com.vpolosov.trainee.mergexml.validators;

import com.vpolosov.trainee.mergexml.aspect.Loggable;
import com.vpolosov.trainee.mergexml.dtos.ValidateDocumentDto;
import com.vpolosov.trainee.mergexml.utils.DocumentUtil;
import com.vpolosov.trainee.mergexml.validators.api.Validation;
import com.vpolosov.trainee.mergexml.validators.api.ValidationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.util.regex.Pattern;

import static com.vpolosov.trainee.mergexml.utils.XmlTags.IP;

/**
 * Проверка IP адреса на соответствие IPv4.
 *
 * @author Maksim Litvinenko
 */
@Component
@RequiredArgsConstructor
public class IPv4Validator implements Validation<ValidateDocumentDto> {

    /**
     * Regexp паттерн для проверки IPv4.
     */
    private static final Pattern IPV4_REGEXP = Pattern.compile(
        "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}"
            + "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

    /**
     * Вспомогательный класс для работы с {@link Document}.
     */
    private final DocumentUtil documentUtil;

    @Loggable
    @Override
    public boolean validate(ValidateDocumentDto validateDocumentDto, ValidationContext context) {
        var ipv4 = documentUtil.getValueByTagName(validateDocumentDto.document(), IP);
        if (IPV4_REGEXP.matcher(ipv4).matches()) {
            return true;
        }
        context.addMessage("IP адрес не соответствует формату IPv4", IP);
        return false;
    }
}

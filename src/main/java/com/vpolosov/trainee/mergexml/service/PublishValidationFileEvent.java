package com.vpolosov.trainee.mergexml.service;

import com.vpolosov.trainee.mergexml.dtos.ValidateDocumentDto;
import com.vpolosov.trainee.mergexml.dtos.ValidationFileFailedEvent;
import com.vpolosov.trainee.mergexml.dtos.ValidationFileSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublishValidationFileEvent {

    /**
     * Публикатор событий приложения.
     */
    private final ApplicationEventPublisher eventPublisher;

    public void publishFailed(ValidateDocumentDto validateDocumentDto, String... failFields) {
        eventPublisher.publishEvent(ValidationFileFailedEvent.builder()
            .validationProcessId(validateDocumentDto.validationProcessId())
            .failFields(String.join(",", failFields))
            .document(validateDocumentDto.document())
            .build());
    }

    public void publishSuccess(ValidateDocumentDto validateDocumentDto) {
        eventPublisher.publishEvent(ValidationFileSuccessEvent.builder()
            .validationProcessId(validateDocumentDto.validationProcessId())
            .document(validateDocumentDto.document())
            .build());
    }
}

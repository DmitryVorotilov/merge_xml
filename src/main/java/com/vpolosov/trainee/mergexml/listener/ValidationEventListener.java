package com.vpolosov.trainee.mergexml.listener;

import com.vpolosov.trainee.mergexml.dtos.ValidationFileFailedEvent;
import com.vpolosov.trainee.mergexml.dtos.ValidationFileSuccessEvent;
import com.vpolosov.trainee.mergexml.model.ValidationFileHistory;
import com.vpolosov.trainee.mergexml.model.ValidationProcess;
import com.vpolosov.trainee.mergexml.repository.ValidationFileHistoryRepository;
import com.vpolosov.trainee.mergexml.utils.DocumentUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ValidationEventListener {

    private final DocumentUtil documentUtil;

    private final Clock clock;

    private final ValidationFileHistoryRepository validationFileHistoryRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    public void handle(ValidationFileSuccessEvent event) {
        var validationProcess = ValidationProcess.builder()
            .id(event.validationProcessId())
            .build();
        var fileHistory = ValidationFileHistory.builder()
            .validationId(UUID.randomUUID())
            .fileName(documentUtil.getFileName(event.document()))
            .docRef(event.document().getDocumentURI())
            .isSuccess(true)
            .failFields(null)
            .validationDate(LocalDateTime.now(clock))
            .validationProcess(validationProcess)
            .build();
        validationFileHistoryRepository.save(fileHistory);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    public void handle(ValidationFileFailedEvent event) {
        var validationProcess = ValidationProcess.builder()
            .id(event.validationProcessId())
            .build();
        var fileHistory = ValidationFileHistory.builder()
            .validationId(UUID.randomUUID())
            .fileName(documentUtil.getFileName(event.document()))
            .docRef(event.document().getDocumentURI())
            .isSuccess(false)
            .failFields(event.failFields())
            .validationDate(LocalDateTime.now(clock))
            .validationProcess(validationProcess)
            .build();
        validationFileHistoryRepository.save(fileHistory);
    }
}

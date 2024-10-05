package com.vpolosov.trainee.mergexml.service;

import com.vpolosov.trainee.mergexml.dtos.StartValidationProcessDto;
import com.vpolosov.trainee.mergexml.model.ValidationProcess;
import com.vpolosov.trainee.mergexml.repository.ValidationProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ValidationProcessService {

    private final ValidationProcessRepository validationProcessRepository;

    private final Clock clock;

    @Transactional
    public StartValidationProcessDto startValidationProcess(String path) {
        var validationProcess = validationProcessRepository.save(ValidationProcess.builder()
            .isSuccess(false)
            .dirRef(path)
            .totalDocRef(null)
            .validationProcessDate(LocalDateTime.now(clock))
            .build());
        return StartValidationProcessDto.builder()
            .id(validationProcess.getId())
            .path(validationProcess.getDirRef())
            .build();
    }

    @Transactional
    public void successValidationProcess(UUID id, Document total) {
        validationProcessRepository.updateToSuccess(id, total.getDocumentURI());
    }
}

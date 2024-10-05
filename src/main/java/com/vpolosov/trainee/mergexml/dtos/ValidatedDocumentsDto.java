package com.vpolosov.trainee.mergexml.dtos;

import lombok.Builder;
import org.w3c.dom.Document;

import java.util.List;
import java.util.UUID;

@Builder
public record ValidatedDocumentsDto(
    UUID validationProcessId,
    String path,
    List<Document> documents
) {
}

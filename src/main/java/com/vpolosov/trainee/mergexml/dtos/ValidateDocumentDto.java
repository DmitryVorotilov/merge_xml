package com.vpolosov.trainee.mergexml.dtos;

import org.w3c.dom.Document;

import java.util.UUID;

public record ValidateDocumentDto(UUID validationProcessId, Document document) {
}

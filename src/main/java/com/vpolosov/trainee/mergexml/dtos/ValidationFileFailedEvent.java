package com.vpolosov.trainee.mergexml.dtos;

import lombok.Builder;
import org.w3c.dom.Document;

import java.util.UUID;

@Builder
public record ValidationFileFailedEvent(
    UUID validationProcessId,
    String failFields,
    Document document
) {
}

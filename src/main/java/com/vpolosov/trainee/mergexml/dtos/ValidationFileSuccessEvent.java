package com.vpolosov.trainee.mergexml.dtos;

import lombok.Builder;
import org.w3c.dom.Document;

import java.util.UUID;

@Builder
public record ValidationFileSuccessEvent(
    UUID validationProcessId,
    Document document
) {
}

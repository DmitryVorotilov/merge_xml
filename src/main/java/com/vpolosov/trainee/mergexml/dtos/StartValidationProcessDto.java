package com.vpolosov.trainee.mergexml.dtos;

import lombok.Builder;

import java.util.UUID;

@Builder
public record StartValidationProcessDto(UUID id, String path) {
}

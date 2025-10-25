package com.rifushigi.nomisma.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ErrorResponseDTO(
        @NotBlank String message,
        @NotNull Object details,
        String timestamp
) {
}

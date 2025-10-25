package com.rifushigi.nomisma.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.Instant;

@Builder
public record CountryDTO(
    @NotBlank String name,
    String capital,
    String region,
    Long population,
    String currencyCode,
    Double exchangeRate,
    Long estimatedGdp,
    String flagUrl,
    Instant lastRefreshedAt
) { }

package com.rifushigi.nomisma.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record CountrySummaryResponseDTO(
        @JsonProperty("total_currencies") Long totalCountries,
        @JsonProperty("last_refreshed_at") Instant lastRefreshedAt
) { }

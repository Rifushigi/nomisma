package com.rifushigi.nomisma.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CountrySummaryResponseDTO(
        @JsonProperty("total_countries") Long totalCountries,
        @JsonProperty("last_refreshed_at") String lastRefreshedAt
) { }

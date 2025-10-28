package com.rifushigi.nomisma.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public record ExternalCountryDTO(
        String name,
        String capital,
        String region,
        long population,
        List<Currency> currencies,
        String flag
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Currency(String code) { }
}

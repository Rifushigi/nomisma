package com.rifushigi.nomisma.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExternalCountryDTO(
        String name,
        String capital,
        String region,
        long population,
        List<Currency> currencies,
        String flag
) {
    public record Currency(String code) { }
}

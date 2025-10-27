package com.rifushigi.nomisma.dto;

import java.util.List;

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

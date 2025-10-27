package com.rifushigi.nomisma.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record ExternalExchangeRateDTO(
    String result,
    @JsonProperty("base_code") String baseCode,
    Map<String, Double> rates
) { }

package com.rifushigi.nomisma.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExternalExchangeRateDTO(
    String result,
    @JsonProperty("base_code") String baseCode,
    Map<String, Double> rates
) { }

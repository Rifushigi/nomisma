package com.rifushigi.nomisma.service;

import com.rifushigi.nomisma.dto.ExternalCountryDTO;
import com.rifushigi.nomisma.dto.ExternalExchangeRateDTO;

import java.util.List;
import java.util.Map;

public interface ExternalApiService {
    List<ExternalCountryDTO> getCountries();
    ExternalExchangeRateDTO getExchangeRate();
}

package com.rifushigi.nomisma.service;

import java.util.Map;
import java.util.List;

public interface ExternalApiService {
    List<Map<String, Object>> getCountries();
    Map<String, Object> getExchangeRate();
}

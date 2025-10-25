package com.rifushigi.nomisma.service.impl;

import com.rifushigi.nomisma.service.ExternalApiService;
import com.rifushigi.nomisma.exception.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class ExternalApiServiceImpl implements ExternalApiService {

    private final RestClient countryApi;
    private final RestClient exchangeRateApi;

    @Override
    public List<Map<String, Object>> getCountries() {
        return countryApi.get()
                .uri("/all?fields=name,capital,region,population,flag,currencies")
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, _) -> {
                    throw new ServiceUnavailableException(
                            "External data source unavailable",
                            "Could not fetch data from " + request.getURI());
                })
                .onStatus(HttpStatusCode::is2xxSuccessful, (_, _) -> log.info("Successfully fetched countries"))
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
    }

    @Override
    public Map<String, Object> getExchangeRate() {
        return exchangeRateApi.get()
                .uri("/USD")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, _) -> {
                    throw new ServiceUnavailableException(
                            "External data source unavailable",
                            "Could not fetch data from " + request.getURI());
                })
                .onStatus(HttpStatusCode::is2xxSuccessful, (_, _) -> log.info("Successfully fetched the exchange rate"))
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}

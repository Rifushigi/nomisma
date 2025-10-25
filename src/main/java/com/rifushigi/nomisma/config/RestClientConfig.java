package com.rifushigi.nomisma.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient countryApi (RestClient.Builder builder){
        return builder
                .baseUrl("https://restcountries.com/v2")
                .build();
    }

    @Bean
    public RestClient exchangeRateApi (RestClient.Builder builder){
        return builder
                .baseUrl("https://open.er-api.com/v6/latest")
                .build();
    }
}

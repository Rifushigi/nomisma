package com.rifushigi.nomisma.service;

import com.rifushigi.nomisma.dto.CountryFilter;
import com.rifushigi.nomisma.dto.CountrySummaryResponse;
import com.rifushigi.nomisma.entity.Country;
import org.springframework.data.jpa.domain.Specification;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CountryService {
    List<Country> getAllCountries(CountryFilter filters);
    Country getCountryByName(String name);
    void deleteCountryByName(String name);
    CountrySummaryResponse getCountriesWithRefreshTimestamp();
    void refreshCountries();
    void generateSummaryImage(long totalCountries, List<String> top5ByGdp);
    File getSummaryImage();
}

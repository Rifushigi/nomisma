package com.rifushigi.nomisma.service;

import com.rifushigi.nomisma.dto.CountryFilterDTO;
import com.rifushigi.nomisma.dto.CountrySummaryResponseDTO;
import com.rifushigi.nomisma.entity.Country;
import com.rifushigi.nomisma.projection.CountryGdpProjection;

import java.io.File;
import java.util.List;

public interface CountryService {
    List<Country> getAllCountries(CountryFilterDTO filters);
    Country getCountryByName(String name);
    void deleteCountryByName(String name);
    CountrySummaryResponseDTO getCountriesWithRefreshTimestamp();
    void refreshCountries();
    void generateSummaryImage(long totalCountries, List<CountryGdpProjection> top5ByGdp, String timestamp);
    File getSummaryImage();
}

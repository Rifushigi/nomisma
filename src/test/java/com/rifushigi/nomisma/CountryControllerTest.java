package com.rifushigi.nomisma;

import com.rifushigi.nomisma.controller.CountryController;
import com.rifushigi.nomisma.dto.CountryFilterDTO;
import com.rifushigi.nomisma.dto.CountrySummaryResponseDTO;
import com.rifushigi.nomisma.entity.Country;
import com.rifushigi.nomisma.exception.GlobalExceptionHandler;
import com.rifushigi.nomisma.exception.NotFoundException;
import com.rifushigi.nomisma.service.impl.CountryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CountryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CountryServiceImpl countryService;

    @InjectMocks
    private CountryController countryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(countryController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Optional: for consistent JSON error responses
                .build();
    }

    @Test
    void testGetCountries_success() throws Exception {
        Country country = new Country();
        country.setName("Nigeria");
        country.setPopulation(200_000_000L);

        when(countryService.getAllCountries(any(CountryFilterDTO.class)))
                .thenReturn(List.of(country));

        mockMvc.perform(get("/countries").param("region", "Africa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Nigeria"))
                .andExpect(jsonPath("$[0].population").value(200_000_000));

        verify(countryService, times(1)).getAllCountries(any(CountryFilterDTO.class));
    }

    @Test
    void testGetCountryByName_success() throws Exception {
        Country country = new Country();
        country.setName("Nigeria");

        when(countryService.getCountryByName("Nigeria")).thenReturn(country);

        mockMvc.perform(get("/countries/Nigeria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nigeria"));

        verify(countryService, times(1)).getCountryByName("Nigeria");
    }

    @Test
    void testGetCountryByName_notFound() throws Exception {
        when(countryService.getCountryByName("Unknown"))
                .thenThrow(new NotFoundException("Country not found", null));

        mockMvc.perform(get("/countries/Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Country not found"));

        verify(countryService, times(1)).getCountryByName("Unknown");
    }

    @Test
    void testDeleteCountryByName_success() throws Exception {
        doNothing().when(countryService).deleteCountryByName("Nigeria");

        mockMvc.perform(delete("/countries/Nigeria"))
                .andExpect(status().isNoContent());

        verify(countryService, times(1)).deleteCountryByName("Nigeria");
    }

    @Test
    void testGetStatus_success() throws Exception {
        CountrySummaryResponseDTO summary = new CountrySummaryResponseDTO(250L, "2025-10-22T18:00:00Z");

        when(countryService.getCountriesWithRefreshTimestamp()).thenReturn(summary);

        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_countries").value(250))
                .andExpect(jsonPath("$.last_refreshed_at").value("2025-10-22T18:00:00Z"));
    }

    @Test
    void testGetSummaryImage_success() throws Exception {
        File dummyFile = File.createTempFile("summary", ".png");
        dummyFile.deleteOnExit();

        when(countryService.getSummaryImage()).thenReturn(dummyFile);

        mockMvc.perform(get("/countries/image"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "inline; filename=\"summary.png\""))
                .andExpect(content().contentType(MediaType.IMAGE_PNG));

        verify(countryService, times(1)).getSummaryImage();
    }

    @Test
    void testGetSummaryImage_fileNotFound() throws Exception {
        when(countryService.getSummaryImage())
                .thenThrow(new NotFoundException("Summary image not found", "Image not found"));

        mockMvc.perform(get("/countries/image"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Summary image not found"));

        verify(countryService, times(1)).getSummaryImage();
    }


    @Test
    void testRefresh_success() throws Exception {
        doNothing().when(countryService).refreshCountries();

        mockMvc.perform(post("/countries/refresh"))
                .andExpect(status().isNoContent());

        verify(countryService, times(1)).refreshCountries();
    }
}

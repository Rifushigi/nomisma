package com.rifushigi.nomisma.controller;

import com.rifushigi.nomisma.dto.CountryFilterDTO;
import com.rifushigi.nomisma.dto.CountrySummaryResponseDTO;
import com.rifushigi.nomisma.entity.Country;
import com.rifushigi.nomisma.exception.NotFoundException;
import com.rifushigi.nomisma.service.impl.CountryServiceImpl;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class CountryController {

    private final CountryServiceImpl countryService;

    @GetMapping("countries")
    public ResponseEntity<List<Country>> getCountries(CountryFilterDTO filters){
        List<Country> response = countryService.getAllCountries(filters);
        return ResponseEntity.ok(response);
    }


    @GetMapping(path = "countries/{name}")
    public ResponseEntity<Country> getCountryByName(@PathVariable("name") String name){
        Country response = countryService.getCountryByName(name);
        return ResponseEntity.ok(response);
    }

    @Validated
    @DeleteMapping(path = "countries/{name}")
    public ResponseEntity<Void> deleteCountryByName(
            @PathVariable("name")
            @NotBlank(message = "Country name must not be blank") String name){
        countryService.deleteCountryByName(name);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("status")
    public ResponseEntity<CountrySummaryResponseDTO> getStatus(){
        CountrySummaryResponseDTO response = countryService.getCountriesWithRefreshTimestamp();
        return ResponseEntity.ok(response);
    }

    @GetMapping("countries/image")
    public ResponseEntity<InputStreamSource> getSummaryImage() {
        File imageFile = countryService.getSummaryImage();
        InputStreamSource resource;
        try {
            resource = new InputStreamResource(new FileInputStream(imageFile));
        } catch (FileNotFoundException e) {
            throw new NotFoundException("Summary image not found", e.getMessage());
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"summary.png\"")
                .contentLength(imageFile.length())
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }

    @PostMapping("countries/refresh")
    public ResponseEntity<Void> refresh(){
        countryService.refreshCountries();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

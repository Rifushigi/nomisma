package com.rifushigi.nomisma.bootstrap;

import com.rifushigi.nomisma.repository.CountryRepository;
import com.rifushigi.nomisma.service.impl.CountryServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final CountryServiceImpl countryService;
    private final CountryRepository countryRepository;

    @Override
    public void run(String... args) {
        if (countryRepository.count() == 0) {
            log.info("Seeding countries...");
            countryService.fetchAllCountries();
            log.info("Database seeding completed!");
        } else {
            log.info("Database already contains data. Skipping seeding.");
        }
    }
}

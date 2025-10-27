package com.rifushigi.nomisma.service.impl;

import com.rifushigi.nomisma.dto.CountryFilterDTO;
import com.rifushigi.nomisma.dto.CountrySummaryResponseDTO;
import com.rifushigi.nomisma.dto.ExternalCountryDTO;
import com.rifushigi.nomisma.entity.Country;
import com.rifushigi.nomisma.exception.FieldValidationException;
import com.rifushigi.nomisma.exception.NotFoundException;
import com.rifushigi.nomisma.projection.CountryGdpProjection;
import com.rifushigi.nomisma.repository.CountryRepository;
import com.rifushigi.nomisma.service.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final ExternalApiServiceImpl externalApiService;
    private final AppMetadataServiceImpl metadataService;
    private final CountryRepository countryRepository;

    @Override
    public List<Country> getAllCountries(CountryFilterDTO filters) {
        Specification<Country> spec = buildSpecification(filters);
        Sort sort = buildSort(filters);
        return countryRepository.findAll(spec, sort);
    }

    private Specification<Country> buildSpecification(CountryFilterDTO filters) {
        Specification<Country> spec = Specification.unrestricted();

        if (filters.currency() != null && !filters.currency().isBlank()) {
            spec = spec.and((root, _, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("currency"), filters.currency()));
        }

        if (filters.region() != null && !filters.region().isBlank()) {
            spec = spec.and((root, _, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("region"), filters.region()));
        }

        return spec;
    }

    private Sort buildSort(CountryFilterDTO filters) {
        if (filters.sort() == null || filters.sort().isBlank()) {
            return Sort.unsorted();
        }

        String[] sortInfo = filters.sort().split("_");
        String sortDirection = sortInfo[1];
        String sortBy = sortInfo[0];

        if (sortDirection.equalsIgnoreCase("desc")) {
            return Sort.by(sortBy).descending();
        } else {
            return Sort.by(sortBy).ascending();
        }
    }

    @Override
    public Country getCountryByName(String name) {
        Country country = countryRepository.getCountryByName(name).orElseThrow(() -> new NotFoundException(
                "Resource not found",
                "Country with name '" + name + "' does not exist"
        ));

        Map<String, String> invalidFields = new HashMap<>();

        if (country.getPopulation() == null) {
            invalidFields.put("population", "Field 'population' is missing");
        }
        if (country.getCurrencyCode() == null || country.getCurrencyCode().trim().isEmpty()) {
            invalidFields.put("currency_code", "Field 'currency_code' is missing or empty");
        }

        if (!invalidFields.isEmpty()) {
            throw new FieldValidationException("Invalid country data", invalidFields);
        }

        return country;
    }

    @Override
    public void deleteCountryByName(String name) {
        long deletedCount = countryRepository.deleteCountryByName(name);
        if (deletedCount == 0) {
            throw new NotFoundException("Failed to delete country", "No country found with name: " + name);
        }
    }

    @Override
    public CountrySummaryResponseDTO getCountriesWithRefreshTimestamp() {
        Long countriesCount = countryRepository.count();
        Instant lastRefreshed = metadataService.getLastRefreshedAt();

        return new CountrySummaryResponseDTO(countriesCount, lastRefreshed.toString());
    }

    @Override
    public void generateSummaryImage(long totalCountries, List<CountryGdpProjection> top5ByGdp, String timestamp) {
        int width = 800;
        int height = 600;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        // Enable antialiasing for smoother text and shapes
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g.setColor(new Color(244, 246, 248)); // light gray-blue
        g.fillRect(0, 0, width, height);

        // Title
        g.setColor(new Color(26, 26, 26));
        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        g.drawString("Country Summary", 40, 60);

        // Metadata section
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        g.setColor(new Color(95, 106, 106));
        g.drawString("Last Refreshed: " + timestamp, 40, 100);
        g.drawString("Total Countries: " + totalCountries, 40, 130);

        // Divider line
        g.setColor(new Color(224, 224, 224));
        g.fillRect(40, 150, width - 80, 2);

        // Subtitle
        g.setColor(new Color(26, 26, 26));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("Top 5 Countries by Estimated GDP", 40, 190);

        // Chart area setup
        int barStartY = 230;
        int barHeight = 35;
        double maxGdp = top5ByGdp.stream()
                .mapToDouble(p -> p.getEstimatedGdp() != null ? p.getEstimatedGdp() : 0)
                .max()
                .orElse(1);

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

        for (int i = 0; i < top5ByGdp.size(); i++) {
            CountryGdpProjection p = top5ByGdp.get(i);
            String name = p.getName();
            double gdp = p.getEstimatedGdp() != null ? p.getEstimatedGdp() : 0;

            int y = barStartY + (i * 70);

            // Country name
            g.setFont(new Font("SansSerif", Font.PLAIN, 18));
            g.setColor(new Color(26, 26, 26));
            g.drawString(name, 40, y);

            String formattedGdp = formatGdp(gdp, numberFormat);

            // GDP value
            g.setFont(new Font("SansSerif", Font.PLAIN, 16));
            g.setColor(new Color(95, 106, 106));
            g.drawString(formattedGdp, width - 150, y);

            // Bar background
            g.setColor(new Color(224, 224, 224));
            g.fill(new RoundRectangle2D.Double(200, y - 20, 500, barHeight, 10, 10));

            // Bar fill (blue gradient)
            double ratio = gdp / maxGdp;
            int barWidth = (int) (500 * ratio);
            GradientPaint gradient = new GradientPaint(
                    200, y - 20, new Color(0, 123, 255),
                    200 + barWidth, y - 20, new Color(0, 198, 255)
            );
            g.setPaint(gradient);
            g.fill(new RoundRectangle2D.Double(200, y - 20, barWidth, barHeight, 10, 10));
        }

        // Footer
        g.setFont(new Font("SansSerif", Font.ITALIC, 14));
        g.setColor(new Color(158, 158, 158));
        g.drawString("Generated automatically by Nomisma API", 40, height - 40);

        g.dispose();

        try {
            File cacheDir = new File("cache");
            if (!cacheDir.exists()) {
                boolean created = cacheDir.mkdirs();
                if (!created) {
                    log.warn("Could not create cache directory at {}", cacheDir.getAbsolutePath());
                }
            }
            ImageIO.write(image, "png", new File("cache/summary.png"));
        } catch (IOException e) {
            log.error("An error occurred while trying to save the image: {}", e.getMessage());
        }
    }

    private String formatGdp(Double gdp, NumberFormat formatter) {
        if (gdp >= 1_000_000_000_000.0) {
            return formatter.format(gdp / 1_000_000_000_000.0) + " T";
        } else if (gdp >= 1_000_000_000.0) {
            return formatter.format(gdp / 1_000_000_000.0) + " B";
        } else if (gdp >= 1_000_000.0) {
            return formatter.format(gdp / 1_000_000.0) + " M";
        } else {
            return formatter.format(gdp);
        }
    }

    @Override
    public File getSummaryImage() {
        File imageFile = new File("cache/summary.png");
        if (imageFile.exists() && imageFile.isFile()) {
            return imageFile;
        } else {
            throw new NotFoundException(
                    "Summary image not found",
                    "No cached summary image was found at " + imageFile.getAbsolutePath()
            );
        }
    }

    @Override
    public void refreshCountries() {
        fetchAllCountries();
        long totalCountries = countryRepository.count();
        String lastRefreshed = metadataService.getLastRefreshedAt().toString();
        List<CountryGdpProjection> topFiveByGdp = countryRepository.findTop5ByOrderByEstimatedGdpDesc();
        generateSummaryImage(totalCountries, topFiveByGdp, lastRefreshed);
    }

    public void fetchAllCountries() {
        List<ExternalCountryDTO> countries = externalApiService.getCountries();
        Map<String, Double> exchangeRate = externalApiService.getExchangeRate().rates();
        Instant now = Instant.now();
        List<Country> data = new ArrayList<>();

        Map<String, Country> existingCountries = countryRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        c -> c.getName().toLowerCase(),
                            c -> c
                ));

        for (ExternalCountryDTO xCountry : countries) {
            String countryName = xCountry.name().toLowerCase();
            Country country = existingCountries.getOrDefault(countryName, new Country());

            country.setName(xCountry.name());
            country.setPopulation(xCountry.population());
            country.setFlagUrl(xCountry.flag());
            country.setRegion(xCountry.region());
            country.setCapital(xCountry.capital());
            country.setLastRefreshedAt(now);

            if (xCountry.currencies() == null) {
                country.setCurrencyCode(null);
                country.setExchangeRate(null);
                country.setEstimatedGdp(0.0);
                data.add(country);
                continue;
            }

            String currency = xCountry.currencies().getFirst().code().toUpperCase();
            country.setCurrencyCode(currency);

            if (!exchangeRate.containsKey(currency) || exchangeRate.get(currency) == null){
                country.setExchangeRate(null);
                country.setEstimatedGdp(null);
                data.add(country);
                continue;
            }

            Double rate = exchangeRate.get(currency);
            double randomMultiplier = (Math.random() * 1001) + 1000;
            Double eGdp = (xCountry.population() * randomMultiplier) / rate;

            country.setExchangeRate(rate);
            country.setEstimatedGdp(eGdp);

            data.add(country);
        }

        metadataService.updateLastRefreshedAt(now);
        countryRepository.saveAll(data);
    }
}
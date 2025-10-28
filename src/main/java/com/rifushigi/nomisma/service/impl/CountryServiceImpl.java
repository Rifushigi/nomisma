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
import jakarta.transaction.Transactional;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
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
                    criteriaBuilder.equal(root.get("currencyCode"), filters.currency()));
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
        if (sortInfo.length != 2) {
            return Sort.unsorted();
        }
        String sortDirection = sortInfo[1];
        String entityField = getEntityField(sortInfo);

        // Validate the field exists on the entity to prevent 500 s
        try {
            Country.class.getDeclaredField(entityField);
        } catch (NoSuchFieldException e) {
            return Sort.unsorted();
        }

        if (sortDirection.equalsIgnoreCase("desc")) {
            return Sort.by(entityField).descending();
        } else {
            return Sort.by(entityField).ascending();
        }
    }

    private static String getEntityField(String[] sortInfo) {
        String sortBy = sortInfo[0];

        // Map API field names to entity fields
        Map<String, String> sortFieldMap = new HashMap<>();
        sortFieldMap.put("name", "name");
        sortFieldMap.put("population", "population");
        sortFieldMap.put("currency_code", "currencyCode");
        sortFieldMap.put("exchange_rate", "exchangeRate");
        sortFieldMap.put("estimated_gdp", "estimatedGdp");
        sortFieldMap.put("gdp", "estimatedGdp");

        return sortFieldMap.getOrDefault(sortBy, sortBy);
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
        if (country.getExchangeRate() == null) {
            invalidFields.put("exchange_rate", "Field 'exchange_rate' is missing");
        }
        if (country.getEstimatedGdp() == null) {
            invalidFields.put("estimated_gdp", "Field 'estimated_gdp' is missing");
        }

        if (!invalidFields.isEmpty()) {
            throw new FieldValidationException("Invalid country data", invalidFields);
        }

        return country;
    }

    @Transactional
    @Override
    public void deleteCountryByName(String name) {
        long deletedCount = countryRepository.deleteByName(name);
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
        int width = 900;
        int height = 700;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        // Enable antialiasing for smoother text and shapes
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Background with subtle gradient
        GradientPaint bgGradient = new GradientPaint(
                0, 0, new Color(250, 251, 252),
                0, height, new Color(244, 246, 248)
        );
        g.setPaint(bgGradient);
        g.fillRect(0, 0, width, height);

        // Header section with colored accent
        g.setColor(new Color(59, 130, 246)); // blue accent
        g.fillRect(0, 0, width, 8);

        // Title
        g.setColor(new Color(15, 23, 42)); // darker for better contrast
        g.setFont(new Font("SansSerif", Font.BOLD, 36));
        g.drawString("Country Summary", 50, 80);

        // Metadata section with icons (using simple shapes)
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g.setColor(new Color(100, 116, 139));

        // Clock icon simulation
        g.fillOval(50, 110, 4, 4);
        g.drawString("Last Refreshed: " + timestamp, 65, 120);

        // Globe icon simulation
        g.fillOval(50, 140, 4, 4);
        g.drawString("Total Countries: " + totalCountries, 65, 150);

        // Card container for top 5
        g.setColor(Color.WHITE);
        g.fill(new RoundRectangle2D.Double(30, 190, width - 60, 450, 20, 20));

        // Subtle shadow for card
        g.setColor(new Color(0, 0, 0, 8));
        g.fill(new RoundRectangle2D.Double(32, 192, width - 64, 450, 20, 20));

        // Subtitle
        g.setColor(new Color(15, 23, 42));
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        g.drawString("Top 5 Countries by Estimated GDP", 50, 235);

        // Divider line under subtitle
        g.setColor(new Color(226, 232, 240));
        g.fillRect(50, 250, width - 100, 2);

        // Chart area setup
        int barStartY = 300;
        int barSpacing = 75;
        int barHeight = 40;
        double maxGdp = top5ByGdp.stream()
                .mapToDouble(p -> p.getEstimatedGdp() != null ? p.getEstimatedGdp() : 0)
                .max()
                .orElse(1);

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

        // Professional monochromatic color scheme (shades of blue)
        Color[] barColors = {
                new Color(37, 99, 235),    // primary blue
                new Color(59, 130, 246),   // lighter blue
                new Color(96, 165, 250),   // even lighter
                new Color(147, 197, 253),  // lighter still
                new Color(191, 219, 254)   // lightest blue
        };

        for (int i = 0; i < top5ByGdp.size(); i++) {
            CountryGdpProjection p = top5ByGdp.get(i);
            String name = p.getName();
            double gdp = p.getEstimatedGdp() != null ? p.getEstimatedGdp() : 0;

            int y = barStartY + (i * barSpacing);

            // Rank badge
            g.setColor(new Color(241, 245, 249));
            g.fillOval(50, y - 25, 35, 35);
            g.setColor(barColors[i]);
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            String rank = String.valueOf(i + 1);
            FontMetrics fm = g.getFontMetrics();
            int rankX = 50 + (35 - fm.stringWidth(rank)) / 2;
            int rankY = y - 25 + ((35 - fm.getHeight()) / 2) + fm.getAscent();
            g.drawString(rank, rankX, rankY);

            // Country name
            g.setFont(new Font("SansSerif", Font.BOLD, 17));
            g.setColor(new Color(30, 41, 59));
            g.drawString(name, 100, y - 5);

            // GDP value with better positioning
            String formattedGdp = formatGdp(gdp, numberFormat);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.setColor(new Color(71, 85, 105));
            FontMetrics valueFm = g.getFontMetrics();
            int valueWidth = valueFm.stringWidth(formattedGdp);
            g.drawString(formattedGdp, width - 80 - valueWidth, y - 5);

            // Bar background with rounded corners
            g.setColor(new Color(241, 245, 249));
            g.fill(new RoundRectangle2D.Double(100, y + 5, width - 200, barHeight, barHeight, barHeight));

            // Bar fill with a single color
            double ratio = gdp / maxGdp;
            int barWidth = (int) ((width - 200) * ratio);

            if (barWidth > 0) {
                // Gradient for depth
                GradientPaint barGradient = new GradientPaint(
                        100, y + 5, barColors[i],
                        100, y + 5 + barHeight, adjustBrightness(barColors[i])
                );
                g.setPaint(barGradient);
                g.fill(new RoundRectangle2D.Double(100, y + 5, barWidth, barHeight, barHeight, barHeight));

                // Add a subtle highlight
                g.setColor(new Color(255, 255, 255, 40));
                g.fill(new RoundRectangle2D.Double(100, y + 5, barWidth, (double) barHeight / 2, barHeight, barHeight));
            }
        }

        // Footer with better styling
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.setColor(new Color(148, 163, 184));
        String footerText = "Generated automatically by Nomisma API";
        FontMetrics footerFm = g.getFontMetrics();
        int footerX = (width - footerFm.stringWidth(footerText)) / 2;
        g.drawString(footerText, footerX, height - 35);

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

    private Color adjustBrightness(Color color) {
        return new Color(
                Math.max(0, Math.min(255, (int)(color.getRed() * (float) 0.85))),
                Math.max(0, Math.min(255, (int)(color.getGreen() * (float) 0.85))),
                Math.max(0, Math.min(255, (int)(color.getBlue() * (float) 0.85))),
                color.getAlpha()
        );
    }

    private String formatGdp(Double gdp, NumberFormat formatter) {
        if (gdp >= 1_000_000_000_000.0) {
            return "$" + formatter.format(gdp / 1_000_000_000_000.0) + "T";
        } else if (gdp >= 1_000_000_000.0) {
            return "$" + formatter.format(gdp / 1_000_000_000.0) + "B";
        } else if (gdp >= 1_000_000.0) {
            return "$" + formatter.format(gdp / 1_000_000.0) + "M";
        } else {
            return "$" + formatter.format(gdp);
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
    @Transactional
    public void refreshCountries() {
        log.info("Refresh::started");
        fetchAllCountries();
        log.info("Refresh::Done refreshing");
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

        log.info("Fetch::start looping through countries");
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
                country.setEstimatedGdp(BigDecimal.valueOf(0));
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

            double rate = exchangeRate.get(currency);
            double randomMultiplier = (Math.random() * 1001) + 1000;
            double computeEGdp = (xCountry.population() * randomMultiplier) / rate;
            BigDecimal eGdp = BigDecimal.valueOf(computeEGdp).setScale(0, RoundingMode.HALF_UP);

            country.setExchangeRate(
                    BigDecimal.valueOf(exchangeRate.get(currency)).setScale(2, RoundingMode.HALF_UP)
            );
            country.setEstimatedGdp(eGdp);

            data.add(country);
        }

        metadataService.updateLastRefreshedAt(now);
        countryRepository.saveAll(data);
    }
}
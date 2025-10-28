package com.rifushigi.nomisma.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    private String capital;

    private String region;

    @Column(nullable = false)
    private Long population;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;

    @Column(name = "estimated_gdp", precision = 20, scale = 2)
    private BigDecimal estimatedGdp;

    @Column(name = "flag_url")
    private String flagUrl;

    @LastModifiedDate
    private Instant lastRefreshedAt;
}

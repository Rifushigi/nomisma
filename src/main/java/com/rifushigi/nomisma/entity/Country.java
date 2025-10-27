package com.rifushigi.nomisma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;

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

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "exchange_rate", nullable = false)
    private Double exchangeRate;

    @Column(name = "estimated_gdp", nullable = false)
    private Double estimatedGdp;

    @Column(name = "flag_url")
    private String flagUrl;

    @LastModifiedDate
    private Instant lastRefreshedAt;
}

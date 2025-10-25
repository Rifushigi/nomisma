package com.rifushigi.nomisma.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
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
    private Long estimatedGdp;

    @Column(name = "flag_url")
    private String flagUrl;

    @LastModifiedDate
    private LocalDateTime lastRefreshedAt;
}

package com.rifushigi.nomisma.repository;

import com.rifushigi.nomisma.entity.Country;
import com.rifushigi.nomisma.projection.CountryGdpProjection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, UUID> {

    List<Country> findAll(Specification<Country> spec, Sort sort);

    Optional<Country> getCountryByName(String name);

    long deleteByName(String name);

    List<CountryGdpProjection> findTop5ByOrderByEstimatedGdpDesc();
}

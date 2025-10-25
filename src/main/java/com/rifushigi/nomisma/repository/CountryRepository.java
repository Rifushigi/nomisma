package com.rifushigi.nomisma.repository;

import com.rifushigi.nomisma.entity.Country;
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

    long deleteCountryByName(String name);

    @Query("from Country c select max(c.lastRefreshedAt)")
    Instant findLastRefreshedAt();
}

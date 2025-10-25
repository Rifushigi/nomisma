package com.rifushigi.nomisma.repository;

import com.rifushigi.nomisma.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CountryRepository extends JpaRepository<Country, UUID> {

}

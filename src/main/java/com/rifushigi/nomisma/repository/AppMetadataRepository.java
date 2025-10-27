package com.rifushigi.nomisma.repository;

import com.rifushigi.nomisma.entity.AppMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppMetadataRepository extends JpaRepository<AppMetadata, Long> {
    Optional<AppMetadata> findByKey(String lastRefreshedAt);
}

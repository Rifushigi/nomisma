package com.rifushigi.nomisma.service.impl;

import com.rifushigi.nomisma.entity.AppMetadata;
import com.rifushigi.nomisma.repository.AppMetadataRepository;
import com.rifushigi.nomisma.service.AppMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AppMetadataServiceImpl implements AppMetadataService {

    private final AppMetadataRepository metadataRepository;


    @Override
    public void updateLastRefreshedAt(Instant timestamp) {
        AppMetadata metadata = metadataRepository
                .findByMetaKey("last_refreshed_at")
                .orElseGet(() -> {
                    AppMetadata md = new AppMetadata();
                    md.setMetaKey("last_refreshed_at");
                    return md;
                });

        metadata.setMetaValue(timestamp.toString());
        metadata.setUpdatedAt(timestamp);
        metadataRepository.save(metadata);
    }

    @Override
    public Instant getLastRefreshedAt() {
        return metadataRepository.findByMetaKey("last_refreshed_at")
                .map(m -> Instant.parse(m.getMetaValue()))
                .orElse(null);
    }
}

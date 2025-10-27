package com.rifushigi.nomisma.service;

import java.time.Instant;

public interface AppMetadataService {
    void updateLastRefreshedAt(Instant timestamp);
    Instant getLastRefreshedAt();
}

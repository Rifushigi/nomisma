package com.rifushigi.nomisma.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "app_metadata")
public class AppMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, name = "meta_key")
    private String metaKey;

    @Column(name = "meta_value")
    private String metaValue;

    @Column(name = "updated_at")
    private Instant updatedAt;
}

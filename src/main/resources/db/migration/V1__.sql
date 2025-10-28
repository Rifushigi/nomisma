CREATE TABLE app_metadata
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    meta_key   VARCHAR(255) NOT NULL,
    meta_value VARCHAR(255) NULL,
    updated_at datetime NULL,
    CONSTRAINT pk_app_metadata PRIMARY KEY (id)
);

CREATE TABLE country
(
    id                VARCHAR(255) NOT NULL,
    name              VARCHAR(255) NOT NULL,
    capital           VARCHAR(255) NULL,
    region            VARCHAR(255) NULL,
    population        BIGINT       NOT NULL,
    currency_code     VARCHAR(255) NULL,
    exchange_rate     DECIMAL NULL,
    estimated_gdp     DECIMAL NULL,
    flag_url          VARCHAR(255) NULL,
    last_refreshed_at datetime NULL,
    CONSTRAINT pk_country PRIMARY KEY (id)
);

ALTER TABLE app_metadata
    ADD CONSTRAINT uc_app_metadata_meta_key UNIQUE (meta_key);
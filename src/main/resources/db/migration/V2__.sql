CREATE TABLE country
(
    id                VARCHAR(255) NOT NULL,
    name              VARCHAR(255) NOT NULL,
    capital           VARCHAR(255) NULL,
    region            VARCHAR(255) NULL,
    population        BIGINT       NOT NULL,
    currency_code     VARCHAR(255) NULL,
    exchange_rate     DECIMAL NULL,
    estimated_gdp     DECIMAL(20, 2) NULL,
    flag_url          VARCHAR(255) NULL,
    last_refreshed_at datetime NULL,
    CONSTRAINT pk_country PRIMARY KEY (id)
);
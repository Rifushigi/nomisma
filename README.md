# Nomisma

A RESTful API that fetches country data from an external API, stores it in a database, and provides CRUD operations. The application aggregates country information including GDP estimates, exchange rates, and generates visual summary reports.

## Features

- **Country Data Management**: Fetch, store, and manage comprehensive country information
- **Exchange Rate Integration**: Retrieve and store currency exchange rates
- **GDP Estimation**: Calculate estimated GDP based on country data
- **Visual Summaries**: Generate and serve summary images with country statistics
- **Auto-Seeding**: Automatically populates the database with country data on first run
- **Flexible Filtering**: Query countries by name, region, currency, and other criteria
- **Data Refresh**: Manual refresh endpoint to update country data from external APIs

## Tech Stack

- **Java 25** with Spring Boot 3.5.7
- **MySQL** database for data persistence
- **H2** database for testing
- **Flyway** for database migrations
- **Spring Data JPA** for data access
- **Spring Web** for REST API
- **Lombok** for reduced boilerplate code
- **Docker** for containerization

## Prerequisites

- Java 25
- Maven 3.x
- MySQL 8.x
- Docker (optional, for containerized deployment)
- External API access for country and exchange rate data

## Getting Started

### Configuration

Create a `.env` file in the root directory with the following environment variables:

```properties
PROFILE=default
PORT=8080
DB_URL=jdbc:mysql://localhost:3306/nomisma
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

### Running Locally

1. **Clone the repository**
   ```bash
   git clone https://github.com/Rifushigi/nomisma
   cd nomisma
   ```

2. **Configure environment variables**
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

3. **Start MySQL database**
   ```bash
   # Using Docker
   docker run --name nomisma-mysql \
     -e MYSQL_ROOT_PASSWORD=rootpassword \
     -e MYSQL_DATABASE=nomisma \
     -p 3306:3306 \
     -d mysql:8.0
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

   Or using Maven directly:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

### Running with Docker

Build and run the application using Docker:

```bash
# Build the Docker image
docker build -t nomisma .

# Run the container
docker run -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/nomisma \
  -e DB_USERNAME=your_username \
  -e DB_PASSWORD=your_password \
  nomisma
```

For development with Docker Compose (create a `docker-compose.yml` if needed):

```bash
docker-compose up -d
```

## API Endpoints

### Countries

#### Get All Countries
```http
GET /countries
```
Query Parameters:
- `name` - Filter by country name
- `region` - Filter by region
- `currencyCode` - Filter by currency code

**Response:**
```json
[
  {
    "id": "uuid",
    "name": "United States",
    "capital": "Washington D.C.",
    "region": "Americas",
    "population": 331000000,
    "currencyCode": "USD",
    "exchangeRate": 1.00,
    "estimatedGdp": 25000000000000.00,
    "flagUrl": "https://flagcdn.com/us.svg",
    "lastRefreshedAt": "2024-01-15T10:30:00Z"
  }
]
```

#### Get Country by Name
```http
GET /countries/{name}
```

#### Delete Country
```http
DELETE /countries/{name}
```

### Status & Summary

#### Get Application Status
```http
GET /status
```
Returns a summary of countries with refresh timestamp.

**Response:**
```json
{
  "totalCountries": 250,
  "lastRefreshTime": "2024-01-15T10:30:00Z"
}
```

#### Get Summary Image
```http
GET /countries/image
```
Returns a PNG image containing a summary of country statistics.

### Data Management

#### Refresh Country Data
```http
POST /countries/refresh
```
Fetches fresh country data from external APIs and updates the database.

## Development

### Project Structure

```
nomisma/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/rifushigi/nomisma/
│   │   │       ├── bootstrap/          # Database seeding
│   │   │       ├── config/             # Configuration classes
│   │   │       ├── controller/         # REST controllers
│   │   │       ├── dto/                # Data Transfer Objects
│   │   │       ├── entity/             # JPA entities
│   │   │       ├── exception/          # Exception handlers
│   │   │       ├── projection/         # Query projections
│   │   │       ├── repository/         # Data repositories
│   │   │       └── service/            # Business logic
│   │   └── resources/
│   │       ├── application.properties   # Application config
│   │       └── db/migration/            # Flyway migrations
│   └── test/                            # Test files
├── Dockerfile
├── pom.xml
└── README.md
```

### Building the Project

```bash
./mvnw clean package
```

### Running Tests

```bash
./mvnw test
```

### Database Migrations

Flyway automatically runs migrations on application startup. Migration files are located in `src/main/resources/db/migration/`.

## Database Schema

### Country Table
- `id` (UUID) - Primary key
- `name` - Country name
- `capital` - Capital city
- `region` - Geographic region
- `population` - Population count
- `currency_code` - Currency ISO code
- `exchange_rate` - Currency exchange rate
- `estimated_gdp` - Estimated GDP
- `flag_url` - Flag image URL
- `last_refreshed_at` - Last refresh timestamp

### App Metadata Table
- Stores application-level metadata and configuration

## Configuration

The application uses Spring profiles and property files. Configuration can be provided through:

1. `.env` file in the project root
2. Environment variables
3. `application.properties`

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the terms specified in the LICENSE file.

## Support

For help and support, please open an issue in the repository.


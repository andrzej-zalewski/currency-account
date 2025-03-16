# Currency Account Application

## Overview
The **Currency Account Application** is a Spring Boot 3.x application designed to manage personal currency accounts with support for currency exchange. It allows users to create accounts with a base currency (default PLN) and a target currency, retrieve account details, and perform currency exchanges using real-time exchange rates from the National Bank of Poland (NBP) API. The application is built using a **hexagonal architecture** (Ports and Adapters pattern), ensuring a clear separation of business logic from infrastructure details, making it modular, testable, and adaptable to future changes.

## Purpose
The primary purpose of this application is to provide a simple yet robust system for managing currency accounts and performing exchanges between a base currency (configurable, default PLN) and a user-selected target currency. It leverages the NBP API for exchange rates and persists data in an H2 database. The application is intended as a demonstration of modern software engineering practices, including hexagonal architecture, RESTful API design, and integration with external services via FeignClient.

## Features
- Create a new account with a specified target currency and initial balance in the base currency.
- Retrieve account details by ID.
- Exchange currency between the account's base and target currencies using real-time NBP rates.
- REST API documented with Swagger (OpenAPI).
- Data persistence in an embedded H2 database with file-based storage.
- Built with hexagonal architecture for modularity and maintainability.

## Architecture
This application follows the **hexagonal architecture** (Ports and Adapters) approach:
- **Domain**: Contains the core business logic and models (`Account`, `Currency`) independent of external systems.
- **Application**: Houses the `AccountService`, which orchestrates use cases using ports.
- **Ports**: Defines interfaces (`AccountRepositoryPort`, `ExchangeRatePort`) for interacting with external systems.
- **Adapters**: Implements the ports:
  - **Persistence**: JPA adapter (`AccountRepositoryAdapter`) for H2 database.
  - **External**: FeignClient adapter (`NbpExchangeRateAdapter`) for NBP API.
  - **UI**: REST adapter (`AccountController`) for exposing the API.

This design isolates the business logic from infrastructure, allowing easy swapping of databases, external APIs, or UI layers.

## Prerequisites
- **Java 21**: Required runtime environment.
- **Maven 3.8+**: Build tool for managing dependencies and running the application.
- **Internet Access**: Needed to fetch exchange rates from the NBP API.

## Installation and Running
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/andrzej-zalewski/currency-account.git
    ```
   
2. **Navigate to the Project Directory:**
   ```bash
    cd currency-account
    ```
3. **Build the Application**:
   ```bash
    mvn clean install
    ```
4. **Run the Application**:
   ```bash
    mvn spring-boot:run
    ```

The application will start on `http://localhost:8080`.

## Configuration
Configuration is managed via `application.yml`:
- **Database**: H2 with file persistence (`./data/currency-account`).
- **NBP API**: Base URL (`http://api.nbp.pl/api/exchangerates`).
- **Default Base Currency**: Set to `PLN` (configurable via `currency.default-base`).
- **Swagger**: Accessible at `/swagger-ui.html`.

## API Endpoints
The application exposes a RESTful API documented with Swagger. All endpoints are prefixed with `/api/accounts`.

### 1. Create a New Account
- **Endpoint**: `POST /api/accounts`
- **Description**: Creates a new account with a specified target currency and initial balance in the base currency (default PLN).
- **Request Body**:
    ```json
    {
        "firstName": "John",
        "lastName": "Doe",
        "targetCurrency": "USD",
        "initialBaseAmount": 1000.00
    }
    ```

- **Responses**:
  - `200 OK`: Account created successfully, returns the account details.
  - `400 Bad Request`: Invalid input data (e.g., missing fields, negative amount).

- **Example Response**:

    ```json
    {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "firstName": "John",
        "lastName": "Doe",
        "baseCurrency": "PLN",
        "targetCurrency": "USD",
        "baseAmount": 1000.00,
        "targetAmount": 0.00
    }
    ```

### 2. Get Account Details
- **Endpoint**: `GET /api/accounts/{id}`
- **Description**: Retrieves the details of an account by its UUID.
- **Path Parameter**:
  - `id`: Unique identifier of the account (UUID).
- **Responses**:
- `200 OK`: Account found, returns the account details.
- `404 Not Found`: Account with the specified ID does not exist.
- **Example Response**:

    ```json
    {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "firstName": "John",
        "lastName": "Doe",
        "baseCurrency": "PLN",
        "targetCurrency": "USD",
        "baseAmount": 900.00,
        "targetAmount": 0.00
    }
    ```

### 3. Exchange Currency
- **Endpoint**: `POST /api/accounts/{id}/exchange`
- **Description**: Exchanges an amount between the account's base and target currencies using the current NBP exchange rate.
- **Path Parameter**:
  - `id`: Unique identifier of the account (UUID).
- **Request Body**:
  - PLN to USD:
    ```json
    {
      "fromCurrency": "PLN",
      "toCurrency": "USD",
      "amount": 100.00
    }
    ```
  - USD to PLN:
    ```json
    {
        "fromCurrency": "USD",
        "toCurrency": "PLN",
        "amount": 25.00
    }
    ```
- **Responses**:
  - `200 OK`: Currency exchanged successfully, returns updated account details.
  - `400 Bad Request`: Invalid request data (e.g., insufficient balance, unsupported currencies).
  - `404 Not Found`: Account not found.
  - `503 Service Unavailable`: NBP API unavailable.
- **Example Response** (after PLN to USD exchange):
    ```json
    {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "firstName": "John",
        "lastName": "Doe",
        "baseCurrency": "PLN",
        "targetCurrency": "USD",
        "baseAmount": 900.00,
        "targetAmount": 25.97
    }
    ```

## Supported Currencies
The application supports the following currencies (defined in the `Currency` enum):
- `PLN` (Polish Zloty)
- `USD` (US Dollar)
- `EUR` (Euro)
- `GBP` (British Pound)

Note: Exchange rates are fetched from the NBP API, which provides rates relative to PLN. Only exchanges involving the default base currency (PLN) are currently supported.

## Database
- **Type**: Embedded H2 database.
- **Persistence**: Data is stored in a file (`./data/currency-account`) for persistence across restarts.
- **Console**: Accessible at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/currency-account`, Username: `sa`, Password: empty).

## API Documentation
- **Swagger UI**: Interactive API documentation is available at `http://localhost:8080/swagger-ui.html`.
- **OpenAPI JSON**: Raw OpenAPI specification can be accessed at `http://localhost:8080/api-docs`.

## Execution Notes
- The application runs on port `8080` by default.
- The entry point is the `CurrencyAccountApplication` class, annotated with `@SpringBootApplication`, which initializes the Spring context.
- Exchange rates are fetched in real-time from the NBP API using Spring Cloud OpenFeign for declarative HTTP client functionality.
- Exceptions are thrown for invalid operations (e.g., insufficient balance, unsupported currency exchange), which are mapped to appropriate HTTP status codes in the REST adapter.

## Development and Contribution
- **Source Code**: The project is organized under the `com.nn_group.currencyaccount` package.
- **Build**: Use `mvn clean install` to build the project and resolve dependencies.
- **Run**: Use `mvn spring-boot:run` to start the application locally.
- **Extend**: To add support for more currencies, update the `Currency` enum and ensure the NBP API supports the new currencies.

This application serves as a foundation for a currency management system and can be extended with additional features, such as support for multiple target currencies or alternative exchange rate providers.

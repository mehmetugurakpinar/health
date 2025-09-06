# Health Management System

This project is a microservices-based system for managing patient data. It consists of two main services: `data-service` for handling data persistence and `search-service` for providing full-text search capabilities.

## Architecture

The system is composed of the following services:

*   **Data Service**: A Spring Boot application responsible for creating and updating patient records. It persists data to an external FHIR server (Aidbox) and then triggers the `search-service` to index the new or updated data.
*   **Search Service**: A Spring Boot application that uses Elasticsearch to provide fast and efficient full-text search functionality for patient data.
*   **Elasticsearch**: A search and analytics engine that stores and indexes patient data for the `search-service`.
*   **Aidbox**: An external FHIR server used by the `data-service` as the primary data store.

## Services

### Data Service (`data-service`)

*   **Description**: Manages patient data, handling create and update operations.
*   **Port**: `8082`
*   **Endpoints**:
    *   `POST /patients`: Creates or updates a patient.
*   **Configuration**: `data-service/src/main/resources/application.properties`

### Search Service (`search-service`)

*   **Description**: Provides search functionality over the patient data.
*   **Port**: `8081`
*   **Endpoints**:
    *   `GET /search?q={query}`: Searches for patients by name.
    *   `GET /search/{id}`: Retrieves a patient by their ID.
*   **Configuration**: `search-service/src/main/resources/application.properties`

## Prerequisites

*   Java 21
*   Maven
*   Docker and Docker Compose

## Getting Started

### 1. Run Backend Dependencies

The project requires Elasticsearch and Aidbox to be running.

**Elasticsearch:**
A `docker-compose.yml` file is provided in the `search-service` directory to run Elasticsearch.

```sh
cd search-service
docker-compose up -d
```

**Aidbox:**
The `data-service` is configured to connect to an Aidbox instance at `http://localhost:8080`. You will need to have an Aidbox instance running. Please refer to the Aidbox documentation for setup instructions.

### 2. Run the Services

You can run each service using the Spring Boot Maven plugin.

**Run Data Service:**

```sh
cd data-service
mvn spring-boot:run
```

**Run Search Service:**

```sh
cd search-service
mvn spring-boot:run
```

Once all services are running, the system is ready to use.

## API Usage

### Create/Update a Patient

Send a `POST` request to the `data-service`.

**URL**: `http://localhost:8082/patients`
**Method**: `POST`
**Body**:

```json
{
    "given": "John",
    "family": "Doe",
    "birthDate": "1990-01-15",
    "gender": "male",
    "phoneNo": "555-1234"
}
```

This will create the patient in Aidbox and index it in Elasticsearch.

### Search for a Patient

Send a `GET` request to the `search-service`.

**URL**: `http://localhost:8081/search?q=john`
**Method**: `GET`

This will return a list of patients matching the search query.

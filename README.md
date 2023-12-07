# Auto-RIA API Wrapper Project

## Overview

This project, focusing on parsing car information from the [Auto RIA](https://auto.ria.com/uk/) website, consists of two
main components: the Car Parser and the API Wrapper. Both components are containerized using
Docker, providing ease of deployment and environment consistency.

## Features

1. **Data Parsing:** Extracts car information from the Auto RIA website.
2. **API Requests:** Supports GET and POST requests.
3. **Rate Limiting:** Limits API requests to prevent overuse, based on IP.
4. **Containerization:** Utilizes Docker for easy deployment and scalability.

## Containerization

### Docker Compose Configuration

- **Version:** `3.8`
- **Services:**
  - **Parser:** 
    - Builds and runs the Car Parser service.
    - Exposes port 8081.
    - Uses volume for resource sharing.
  - **Wrapper:** 
    - Builds and runs the API Wrapper service.
    - Exposes port 80 and links to the Parser service.
    - Environment variables for configuration.

## API Endpoints

### 1. GET /get

Fetches processed data based on the given request parameters.

#### Parameters:
- `requestName` (String): Specifies the name of the file to be processed.

#### Response Format (JSON):
```json
{
  "timestamp": "2023-12-07T12:00:00Z",
  "status": 200,
  "reason": "OK",
  "path": "/get",
  "processingStatus": "COMPLETED",
  "collection": [
    {
      "producer": "String",
      "model": "String",
      "body": "String",
      "drive": "String",
      "transmission": "String",
      "engine": 2.0,
      "horsepower": 150,
      "kW": 110,
      "fuel": "String",
      "distance": 100000.0,
      "owners": 1,
      "year": 2020,
      "price": 20000,
      "color": "String",
      "city": "String",
      "enabled": true,
      "link": "String"
    }
    // Additional car objects...
  ]
}
```

### 2. POST /produce

Initiates the processing of car data based on specified filters.

#### Request Body (JSON):
```json
{
  "id": "String", // Name of the output file
  "threads": 2, // Range: 1 to 100000
  "pages": 2147483647, // Range: 0 to 2147483647
  "generateCsv": false, // Boolean
  "force": false // Boolean
}
```

#### Response Format (JSON):
```json
{
  "timestamp": "2023-12-07T12:00:00Z",
  "status": 200,
  "reason": "OK",
  "path": "/produce",
  "processingStatus": "ACCEPTED"
}
```

These endpoints provide a straightforward way to interact with the service, allowing users to
initiate data processing and retrieve results in a structured JSON format. The GET endpoint is
used to fetch processed data, while the POST endpoint is for starting the data processing with
specific parameters.

## Rate Limiting in Wrapper Project

- **Max Requests:** 3 requests per minute per IP.
- **Over Limit Response:** Status code 429 with header `X-Rate-Limit-Retry-After-Seconds`.

## Error Handling

The project implements robust error handling to ensure that any issues during API requests are
clearly communicated to the client.

#### Response Format on Validation Error (JSON):
```json
{
  "timestamp": "2023-12-07T12:00:00Z",
  "status": 400,
  "reason": "Bad Request",
  "path": "/requested/path",
  "cause": {
    "fieldName": "Error Message",
    // Additional field errors...
  }
}
```

This error handling mechanism ensures that validation errors are captured and reported in a
structured format. The `cause` field provides detailed information about each validation error,
making it easier for clients to understand and rectify their requests.

## Technology Stack

- **Primary Service:** Java 11, Spring Boot 2, Maven 3.8.5.
- **API Wrapper:** Java 11, Spring Boot 2, Gradle 8.2.
- **Containerization:** Docker.

## Example CURL Requests

### POST /produce

```bash
curl -X POST "http://localhost/produce" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"id\": \"output\", \"threads\": 2, \"pages\": 100, \"generateCsv\": false, \"force\": false}"
```

### GET /get

```bash
curl -X GET "http://localhost/get?requestName=output" -H "accept: */*"
```

## Running the Project with Docker Compose

```bash
docker-compose up --build
```

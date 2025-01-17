# WeatherApp-backend

A Spring Boot application that provides weather forecasts and summaries by integrating with the OpenMeteo API. The application calculates solar energy estimates and offers weekly weather summaries, including average pressure, sunshine exposure, and temperature details.

---

## Features

- Retrieve a 7-day weather forecast for any location.
- Calculate solar energy production estimates based on sunshine duration.
- Get a weekly weather summary, including:
    - Average pressure.
    - Average sunshine exposure.
    - Minimum and maximum temperatures.
    - Rainfall summary.

---

## Frontend Integration

This backend application is designed to work seamlessly with the [WeatherApp Frontend](https://github.com/HKordula/WeatherApp-frontend), which provides a user-friendly interface for interacting with the API.

- **Frontend Repository**: [HKordula/WeatherApp-frontend](https://github.com/HKordula/WeatherApp-frontend)
- **Frontend Technologies**: React.js

### Setting Up the Frontend

1. Clone the frontend repository:
   ```bash
   git clone https://github.com/HKordula/WeatherApp-frontend.git
   cd WeatherApp-frontend
   ```

2. Follow the setup instructions in the frontend `README.md` to configure and run the UI.

3. Ensure the backend API (`WeatherApp`) is running on `http://localhost:8080`, or update the frontend configuration to point to the correct API URL.

---

## Technologies Used

- **Spring Boot**: Backend framework for REST API development.
- **Lombok**: Simplifies boilerplate code for DTOs.
- **RestTemplate**: Handles external API calls.
- **OpenMeteo API**: Source for weather data.
- **React.js** (Frontend): Dynamic user interface for consuming the API.

---

## Setup and Installation

### Backend (This Repository)

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/HKordula/WeatherApp-backend
   cd WeatherApp-backend
   ```

2. **Build the Project**:
   Make sure you have Maven installed, then run:
   ```bash
   mvn clean install
   ```

3. **Run the Application**:
   Start the application with:
   ```bash
   mvn spring-boot:run
   ```

4. **Access the API**:
   The application runs by default on `http://localhost:8080`.

---

## API Endpoints

### 1. Get 7-Day Forecast
**URL**: `/api/weather/forecast`  
**Method**: `GET`  
**Query Parameters**:
- `latitude` (double): Latitude of the location.
- `longitude` (double): Longitude of the location.

**Example Request**:
```http
GET http://localhost:8080/api/weather/forecast?latitude=52.5200&longitude=13.4050
```

**Response**:
```json
[
  {
    "date": "2025-01-20",
    "weatherCode": 2,
    "minTemperature": -3.0,
    "maxTemperature": 5.0,
    "estimatedEnergy": 12.5
  }
]
```

---

### 2. Get Weekly Weather Summary
**URL**: `/api/weather/summary`  
**Method**: `GET`  
**Query Parameters**:
- `latitude` (double): Latitude of the location.
- `longitude` (double): Longitude of the location.

**Example Request**:
```http
GET http://localhost:8080/api/weather/summary?latitude=52.5200&longitude=13.4050
```

**Response**:
```json
{
  "averagePressure": 1015.5,
  "averageSunExposure": 4.5,
  "minTemperature": -5.0,
  "maxTemperature": 8.0,
  "weekSummary": "with rainfall"
}
```

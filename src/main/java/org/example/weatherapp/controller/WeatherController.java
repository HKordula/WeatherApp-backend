package org.example.weatherapp.controller;

import org.example.weatherapp.dto.WeatherForecastDto;
import org.example.weatherapp.dto.WeatherSummaryDto;
import org.example.weatherapp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Controller class for handling weather-related API requests.
// Provides endpoints for weather forecasts and summaries.

@CrossOrigin("*") // Allows cross-origin requests from any domain.
@RestController
@RequestMapping("/api/weather") // Base URL for all weather-related endpoints.
public class WeatherController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    // Endpoint to retrieve a 7-day weather forecast for a given location.
    @GetMapping("/forecast")
    public ResponseEntity<List<WeatherForecastDto>> getForecast(@RequestParam double latitude, @RequestParam double longitude) {
        try {
            validateCoordinates(latitude, longitude);
            List<WeatherForecastDto> forecast = weatherService.get7DayForecast(latitude, longitude); // Fetch the 7-day weather forecast from the service layer.
            return ResponseEntity.ok(forecast); // Return the forecast.
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Handle invalid coordinate inputs.
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Handle unexpected server errors.
        }
    }

    // Endpoint to retrieve a weekly weather summary for a given location.
    @GetMapping("/summary")
    public ResponseEntity<WeatherSummaryDto> getWeeklySummary(@RequestParam double latitude, @RequestParam double longitude) {
        try {
            validateCoordinates(latitude, longitude);
            WeatherSummaryDto summary = weatherService.getWeeklySummary(latitude, longitude); // Fetch the weekly weather summary from the service layer.
            return ResponseEntity.ok(summary); // Return the summary.
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Handle invalid coordinate inputs.
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Handle unexpected server errors.
        }
    }

    // Validates the latitude and longitude values.
    private void validateCoordinates(double latitude, double longitude) {
        if (latitude <= -90 || latitude >= 90 || longitude <= -180 || longitude >= 180) {
            throw new IllegalArgumentException("Invalid latitude or longitude values."); // Throw an exception for invalid coordinates.
        }
    }
}
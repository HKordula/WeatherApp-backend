package org.example.weatherapp.controller;

import org.example.weatherapp.dto.WeatherForecastDto;
import org.example.weatherapp.dto.WeatherSummaryDto;
import org.example.weatherapp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/forecast")
    public ResponseEntity<List<WeatherForecastDto>> getForecast(@RequestParam double latitude, @RequestParam double longitude) {
        try {
            validateCoordinates(latitude, longitude);
            List<WeatherForecastDto> forecast = weatherService.get7DayForecast(latitude, longitude);
            return ResponseEntity.ok(forecast);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<WeatherSummaryDto> getWeeklySummary(@RequestParam double latitude, @RequestParam double longitude) {
        try {
            validateCoordinates(latitude, longitude);
            WeatherSummaryDto summary = weatherService.getWeeklySummary(latitude, longitude);
            return ResponseEntity.ok(summary);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private void validateCoordinates(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid latitude or longitude values.");
        }
    }
}
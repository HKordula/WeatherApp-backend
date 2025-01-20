package org.example.weatherapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Data Transfer Object for weekly weather summary.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherSummaryDto {
    private double averagePressure; // Average atmospheric pressure (hPa).
    private double averageSunExposure; // Average sun exposure (hours).
    private double minTemperature; // Minimum temperature recorded during the week (°C).
    private double maxTemperature; // Maximum temperature recorded during the week (°C).
    private String weekSummary; // Summary of the week's weather.
}

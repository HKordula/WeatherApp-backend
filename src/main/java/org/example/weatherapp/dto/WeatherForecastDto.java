package org.example.weatherapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Data Transfer Object for weather forecast information.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherForecastDto {
    private String date; // Date of the forecast.
    private int weatherCode; //Weather condition code.
    private double minTemperature; // Minimum temperature (°C).
    private double maxTemperature; // Maximum temperature (°C).
    private double estimatedEnergy; // Estimated energy production.
}

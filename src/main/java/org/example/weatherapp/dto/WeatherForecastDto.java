package org.example.weatherapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherForecastDto {
    private String date;
    private int weatherCode;
    private double minTemperature;
    private double maxTemperature;
    private double estimatedEnergy;
}

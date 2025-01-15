package org.example.weatherapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherSummaryDto {
    private double averagePressure;
    private double averageSunExposure;
    private double minTemperature;
    private double maxTemperature;
    private String weekSummary;

}

package org.example.weatherapp.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherSummary {
    private double averagePressure;
    private double averageSunExposure;
    private double minTemperature;
    private double maxTemperature;
    private String weekSummary;
}

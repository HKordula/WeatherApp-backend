package org.example.weatherapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.weatherapp.dto.WeatherForecastDto;
import org.example.weatherapp.dto.WeatherSummaryDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${openmeteo.api.url}")
    private String openMeteoApiUrl;

    private static final double INSTALLATION_POWER = 2.5;
    private static final double PANEL_EFFICIENCY = 0.2;

    public WeatherService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<WeatherForecastDto> get7DayForecast(double latitude, double longitude) {
        String url = buildForecastUrl(latitude, longitude);
        JsonNode body = fetchWeatherData(url, "daily");

        List<WeatherForecastDto> forecastList = new ArrayList<>();
        JsonNode dailyData = body.get("daily");

        for (int i = 0; i < dailyData.get("time").size(); i++) {
            forecastList.add(parseDailyForecast(dailyData, i));
        }

        return forecastList;
    }

    public WeatherSummaryDto getWeeklySummary(double latitude, double longitude) {
        String url = buildSummaryUrl(latitude, longitude);
        JsonNode body = fetchWeatherData(url, "hourly");

        return calculateWeeklySummary(body.get("hourly"));
    }

    private String buildForecastUrl(double latitude, double longitude) {
        return String.format(Locale.ENGLISH, "%s?latitude=%.6f&longitude=%.6f&daily=weathercode,temperature_2m_min,temperature_2m_max,sunshine_duration&timezone=auto",
                openMeteoApiUrl, latitude, longitude);
    }

    private String buildSummaryUrl(double latitude, double longitude) {
        return String.format(Locale.ENGLISH, "%s?latitude=%.6f&longitude=%.6f&hourly=pressure_msl,temperature_2m,sunshine_duration,weather_code&timezone=auto",
                openMeteoApiUrl, latitude, longitude);
    }

    private JsonNode fetchWeatherData(String url, String requiredNode) {
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        JsonNode body = response.getBody();

        if (body == null || body.get(requiredNode) == null) {
            throw new RuntimeException("Invalid response from weather API.");
        }

        return body;
    }

    private WeatherForecastDto parseDailyForecast(JsonNode dailyData, int index) {
        double sunshineDurationInHours = dailyData.get("sunshine_duration").get(index).asDouble() / 3600.0;
        return new WeatherForecastDto(
                dailyData.get("time").get(index).asText(),
                dailyData.get("weathercode").get(index).asInt(),
                dailyData.get("temperature_2m_min").get(index).asDouble(),
                dailyData.get("temperature_2m_max").get(index).asDouble(),
                calculateEnergy(sunshineDurationInHours)
        );
    }

    private double calculateEnergy(double sunshineDuration) {
        return Math.round(INSTALLATION_POWER * sunshineDuration * PANEL_EFFICIENCY * 100.0) / 100.0;
    }

    private WeatherSummaryDto calculateWeeklySummary(JsonNode hourlyData) {
        double totalPressure = 0.0;
        double totalSunshine = 0.0;
        double minTemperature = Double.MAX_VALUE;
        double maxTemperature = Double.MIN_VALUE;
        int rainyHours = 0;

        int totalHours = hourlyData.get("pressure_msl").size();

        for (int i = 0; i < totalHours; i++) {
            double pressure = hourlyData.get("pressure_msl").get(i).asDouble();
            double sunshine = hourlyData.get("sunshine_duration").get(i).asDouble() / 3600.0;
            double temp = hourlyData.get("temperature_2m").get(i).asDouble();
            int weatherCode = hourlyData.get("weather_code").get(i).asInt();

            totalPressure += pressure;
            totalSunshine += sunshine;
            minTemperature = Math.min(minTemperature, temp);
            maxTemperature = Math.max(maxTemperature, temp);

            if (isRainy(weatherCode)) {
                rainyHours++;
            }
        }

        return new WeatherSummaryDto(
                totalPressure / totalHours,
                totalSunshine / totalHours,
                minTemperature,
                maxTemperature,
                rainyHours > totalHours / 2 ? "z opadami" : "bez opadów"
        );
    }

    private boolean isRainy(int weatherCode) {
        return (weatherCode >= 51 && weatherCode <= 57) ||
                (weatherCode >= 61 && weatherCode <= 67) ||
                (weatherCode >= 80 && weatherCode <= 82) ||
                (weatherCode >= 95 && weatherCode <= 99);
    }
}

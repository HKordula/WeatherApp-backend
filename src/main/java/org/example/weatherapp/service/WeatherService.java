package org.example.weatherapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.weatherapp.dto.WeatherForecastDto;
import org.example.weatherapp.dto.WeatherSummaryDto;
import org.example.weatherapp.exception.ExternalApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${openmeteo.api.url}") // Injects the base URL for the OpenMeteo API.
    private String openMeteoApiUrl;

    private static final double INSTALLATION_POWER = 2.5; // Panel installation power (kW).
    private static final double PANEL_EFFICIENCY = 0.2; // Panel efficiency (20%).

    public WeatherService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build(); // Build RestTemplate for API calls.
    }

    // Retrieves the 7-day weather forecast for the given location.
    public List<WeatherForecastDto> get7DayForecast(double latitude, double longitude) {
        String url = buildForecastUrl(latitude, longitude); // Build the daily forecast API URL.
        JsonNode dailyData = fetchWeatherData(url, "daily"); // Fetch daily data from the API.

        // Parse daily data into a list of WeatherForecastDto objects.
        List<WeatherForecastDto> forecastList = new ArrayList<>();
        for (int i = 0; i < dailyData.get("time").size(); i++) {
            forecastList.add(parseDailyForecast(dailyData, i));
        }

        return forecastList;
    }

    // Retrieves a weekly summary of the weather for the given location.
    public WeatherSummaryDto getWeeklySummary(double latitude, double longitude) {
        // Build URLs for hourly and daily data.
        String hourlyUrl = buildHourlyUrl(latitude, longitude);
        String dailyUrl = buildForecastUrl(latitude, longitude);

        // Fetch hourly and daily data from the API.
        JsonNode hourlyData = fetchWeatherData(hourlyUrl, "hourly");
        JsonNode dailyData = fetchWeatherData(dailyUrl, "daily");

        // Calculate and return the weekly summary.
        return calculateWeeklySummary(hourlyData, dailyData);
    }

    // Builds the URL for fetching daily forecast data.
    private String buildForecastUrl(double latitude, double longitude) {
        return String.format(Locale.ENGLISH,
                "%s?latitude=%.6f&longitude=%.6f&daily=weather_code,temperature_2m_min,temperature_2m_max,sunshine_duration&timezone=auto",
                openMeteoApiUrl, latitude, longitude);
    }

    // Builds the URL for fetching hourly weather data.
    private String buildHourlyUrl(double latitude, double longitude) {
        return String.format(Locale.ENGLISH,
                "%s?latitude=%.6f&longitude=%.6f&hourly=pressure_msl&timezone=auto",
                openMeteoApiUrl, latitude, longitude);
    }

    // Fetches weather data from the API and validates the response.
    private JsonNode fetchWeatherData(String url, String requiredNode) {
        try {
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);

            // Check for HTTP error responses.
            if (response.getStatusCode().isError()) {
                throw new ExternalApiException("API returned error: " + response.getStatusCode());
            }

            JsonNode body = response.getBody();
            if (body == null || body.get(requiredNode) == null) {
                throw new ExternalApiException("Invalid response: Missing node '" + requiredNode + "'");
            }

            return body.get(requiredNode); // Return the required data node.
        } catch (RestClientException e) {
            throw new ExternalApiException("Error while calling external API", e);
        }
    }

    // Parses daily forecast data into a WeatherForecastDto object.
    private WeatherForecastDto parseDailyForecast(JsonNode dailyData, int index) {
        double sunshineDurationInHours = dailyData.get("sunshine_duration").get(index).asDouble() / 3600.0;
        return new WeatherForecastDto(
                dailyData.get("time").get(index).asText(),
                dailyData.get("weather_code").get(index).asInt(),
                dailyData.get("temperature_2m_min").get(index).asDouble(),
                dailyData.get("temperature_2m_max").get(index).asDouble(),
                calculateEnergy(sunshineDurationInHours) // Calculate energy production for the day.
        );
    }

    // Calculates estimated energy production based on sunshine duration.
    private double calculateEnergy(double sunshineDuration) {
        return Math.round(INSTALLATION_POWER * sunshineDuration * PANEL_EFFICIENCY * 100.0) / 100.0;
    }

    // Calculates a weekly weather summary from hourly and daily data.
    private WeatherSummaryDto calculateWeeklySummary(JsonNode hourlyData, JsonNode dailyData) {
        // Calculate average pressure
        double totalPressure = 0.0;
        int totalHours = hourlyData.get("pressure_msl").size();
        for (int i = 0; i < totalHours; i++) {
            totalPressure += hourlyData.get("pressure_msl").get(i).asDouble();
        }
        double averagePressure = Math.round((totalPressure / totalHours) * 100.0) / 100.0;

        // Calculate daily data
        double totalSunshine = 0.0;
        double minTemperature = Double.MAX_VALUE;
        double maxTemperature = -Double.MAX_VALUE;
        int rainyDays = 0;
        int totalDays = dailyData.get("time").size();

        for (int i = 0; i < totalDays; i++) {
            totalSunshine += dailyData.get("sunshine_duration").get(i).asDouble() / 3600.0;
            minTemperature = Math.min(minTemperature, dailyData.get("temperature_2m_min").get(i).asDouble());
            maxTemperature = Math.max(maxTemperature, dailyData.get("temperature_2m_max").get(i).asDouble());

            if (isRainy(dailyData.get("weather_code").get(i).asInt())) {
                rainyDays++;
            }
        }

        // Determine if the week had significant rainfall.
        double averageSunshine = Math.round((totalSunshine / totalDays) * 100.0) / 100.0;

        return new WeatherSummaryDto(
                averagePressure,
                averageSunshine,
                minTemperature,
                maxTemperature,
                rainyDays > totalDays / 2 ? "with rainfall" : "without rainfall"
        );
    }

    // Determines if a weather code represents rain or thunderstorms.
    private boolean isRainy(int weatherCode) {
        return (weatherCode >= 51 && weatherCode <= 55) ||  // Drizzle
                (weatherCode >= 61 && weatherCode <= 65) ||  // Rain
                (weatherCode >= 80 && weatherCode <= 82) ||  // Rain showers
                (weatherCode >= 95 && weatherCode <= 99);    // Thunderstorm (with or without hail)
    }
}
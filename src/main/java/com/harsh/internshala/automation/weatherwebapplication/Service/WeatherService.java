package com.harsh.internshala.automation.weatherwebapplication.Service;
import com.harsh.internshala.automation.weatherwebapplication.Exceptions.WeatherServiceException;
import com.harsh.internshala.automation.weatherwebapplication.Model.WeatherDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class WeatherService {

    private final WebClient webClient;

    @Value("${openweather.api.key}")
    private String apiKey;

    // Inject API URL via constructor to ensure it's available before WebClient is built
    public WeatherService(@Value("${openweather.api.url}") String apiUrl, WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
    }

    public Mono<WeatherDTO> fetchWeatherData(String city) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("q", city)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> parseWeatherResponse(city, response))
                .onErrorResume(WebClientResponseException.class, e -> handleWebClientException(city, e))
                .onErrorResume(Exception.class, e -> {
                    e.printStackTrace(); // Log the error
                    return Mono.error(new WeatherServiceException("An unexpected error occurred: " + e.getMessage(), e));
                });
    }

    private WeatherDTO parseWeatherResponse(String city, Map<String, Object> response) {
        if (response == null || !response.containsKey("name") || !response.containsKey("main") || !response.containsKey("weather")) {
            throw new WeatherServiceException("Invalid API response: Missing required fields for city " + city);
        }

        String cityName = (String) response.get("name");
        Map<String, Object> main = (Map<String, Object>) response.get("main");

        double temperature = extractDouble(main.get("temp"));
        double humidity = extractDouble(main.get("humidity"));
        double pressure = extractDouble(main.get("pressure"));

        var weatherList = (java.util.List<?>) response.get("weather");
        if (weatherList == null || weatherList.isEmpty()) {
            throw new WeatherServiceException("Invalid API response: Weather data missing for city " + city);
        }

        Map<String, Object> weather = (Map<String, Object>) weatherList.get(0);
        String weatherDescription = (String) weather.get("description");

        return new WeatherDTO(cityName, weatherDescription, temperature, humidity, pressure);
    }

    private Mono<WeatherDTO> handleWebClientException(String city, WebClientResponseException e) {
        if (e.getStatusCode().value() == 404) {
            return Mono.error(new WeatherServiceException("City not found: " + city, e));
        } else {
            return Mono.error(new WeatherServiceException("Failed to fetch weather data: " + e.getMessage(), e));
        }
    }

    private double extractDouble(Object value) {
        return value instanceof Number ? ((Number) value).doubleValue() : 0.0;
    }
}

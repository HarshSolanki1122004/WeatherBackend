package com.harsh.internshala.automation.weatherwebapplication.Model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WeatherDTO {
    private String cityName;
    private String weatherDescription;
    private double temperature;
    private double humidity;
    private double pressure;

    public WeatherDTO(String cityName, String weatherDescription, double temperature, double humidity, double pressure) {
        this.cityName = cityName;
        this.weatherDescription = weatherDescription;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
    }

}

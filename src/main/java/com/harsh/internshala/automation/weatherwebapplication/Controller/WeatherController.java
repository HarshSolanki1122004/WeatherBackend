package com.harsh.internshala.automation.weatherwebapplication.Controller;
import com.harsh.internshala.automation.weatherwebapplication.Model.WeatherDTO;
import com.harsh.internshala.automation.weatherwebapplication.Service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService){
        this.weatherService = weatherService;
    }

    @GetMapping("/{city}")
    public Mono<WeatherDTO> getWeather(@PathVariable String city) {
        return weatherService.fetchWeatherData(city);
    }

}

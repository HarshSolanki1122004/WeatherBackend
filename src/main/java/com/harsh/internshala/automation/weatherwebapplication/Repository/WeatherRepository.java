package com.harsh.internshala.automation.weatherwebapplication.Repository;
import com.harsh.internshala.automation.weatherwebapplication.Model.WeatherHistoryClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherHistoryClass,Long> {
}

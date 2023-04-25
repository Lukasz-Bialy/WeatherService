package com.vector.weatherservice.weather.agglomeration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "weather-service")
public class AgglomerationProperties {

  /**
   * Configuration of locations belonging to the agglomeration. It allows us to calculate average
   * weather data for specific agglomeration. Where key = agglomeration name and value = list of
   * location names
   */
  private Map<String, List<String>> agglomerationToLocations = new HashMap<>();
}

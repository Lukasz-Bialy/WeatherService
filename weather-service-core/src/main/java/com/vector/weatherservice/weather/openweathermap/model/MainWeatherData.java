package com.vector.weatherservice.weather.openweathermap.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MainWeatherData {

  @JsonProperty(value = "temp")
  private Double temperature;

  private Integer pressure;

  private Integer humidity;

  @Override
  public String toString() {
    return "temperature=" + temperature + ", pressure=" + pressure + ", humidity=" + humidity;
  }
}

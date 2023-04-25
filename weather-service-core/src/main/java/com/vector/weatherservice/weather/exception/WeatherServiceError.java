package com.vector.weatherservice.weather.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class WeatherServiceError {
  private final Integer status;
  private final String title;
  private final String details;

  public static WeatherServiceError of(HttpStatus status, String details) {
    return new WeatherServiceError(status.value(), status.getReasonPhrase(), details);
  }
}

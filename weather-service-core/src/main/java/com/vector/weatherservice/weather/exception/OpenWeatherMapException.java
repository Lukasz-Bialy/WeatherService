package com.vector.weatherservice.weather.exception;

public class OpenWeatherMapException extends RuntimeException {

  private OpenWeatherMapException(String message) {
    super(message);
  }

  public static OpenWeatherMapException of(String message) {
    return new OpenWeatherMapException(message);
  }
}

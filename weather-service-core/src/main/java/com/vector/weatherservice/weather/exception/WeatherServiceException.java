package com.vector.weatherservice.weather.exception;

public class WeatherServiceException extends RuntimeException {

  private WeatherServiceException(String message) {
    super(message);
  }

  public static WeatherServiceException of(String message) {
    return new WeatherServiceException(message);
  }
}

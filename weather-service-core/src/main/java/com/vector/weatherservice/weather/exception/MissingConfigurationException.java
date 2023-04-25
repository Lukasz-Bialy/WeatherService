package com.vector.weatherservice.weather.exception;

public class MissingConfigurationException extends RuntimeException {

  private MissingConfigurationException(String message) {
    super(message);
  }

  public static MissingConfigurationException of(String message) {
    return new MissingConfigurationException(message);
  }
}

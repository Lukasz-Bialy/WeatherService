package com.vector.weatherservice.weather.exception;

public class RequestModelException extends RuntimeException {

  private RequestModelException(String message) {
    super(message);
  }

  public static RequestModelException of(String message) {
    return new RequestModelException(message);
  }
}

package com.vector.weatherservice.weather.exception;

import com.vector.weatherservice.weather.WeatherController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackageClasses = WeatherController.class)
@Configuration
@Slf4j
public class ErrorHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {ArithmeticException.class})
  protected ResponseEntity<WeatherServiceError> handleArithmeticException(
      ArithmeticException ex, HttpServletRequest request) {
    final WeatherServiceError weatherServiceError =
        WeatherServiceError.of(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(weatherServiceError);
  }

  @ExceptionHandler(value = {OpenWeatherMapException.class})
  protected ResponseEntity<WeatherServiceError> handleOpenWeatherMapException(
      OpenWeatherMapException ex, HttpServletRequest request) {
    final WeatherServiceError weatherServiceError =
        WeatherServiceError.of(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(weatherServiceError);
  }

  @ExceptionHandler(value = {RequestModelException.class})
  protected ResponseEntity<WeatherServiceError> handleRequestModelException(
      RequestModelException ex, HttpServletRequest request) {
    final WeatherServiceError weatherServiceError =
        WeatherServiceError.of(HttpStatus.BAD_REQUEST, ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(weatherServiceError);
  }

  @ExceptionHandler(value = {MissingConfigurationException.class})
  protected ResponseEntity<WeatherServiceError> handleMissingConfigurationException(
      MissingConfigurationException ex, HttpServletRequest request) {
    final WeatherServiceError weatherServiceError =
        WeatherServiceError.of(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(weatherServiceError);
  }

  @ExceptionHandler(value = {WeatherServiceException.class})
  protected ResponseEntity<WeatherServiceError> handleWeatherServiceException(
      WeatherServiceException ex, HttpServletRequest request) {
    final WeatherServiceError weatherServiceError =
        WeatherServiceError.of(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(weatherServiceError);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    log.error("Missing servlet request parameter", ex);
    if (ex.getMessage() != null) {
      final WeatherServiceError weatherServiceError =
          WeatherServiceError.of(HttpStatus.BAD_REQUEST, ex.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(weatherServiceError);
    }

    return super.handleMissingServletRequestParameter(ex, headers, status, request);
  }
}

package com.vector.weatherservice.weather.openweathermap;

import com.vector.weatherservice.api.weather.model.Weather;
import com.vector.weatherservice.weather.WeatherService;
import com.vector.weatherservice.weather.agglomeration.Agglomeration;
import com.vector.weatherservice.weather.agglomeration.AgglomerationProperties;
import com.vector.weatherservice.weather.exception.MissingConfigurationException;
import com.vector.weatherservice.weather.exception.OpenWeatherMapException;
import com.vector.weatherservice.weather.exception.WeatherServiceException;
import com.vector.weatherservice.weather.openweathermap.model.LocationWeather;
import com.vector.weatherservice.weather.util.AverageWeatherCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenWeatherMapService implements WeatherService {

  private static final int GET_LOCATION_WEATHER_TIMEOUT = 60;

  private static final String WEATHER_SERVICE_EXCEPTION_MESSAGE =
      "Failed to retrieve location weather required to calculate agglomeration weather";

  private final LocationWeatherClient weatherClient;
  private final AgglomerationProperties agglomerationProperties;
  private final AverageWeatherCalculator calculator;

  @Override
  public Weather getAgglomerationWeather(Agglomeration agglomeration) {
    calculator.clear();

    final Optional<List<String>> optionalLocations =
        Optional.ofNullable(
            agglomerationProperties.getAgglomerationToLocations().get(agglomeration.getValue()));

    final List<CompletableFuture<LocationWeather>> locationWeatherFutures =
        createLocationWeatherFutures(optionalLocations, agglomeration);

    final CompletableFuture<Void> combinedLocationsWeatherFutures =
        CompletableFuture.allOf(
            locationWeatherFutures.toArray(new CompletableFuture[locationWeatherFutures.size()]));

    collectLocationsWeather(combinedLocationsWeatherFutures);

    sumLocationsWeather(locationWeatherFutures);

    return Weather.builder()
        .temperature(calculator.calcTemperature())
        .pressure(calculator.calcPressure())
        .humidity(calculator.calcHumidity())
        .build();
  }

  private List<CompletableFuture<LocationWeather>> createLocationWeatherFutures(
      Optional<List<String>> optionalLocations, Agglomeration agglomeration) {
    return optionalLocations
        .map(
            locations ->
                locations.stream()
                    .map(location -> supplyAsync(() -> weatherClient.getLocationWeather(location)))
                    .collect(Collectors.toList()))
        .filter(completableFutures -> !completableFutures.isEmpty())
        .orElseThrow(() -> throwMissingAgglomerationLocationsException(agglomeration));
  }

  private void collectLocationsWeather(CompletableFuture<Void> combinedLocationWeatherFutures) {
    try {
      combinedLocationWeatherFutures.get(GET_LOCATION_WEATHER_TIMEOUT, TimeUnit.SECONDS);
    } catch (InterruptedException ex) {
      handleInterruptedException(ex);
    } catch (ExecutionException ex) {
      handleExecutionException(ex);
    } catch (TimeoutException ex) {
      handleTimeoutException(ex);
    }
  }

  private void sumLocationsWeather(
      List<CompletableFuture<LocationWeather>> locationWeatherFutures) {
    locationWeatherFutures.stream()
        .map(
            locationWeatherFuture -> {
              try {
                return locationWeatherFuture.get();
              } catch (InterruptedException ex) {
                handleInterruptedException(ex);
              } catch (ExecutionException ex) {
                handleExecutionException(ex);
              }
              throw WeatherServiceException.of(WEATHER_SERVICE_EXCEPTION_MESSAGE);
            })
        .map(LocationWeather::getMainWeatherData)
        .forEach(calculator::add);
  }

  private void handleTimeoutException(TimeoutException ex) {
    log.error(
        "Failed to retrieve location weather required to calculate agglomeration weather. Request timed out",
        ex);
    throw WeatherServiceException.of(WEATHER_SERVICE_EXCEPTION_MESSAGE);
  }

  private void handleExecutionException(Exception ex) {
    log.error(
        "Failed to retrieve location weather required to calculate agglomeration weather. Error occurred while requesting asynchronously location weather",
        ex);
    if (ex.getCause() instanceof OpenWeatherMapException) {
      throw (OpenWeatherMapException) ex.getCause();
    } else if (ex.getCause() != null && ex.getCause().getCause() instanceof TimeoutException) {
      handleTimeoutException((TimeoutException) ex.getCause().getCause());
    } else {
      throw WeatherServiceException.of(WEATHER_SERVICE_EXCEPTION_MESSAGE);
    }
  }

  private void handleInterruptedException(InterruptedException ex) {
    log.error(
        "Main thread was interrupted while requesting location weather data from Open Weather API server",
        ex);
    Thread.currentThread().interrupt();
    throw WeatherServiceException.of(WEATHER_SERVICE_EXCEPTION_MESSAGE);
  }

  private MissingConfigurationException throwMissingAgglomerationLocationsException(
      Agglomeration agglomeration) {
      log.error("Failed to retrieve agglomeration weather. Agglomeration is missing locations");
    return MissingConfigurationException.of(
        "Failed to retrieve agglomeration weather. Configuration of the agglomeration '"
            + agglomeration
            + "' locations is missing");
  }
}

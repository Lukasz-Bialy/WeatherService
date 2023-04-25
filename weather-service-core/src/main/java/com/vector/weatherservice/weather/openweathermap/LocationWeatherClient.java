package com.vector.weatherservice.weather.openweathermap;

import com.vector.weatherservice.weather.exception.OpenWeatherMapException;
import com.vector.weatherservice.weather.openweathermap.model.LocationWeather;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

@Service
@Slf4j
class LocationWeatherClient {

  private static final String LOCATION_COUNTRY = "pl";
  private static final String LOCATION_WEATHER_UNITS = "metric";
  private static final String QUERY_PARAM_PREFIX = "?";

  private static final String LOCATION_NAME_QUERY_PARAM = "q=";
  private static final String UNITS_QUERY_PARAM = "&units=";
  private static final String APP_ID_QUERY_PARAM = "&appid=";

  private final WebClient openWeatherMapClient;
  private final String appId;

  LocationWeatherClient(WebClient webClient, OpenWeatherMapProperties weatherMapProperties) {
    this.openWeatherMapClient = webClient;
    appId = weatherMapProperties.getAppid();
  }

  LocationWeather getLocationWeather(String locationName) {
    final String uri = createLocationWeatherUri(locationName);
    return openWeatherMapClient
        .get()
        .uri(uri)
        .retrieve()
        .onStatus(
            HttpStatus::isError,
            errorResponse -> createErrorResponseException(errorResponse, locationName))
        .toEntity(LocationWeather.class)
        .timeout(Duration.ofSeconds(30))
        .doOnError(TimeoutException.class, ex -> createTimeoutException(locationName, ex))
        .mapNotNull(HttpEntity::getBody)
        .map(locationWeather -> validateResponse(locationWeather, locationName))
        .blockOptional()
        .orElseThrow(createEmptyResponseBodyException(locationName));
  }

  private LocationWeather validateResponse(LocationWeather locationWeather, String locationName) {
    if (isWeatherPopulated(locationWeather)) {
      return locationWeather;
    }
    log.error(
        "Failed to retrieve location '{}' weather from Open Weather Map server, temperature, pressure or humidity are empty",
        locationName);

    throw OpenWeatherMapException.of(
        String.format(
            "Failed to retrieve location '%s' weather. Third party service didn't not send enough data",
            locationName));
  }

  private boolean isWeatherPopulated(LocationWeather locationWeather) {
    return locationWeather.getMainWeatherData() != null
        && locationWeather.getMainWeatherData().getTemperature() != null
        && locationWeather.getMainWeatherData().getPressure() != null
        && locationWeather.getMainWeatherData().getHumidity() != null;
  }

  private String createLocationWeatherUri(String locationName) {
    return new StringBuilder()
        .append(QUERY_PARAM_PREFIX)
        .append(LOCATION_NAME_QUERY_PARAM + locationName + "," + LOCATION_COUNTRY)
        .append(UNITS_QUERY_PARAM + LOCATION_WEATHER_UNITS)
        .append(APP_ID_QUERY_PARAM + appId)
        .toString();
  }

  private Supplier<OpenWeatherMapException> createEmptyResponseBodyException(String locationName) {
    return () -> {
      log.error(
          "Failed to retrieve location '{}' weather details. Response body is empty", locationName);
      return OpenWeatherMapException.of(
          "Failed to retrieve location '"
              + locationName
              + "' weather details. Response body is empty");
    };
  }

  private Supplier<OpenWeatherMapException> createTimeoutException(
      String locationName, TimeoutException ex) {
    return () -> {
      log.error(
          "Failed to retrieve location '{}' weather details. Request took longer than 30 seconds",
          locationName,
          ex);
      return OpenWeatherMapException.of(
          "Failed to retrieve location '" + locationName + "' weather details. Request timed out");
    };
  }

  private Mono<? extends Throwable> createErrorResponseException(
      ClientResponse response, String locationName) {
    log.error(
        "Failed to retrieve location '{}' weather details. Open Weather Map server returned an error with status {}",
        locationName,
        response.statusCode());
    return Mono.error(
        OpenWeatherMapException.of(
            "Open Weather Map client failed to request location '"
                + locationName
                + "' weather data. Server failed with status "
                + response.statusCode()));
  }
}

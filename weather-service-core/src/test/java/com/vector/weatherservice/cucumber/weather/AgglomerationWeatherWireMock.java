package com.vector.weatherservice.cucumber.weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.vector.weatherservice.weather.openweathermap.model.LocationWeather;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;

import java.net.URI;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class AgglomerationWeatherWireMock {

  private static final String LOCATION_WEATHER_URI = "/weather?q=%s,pl&units=metric&appid=1234";
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @SneakyThrows
  public static void stubLocationWeather(String locationName, LocationWeather locationWeather) {
    final URI uri = new URI(String.format(LOCATION_WEATHER_URI, locationName));
    stubFor(
        get(urlEqualTo(uri.toASCIIString()))
            .willReturn(
                aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .withBody(objectMapper.writeValueAsString(locationWeather))
                    .withStatus(OK.value())));
  }

    @SneakyThrows
    static void stubTimeOutLocationWeather(String locationName, LocationWeather locationWeather) {
        final URI uri = new URI(String.format(LOCATION_WEATHER_URI, locationName));
        stubFor(
                get(urlEqualTo(uri.toASCIIString()))
                        .willReturn(
                                aResponse()
                                        .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                        .withBody(objectMapper.writeValueAsString(locationWeather))
                                        .withStatus(OK.value()).withFixedDelay(40000)));
    }
}

package com.vector.weatherservice.weather.openweathermap;

import com.vector.weatherservice.api.weather.model.Weather;
import com.vector.weatherservice.weather.agglomeration.Agglomeration;
import com.vector.weatherservice.weather.agglomeration.AgglomerationProperties;
import com.vector.weatherservice.weather.exception.MissingConfigurationException;
import com.vector.weatherservice.weather.openweathermap.model.LocationWeather;
import com.vector.weatherservice.weather.openweathermap.model.MainWeatherData;
import com.vector.weatherservice.weather.util.AverageWeatherCalculator;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenWeatherMapServiceTest {

  private final LocationWeatherClient webClient = mock(LocationWeatherClient.class);
  private final AgglomerationProperties agglomerationProperties = new AgglomerationProperties();
  private final AverageWeatherCalculator calculator = new AverageWeatherCalculator();
  private final OpenWeatherMapService openWeatherMapService =
      new OpenWeatherMapService(webClient, agglomerationProperties, calculator);

  @Test
  void
      givenExistingAgglomerationToLocationConfiguration_whenAgglomerationWeatherIsRequested_thenAgglomerationWeatherIsReturned() {
    // given
    agglomerationProperties.setAgglomerationToLocations(createAgglomerationToLocations());
    final LocationWeather gdanskWeather = createLocationWeather("Gdańsk", 10D, 800, 50);
    when(webClient.getLocationWeather("Gdańsk")).thenReturn(gdanskWeather);

    final LocationWeather gdyniaWeather = createLocationWeather("Gdynia", 12D, 900, 60);
    when(webClient.getLocationWeather("Gdynia")).thenReturn(gdyniaWeather);

    final LocationWeather sopotWeather = createLocationWeather("Sopot", 18D, 850, 70);
    when(webClient.getLocationWeather("Sopot")).thenReturn(sopotWeather);

    // when
    final Weather agglomerationWeather =
        openWeatherMapService.getAgglomerationWeather(Agglomeration.TRÓJMIASTO);

    // then
    assertThat(agglomerationWeather).isNotNull();
    assertThat(agglomerationWeather.getTemperature())
        .isNotNull()
        .isNotNaN()
        .isEqualTo(13.33333333, Offset.offset(0.00000001));
    assertThat(agglomerationWeather.getPressure()).isNotNull().isNotNaN().isEqualTo(2550D / 3);
    assertThat(agglomerationWeather.getHumidity()).isNotNull().isNotNaN().isEqualTo(180D / 3);
  }

  @Test
  void
      givenNonExistingAgglomerationToLocationConfiguration_whenAgglomerationWeatherIsRequested_thenExceptionIsThrown() {
    // given-when-then
    assertThatExceptionOfType(MissingConfigurationException.class)
        .isThrownBy(() -> openWeatherMapService.getAgglomerationWeather(Agglomeration.TRÓJMIASTO));
  }

  private LocationWeather createLocationWeather(
      String name, double temperature, int pressure, int humidity) {
    final LocationWeather locationWeather = new LocationWeather();
    locationWeather.setName(name);

    final MainWeatherData mainWeatherData = new MainWeatherData();
    mainWeatherData.setTemperature(temperature);
    mainWeatherData.setPressure(pressure);
    mainWeatherData.setHumidity(humidity);

    locationWeather.setMainWeatherData(mainWeatherData);
    return locationWeather;
  }

  private Map<String, List<String>> createAgglomerationToLocations() {
    final List<String> locations = new ArrayList<>();
    locations.add("Gdańsk");
    locations.add("Gdynia");
    locations.add("Sopot");
    final Map<String, List<String>> agglomerationToLocations = new HashMap<>();
    agglomerationToLocations.put("trojmiasto", locations);
    return agglomerationToLocations;
  }
}

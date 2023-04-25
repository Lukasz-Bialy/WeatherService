package com.vector.weatherservice.weather;

import com.vector.weatherservice.api.weather.WeatherApi;
import com.vector.weatherservice.api.weather.model.Weather;
import com.vector.weatherservice.weather.agglomeration.Agglomeration;
import com.vector.weatherservice.weather.agglomeration.AgglomerationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WeatherController implements WeatherApi {

  private final WeatherService weatherService;
  private final AgglomerationMapper agglomerationMapper;

  @Override
  public ResponseEntity<Weather> getWeather(String agglomeration) {
    final Agglomeration agglomerationEnum =
        agglomerationMapper.toAgglomerationEnum(agglomeration);
    log.debug("Getting agglomeration '{}' weather", agglomeration);

    final Weather agglomerationWeather = weatherService.getAgglomerationWeather(agglomerationEnum);

    log.info("Agglomeration '{}' weather, {}", agglomeration, agglomerationWeather);

    return ResponseEntity.ok(agglomerationWeather);
  }
}

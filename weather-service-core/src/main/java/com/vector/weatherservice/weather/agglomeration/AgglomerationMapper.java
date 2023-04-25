package com.vector.weatherservice.weather.agglomeration;

import com.vector.weatherservice.weather.exception.RequestModelException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@Slf4j
public class AgglomerationMapper {
  public Agglomeration toAgglomerationEnum(String agglomeration) {
    try {
      return Agglomeration.valueOf(agglomeration.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException ex) {
      log.error(
          "Failed to map agglomeration {} to any of the known agglomeration values", agglomeration);
      throw RequestModelException.of(
          "Failed to retrieve agglomeration weather data. Agglomeration '"
              + agglomeration
              + "' is unknown");
    }
  }
}

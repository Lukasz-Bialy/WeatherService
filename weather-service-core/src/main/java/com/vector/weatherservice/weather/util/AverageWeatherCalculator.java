package com.vector.weatherservice.weather.util;

import com.vector.weatherservice.weather.openweathermap.model.MainWeatherData;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class AverageWeatherCalculator {
  private BigDecimal temperature = BigDecimal.ZERO;
  private int pressure = 0;
  private int humidity = 0;
  private int counter = 0;

  public void add(MainWeatherData mainWeatherData) {
    temperature = temperature.add(BigDecimal.valueOf(mainWeatherData.getTemperature()));
    pressure += mainWeatherData.getPressure();
    humidity += mainWeatherData.getHumidity();
    counter++;
  }

  public double calcTemperature() {
    if (counter != 0) {
      return temperature.divide(BigDecimal.valueOf(counter), 8, RoundingMode.HALF_UP).doubleValue();
    }
    throw new ArithmeticException("Failed to divide temperature by 0");
  }

  public double calcPressure() {
    if (counter != 0) {
      return BigDecimal.valueOf(pressure)
          .divide(BigDecimal.valueOf(counter), 8, RoundingMode.HALF_UP)
          .doubleValue();
    }
    throw new ArithmeticException("Failed to divide pressure by 0");
  }

  public double calcHumidity() {
    if (counter != 0) {
      return BigDecimal.valueOf(humidity)
          .divide(BigDecimal.valueOf(counter), 8, RoundingMode.HALF_UP)
          .doubleValue();
    }
    throw new ArithmeticException("Failed to divide humidity by 0");
  }

  public void clear() {
    temperature = BigDecimal.ZERO;
    pressure = 0;
    humidity = 0;
    counter = 0;
  }
}

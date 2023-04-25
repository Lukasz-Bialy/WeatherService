package com.vector.weatherservice.weather.util;

import com.vector.weatherservice.weather.openweathermap.model.MainWeatherData;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class AverageWeatherCalculatorTest {

  private final AverageWeatherCalculator averageWeatherCalculator = new AverageWeatherCalculator();

  @AfterEach
  public void afterEach() {
    averageWeatherCalculator.clear();
  }

  @Test
  void
      givenWeatherWithMixedValues_whenMultipleWeatherAddedToTheCalculator_thenCalculatedWeatherIsCorrect() {
    // given
    final MainWeatherData weather1 = new MainWeatherData();
    weather1.setTemperature(10.7D);
    weather1.setPressure(1000);
    weather1.setHumidity(60);

    final MainWeatherData weather2 = new MainWeatherData();
    weather2.setTemperature(-4.7D);
    weather2.setPressure(1);
    weather2.setHumidity(30);

    // when
    averageWeatherCalculator.add(weather1);
    averageWeatherCalculator.add(weather2);
    final double actualTemperature = averageWeatherCalculator.calcTemperature();
    final double actualPressure = averageWeatherCalculator.calcPressure();
    final double actualHumidity = averageWeatherCalculator.calcHumidity();

    // then
    assertThat(actualTemperature).isEqualTo(3, Offset.offset(0.00000001));
    assertThat(actualPressure).isEqualTo(500.5D, Offset.offset(0.00000001));
    assertThat(actualHumidity).isEqualTo(45, Offset.offset(0.00000001));
  }

  @Test
  void
      givenWeatherWithPositiveValues_whenMultipleWeatherAddedToTheCalculator_thenCalculatedWeatherIsPositive() {
    // given
    final MainWeatherData weather1 = new MainWeatherData();
    weather1.setTemperature(10.7D);
    weather1.setPressure(1000);
    weather1.setHumidity(60);

    final MainWeatherData weather2 = new MainWeatherData();
    weather2.setTemperature(5.3D);
    weather2.setPressure(500);
    weather2.setHumidity(70);

    // when
    averageWeatherCalculator.add(weather1);
    averageWeatherCalculator.add(weather2);
    final double actualTemperature = averageWeatherCalculator.calcTemperature();
    final double actualPressure = averageWeatherCalculator.calcPressure();
    final double actualHumidity = averageWeatherCalculator.calcHumidity();

    // then
    assertThat(actualTemperature).isEqualTo(8, Offset.offset(0.00000001));
    assertThat(actualPressure).isEqualTo(750, Offset.offset(0.00000001));
    assertThat(actualHumidity).isEqualTo(65, Offset.offset(0.00000001));
  }

  @Test
  void
      givenWeatherWithPositiveValues_whenSingleWeatherAddedToTheCalculator_thenCalculatedWeatherIsPositive() {
    // given
    final double expectedTemperature = 10.7F;
    final int expectedHumidity = 60;
    final int expectedPressure = 1000;

    final MainWeatherData weather = new MainWeatherData();
    weather.setTemperature(expectedTemperature);
    weather.setPressure(expectedPressure);
    weather.setHumidity(expectedHumidity);

    // when
    averageWeatherCalculator.add(weather);
    final double actualTemperature = averageWeatherCalculator.calcTemperature();
    final double actualPressure = averageWeatherCalculator.calcPressure();
    final double actualHumidity = averageWeatherCalculator.calcHumidity();

    // then
    assertThat(actualTemperature).isEqualTo(expectedTemperature, Offset.offset(0.00000001));
    assertThat(actualPressure).isEqualTo(expectedPressure, Offset.offset(0.00000001));
    assertThat(actualHumidity).isEqualTo(expectedHumidity, Offset.offset(0.00000001));
  }

  @Test
  void givenWeatherWithZeroValues_whenAddedToTheCalculator_thenCalculatedWeatherEqualsZero() {
    // given
    final MainWeatherData weather = new MainWeatherData();
    weather.setTemperature(0D);
    weather.setPressure(0);
    weather.setHumidity(0);

    // when
    averageWeatherCalculator.add(weather);
    final double actualTemperature = averageWeatherCalculator.calcTemperature();
    final double actualPressure = averageWeatherCalculator.calcPressure();
    final double actualHumidity = averageWeatherCalculator.calcHumidity();

    // then
    assertThat(actualTemperature).isZero();
    assertThat(actualPressure).isZero();
    assertThat(actualHumidity).isZero();
  }

  @Test
  void
      givenNoWeather_whenNoWeatherIsAddedToCalculator_thenCalculatingTemperatureThrowsDividingByZeroException() {
    // given-when-then
    assertThatExceptionOfType(ArithmeticException.class)
        .isThrownBy(averageWeatherCalculator::calcTemperature);
  }

  @Test
  void
      givenNoWeather_whenNoWeatherIsAddedToCalculator_thenCalculatingHumidityThrowsDividingByZeroException() {
    // given-when-then
    assertThatExceptionOfType(ArithmeticException.class)
        .isThrownBy(averageWeatherCalculator::calcHumidity);
  }

  @Test
  void
      givenNoWeather_whenNoWeatherIsAddedToCalculator_thenCalculatingPressureThrowsDividingByZeroException() {
    // given-when-then
    assertThatExceptionOfType(ArithmeticException.class)
        .isThrownBy(averageWeatherCalculator::calcPressure);
  }

  @Test
  void
      givenSingleWeatherData_whenCalculatorIsCleared_thenCalculatingAnyValueThrowsDividingByZeroException() {
    // given
    final MainWeatherData weather = new MainWeatherData();
    weather.setTemperature(10.7D);
    weather.setPressure(1000);
    weather.setHumidity(60);

    // when-then
    averageWeatherCalculator.add(weather);
    averageWeatherCalculator.clear();

    assertThatExceptionOfType(ArithmeticException.class)
        .isThrownBy(averageWeatherCalculator::calcTemperature);
    assertThatExceptionOfType(ArithmeticException.class)
        .isThrownBy(averageWeatherCalculator::calcPressure);
    assertThatExceptionOfType(ArithmeticException.class)
        .isThrownBy(averageWeatherCalculator::calcHumidity);
  }
}

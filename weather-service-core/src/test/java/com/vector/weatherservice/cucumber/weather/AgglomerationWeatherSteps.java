package com.vector.weatherservice.cucumber.weather;

import com.vector.weatherservice.api.weather.model.Weather;
import com.vector.weatherservice.weather.exception.WeatherServiceError;
import com.vector.weatherservice.weather.openweathermap.model.LocationWeather;
import com.vector.weatherservice.weather.openweathermap.model.MainWeatherData;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.assertj.core.data.Offset;

import static org.assertj.core.api.Assertions.assertThat;

public class AgglomerationWeatherSteps {

  private static final String AGGLOMERATION_QUERY_PARAMETER = "agglomeration";
  private static final String AGGLOMERATION_WEATHER_URI = "/weather";
  private Response response;

  static {
    RestAssured.port = 8080;
  }

  @Given("Location {string} has temperature {double}, pressure {int} and humidity {int}")
  public void locationHasTemperaturePressureAndHumidity(
      String locationName, double temperature, int pressure, int humidity) {
    final LocationWeather locationWeather =
        createLocationWeather(locationName, temperature, pressure, humidity);
    AgglomerationWeatherWireMock.stubLocationWeather(locationName, locationWeather);
  }

  @Given("Location {string} has no weather details")
  public void locationHasNoWeatherDetails(String locationName) {
    final LocationWeather locationWeather = new LocationWeather();
    locationWeather.setName(locationName);
    AgglomerationWeatherWireMock.stubLocationWeather(locationName, locationWeather);
  }

  @Given(
      "Location {string} has temperature {double}, pressure {int} and humidity {int} but has"
          + " no name in the response")
  public void locationHasTemperaturePressureAndHumidityButHasNoNameInTheResponse(
      String locationName, double temperature, int pressure, int humidity) {
    final LocationWeather locationWeather =
        createLocationWeather(null, temperature, pressure, humidity);
    AgglomerationWeatherWireMock.stubLocationWeather(locationName, locationWeather);
  }

  @Given(
      "Location {string} has temperature {double}, pressure {int} and humidity {int} but the"
          + " response is timed out")
  public void locationHasTemperaturePressureAndHumidityButTheResponseIsTimedOut(
      String locationName, double temperature, int pressure, int humidity) {
    final LocationWeather locationWeather =
        createLocationWeather(locationName, temperature, pressure, humidity);
    AgglomerationWeatherWireMock.stubTimeOutLocationWeather(locationName, locationWeather);
  }

  @When("I request weather for agglomeration called {string}")
  public void iRequestWeatherForAgglomerationCalled(String agglomerationName) {
    response =
        RestAssured.given()
            .queryParam(AGGLOMERATION_QUERY_PARAMETER, agglomerationName)
            .get(AGGLOMERATION_WEATHER_URI);
  }

  @When("I request weather without specifying agglomeration")
  public void iRequestWeatherWithoutSpecifyingAgglomeration() {
    response = RestAssured.given().get(AGGLOMERATION_WEATHER_URI);
  }

  @Then(
      "I receive agglomeration weather with temperature {double}, pressure {double} and "
          + "humidity {double}")
  public void iReceiveAgglomerationWeatherWithTemperaturePressureAndHumidity(
      double expectedTemperature, double expectedPressure, double expectedHumidity) {
    final Weather agglomerationWeather = response.body().as(Weather.class);
    assertThat(agglomerationWeather).isNotNull();

    assertThat(agglomerationWeather.getTemperature())
        .isNotNull()
        .isNotNaN()
        .isEqualTo(expectedTemperature, Offset.offset(0.00000001));

    assertThat(agglomerationWeather.getPressure())
        .isNotNull()
        .isNotNaN()
        .isEqualTo(expectedPressure, Offset.offset(0.00000001));

    assertThat(agglomerationWeather.getHumidity())
        .isNotNull()
        .isNotNaN()
        .isEqualTo(expectedHumidity, Offset.offset(0.00000001));
  }

  @Then("I receive error response with status {int} and message {string}")
  public void iReceiveErrorResponseWithStatusAndMessage(int statusCode, String message) {
    final WeatherServiceError weatherServiceError = response.body().as(WeatherServiceError.class);
    assertThat(weatherServiceError.getStatus()).isNotNull().isEqualTo(Integer.valueOf(statusCode));
    assertThat(weatherServiceError.getDetails()).isNotNull().isEqualTo(message);
  }

  private static LocationWeather createLocationWeather(
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
}

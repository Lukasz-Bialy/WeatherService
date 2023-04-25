package com.vector.weatherservice.cucumber;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.vector.weatherservice.WeatherServiceApplication;
import io.cucumber.junit.platform.engine.Cucumber;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestRunStarted;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;

@Slf4j
@Cucumber
@CucumberContextConfiguration
@SpringBootTest(
    classes = WeatherServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CucumberSpringTest implements EventListener {

  protected static WireMockServer WIRE_MOCK_SERVER;

  @Override
  public void setEventPublisher(EventPublisher eventPublisher) {
    eventPublisher.registerHandlerFor(TestRunStarted.class, CucumberSpringTest::onTestRunStarted);
    eventPublisher.registerHandlerFor(TestRunFinished.class, CucumberSpringTest::onTestRunFinished);
  }

  private static <T> void onTestRunStarted(T t) {
    WIRE_MOCK_SERVER = new WireMockServer(8081);
    WIRE_MOCK_SERVER.start();
    configureFor(WIRE_MOCK_SERVER.port());
  }

  private static void onTestRunFinished(TestRunFinished testRunFinished) {
    if (WIRE_MOCK_SERVER != null) {
      WIRE_MOCK_SERVER.stop();
    }
  }
}

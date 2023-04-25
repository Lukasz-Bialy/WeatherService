package com.vector.weatherservice.weather.openweathermap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
class ClientConfiguration {

  @Bean
  public WebClient openWeatherMapClient(OpenWeatherMapProperties weatherMapProperties) {
    return WebClient.builder()
        .baseUrl(weatherMapProperties.getUrl())
        .build();
  }
}

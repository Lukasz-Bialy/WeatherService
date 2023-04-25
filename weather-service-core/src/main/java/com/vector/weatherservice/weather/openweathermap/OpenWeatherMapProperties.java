package com.vector.weatherservice.weather.openweathermap;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "weather-service.client.open-weather-map")
class OpenWeatherMapProperties {
    /**
     * Url to the Open Weather Map API server
     */
    private String url;

    /**
     * App id required by requests to the Open Weather Map API
     */
    private String appid;
}

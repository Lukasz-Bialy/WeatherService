package com.vector.weatherservice.weather;

import com.vector.weatherservice.api.weather.model.Weather;
import com.vector.weatherservice.weather.agglomeration.Agglomeration;

public interface WeatherService {
    Weather getAgglomerationWeather(Agglomeration agglomeration);
}

package com.vector.weatherservice.weather.agglomeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Agglomeration {
  TRÓJMIASTO("trojmiasto"),
  AGLOMERACJA_ŁÓDZKA("aglomeracjaLodzka"),
  METROPOLIA_GÓRNOŚLĄSKA("metropoliaGornoslaska");

  private final String value;
}

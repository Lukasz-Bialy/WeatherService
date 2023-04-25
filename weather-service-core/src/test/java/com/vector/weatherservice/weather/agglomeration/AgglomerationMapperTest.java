package com.vector.weatherservice.weather.agglomeration;

import com.vector.weatherservice.weather.exception.RequestModelException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class AgglomerationMapperTest {

  private final AgglomerationMapper agglomerationMapper = new AgglomerationMapper();

  @ParameterizedTest
  @CsvSource({
    "Trójmiasto, TRÓJMIASTO",
    "Aglomeracja_Łódzka, AGLOMERACJA_ŁÓDZKA",
    "Metropolia_Górnośląska, METROPOLIA_GÓRNOŚLĄSKA"
  })
  void givenExistingAgglomerationName_whenMappedToAgglomerationEnum_thenAgglomerationEnumIsReturned(
      String name, Agglomeration agglomeration) {
    // given-when
    final Agglomeration actualAgglomeration = agglomerationMapper.toAgglomerationEnum(name);

    // then
    assertThat(actualAgglomeration).isNotNull().isEqualTo(agglomeration);
  }

  @Test
  void givenNonExistingAgglomerationName_whenMappedToAgglomerationEnum_thenExceptionIsThrown() {
    // given
    final String nonExistingAgglomerationName = "Mazury";

    // when-then
    assertThatExceptionOfType(RequestModelException.class)
        .isThrownBy(() -> agglomerationMapper.toAgglomerationEnum(nonExistingAgglomerationName));
  }
}

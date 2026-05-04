package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.UnaryOperator;
import org.junit.jupiter.api.Test;

class DummyNullDateFilterTest {

  private static final ZonedDateTime DUMMY_DATE = ZonedDateTime.parse("1970-01-01T00:00Z");

  private final UnaryOperator<ZonedDateTime> filter = new DummyNullDateFilter(DUMMY_DATE);

  @Test
  void shouldReplaceDummyDateWithNull() {
    var result = filter.apply(DUMMY_DATE);

    assertThat(result).isNull();
  }

  @Test
  void shouldReturnAnyOtherDate() {
    var input = DUMMY_DATE.plus(1, ChronoUnit.MILLIS);

    var result = filter.apply(input);

    assertThat(result).isEqualTo(input);
  }
}

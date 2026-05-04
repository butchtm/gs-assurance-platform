package org.goldstandard.assurance.api.service;

import java.time.ZonedDateTime;
import java.util.function.UnaryOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DummyNullDateFilter implements UnaryOperator<ZonedDateTime> {

  private final ZonedDateTime dummyNullDate;

  public DummyNullDateFilter() {
    this(ZonedDateTime.parse("1970-01-01T00:00Z"));
  }

  @Override
  public ZonedDateTime apply(ZonedDateTime dateTime) {
    if (dateTime.equals(dummyNullDate)) {
      return null;
    }
    return dateTime;
  }
}

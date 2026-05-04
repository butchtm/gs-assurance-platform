package org.goldstandard.assurance.api.entity.activity;

import jakarta.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
@Getter
public class RequestActivities implements Iterable<RequestActivity> {

  private final RequestActivity next;
  private final Collection<RequestActivity> others;

  public RequestActivities() {
    this(null);
  }

  public RequestActivities(RequestActivity next) {
    this(next, Collections.emptyList());
  }

  public boolean isEmpty() {
    return Objects.isNull(next) && others.isEmpty();
  }

  @Override
  public @Nonnull Iterator<RequestActivity> iterator() {
    return asCollection().iterator();
  }

  public Collection<RequestActivity> asCollection() {
    if (Objects.nonNull(next)) {
      return Stream.concat(others.stream(), Stream.of(next)).toList();
    }
    return others;
  }
}

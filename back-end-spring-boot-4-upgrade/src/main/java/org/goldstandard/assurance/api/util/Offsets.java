package org.goldstandard.assurance.api.util;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@Data
public class Offsets {
  private final int start;
  private final int end;

  public boolean matches(Offsets other) {
    return other.start >= start && other.end <= end;
  }
}

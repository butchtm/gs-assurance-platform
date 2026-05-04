package org.goldstandard.assurance.api.dto;

import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class RequestFormFiltersDTO {
  private final Integer displayCategoryId;

  public Optional<Integer> getDisplayCategoryId() {
    return Optional.ofNullable(displayCategoryId);
  }
}

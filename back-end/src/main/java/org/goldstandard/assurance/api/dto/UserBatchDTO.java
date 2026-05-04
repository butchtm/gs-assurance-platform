package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Getter
public class UserBatchDTO {

  private final Integer id;
  private final Collection<UserBatchErrorDTO> errors;
  private final ZonedDateTime createdAt;
  private final ZonedDateTime updatedAt;
  private final boolean complete;
  private final int count;
  private final int successCount;
}

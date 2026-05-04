package org.goldstandard.assurance.api.dto;

import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
public class RequestRoundUpdateDTO {
  private final @NotNull ZonedDateTime startDate;
  private final @NotNull ZonedDateTime endDate;
  private final @NotNull Integer statusId;
}

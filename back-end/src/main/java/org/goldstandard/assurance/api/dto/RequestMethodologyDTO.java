package org.goldstandard.assurance.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RequestMethodologyDTO {

  @Positive private Integer creditingPeriodId;

  @PositiveOrZero @NotNull private Integer methodologyId;

  private String methodologyDescription;

  @PositiveOrZero @NotNull private Double methodologyVersion;
}

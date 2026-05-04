package org.goldstandard.assurance.api.entity.view;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Getter
@Builder
public class StatusReportRequestsGrouping {
  private final String type;
  private final String description;
  private final Integer count;
}

package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.activity.Activity;

@RequiredArgsConstructor
@Getter
@Builder
public class RequestActivityDTO {
  private final Integer id;
  private final String name;
  private final Activity activity;
  private final ZonedDateTime startDate;
  private final ZonedDateTime dueDate;
  private final ZonedDateTime endDate;
  private final boolean skipped;
  private final AccountDTO account;
}

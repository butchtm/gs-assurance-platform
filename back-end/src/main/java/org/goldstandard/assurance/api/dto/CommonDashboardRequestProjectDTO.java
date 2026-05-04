package org.goldstandard.assurance.api.dto;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class CommonDashboardRequestProjectDTO {
  private final String gsid;
  private final String name;
  private final ProjectStatusDTO status;
  private final String poaGsid;
  private final String realCaseVpaGsid;
  private final ProjectClassificationDTO classification;
  private final Collection<ProjectType> types;
  private final FeeScheduleDTO feeSchedule;
  private final String accountName;
}

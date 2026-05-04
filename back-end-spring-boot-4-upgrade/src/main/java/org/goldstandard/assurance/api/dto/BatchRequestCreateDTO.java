package org.goldstandard.assurance.api.dto;

import jakarta.validation.Valid;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.goldstandard.assurance.api.dto.validator.RequestCreditingPeriod;
import org.goldstandard.assurance.api.dto.validator.RequestMonitoringPeriod;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder
public class BatchRequestCreateDTO {

  private final int requestTypeId;
  private final int batchTypeId;
  private final List<@Valid ProjectRequestInfo> projectRequestInfos;

  public int getNumberOfProjects() {
    if (CollectionUtils.isEmpty(projectRequestInfos)) {
      return 0;
    }
    return projectRequestInfos.size();
  }

  @Getter
  @NoArgsConstructor(force = true)
  @RequiredArgsConstructor
  @Builder
  public static class ProjectRequestInfo {

    private final int projectId;
    private final @RequestCreditingPeriod @Valid CreditingPeriodDTO creditingPeriod;
    private final @RequestMonitoringPeriod @Valid MonitoringPeriodDTO monitoringPeriod;
    private final Integer emissionReductionAmount;
    private final List<@Valid RequestMethodologyDTO> methodologies;
  }
}

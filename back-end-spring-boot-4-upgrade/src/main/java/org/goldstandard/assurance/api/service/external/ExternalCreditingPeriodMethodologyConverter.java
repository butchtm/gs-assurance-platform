package org.goldstandard.assurance.api.service.external;

import java.util.Collection;
import java.util.Collections;
import org.apache.commons.collections4.CollectionUtils;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodMethodology;
import org.springframework.stereotype.Component;

@Component
public class ExternalCreditingPeriodMethodologyConverter {

  public Collection<ExternalCreditingPeriodMethodology> toMethodologyDtos(
      Collection<ProjectCreditingPeriodMethodology> methodologies) {
    if (CollectionUtils.isEmpty(methodologies)) {
      return Collections.emptyList();
    }
    return methodologies.stream()
        .map(ExternalCreditingPeriodMethodologyConverter::toMethodologyDto)
        .toList();
  }

  private static ExternalCreditingPeriodMethodology toMethodologyDto(
      ProjectCreditingPeriodMethodology methodology) {
    return ExternalCreditingPeriodMethodology.builder()
        .id(methodology.getMethodologyId())
        .description(methodology.getDescription())
        .version(methodology.getVersion())
        .build();
  }
}

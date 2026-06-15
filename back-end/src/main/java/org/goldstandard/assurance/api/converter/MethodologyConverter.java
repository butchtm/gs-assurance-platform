package org.goldstandard.assurance.api.converter;

import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.goldstandard.assurance.api.dto.CreditingPeriodMethodologyDTO;
import org.goldstandard.assurance.api.dto.MethodologyManagementDTO;
import org.goldstandard.assurance.api.dto.RequestMethodologyDTO;
import org.goldstandard.assurance.api.entity.RequestMethodology;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodMethodology;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.repository.ProjectCreditingPeriodMethodologyRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MethodologyConverter {

  private final ProjectCreditingPeriodMethodologyRepository periodMethodologyRepository;

  public Collection<CreditingPeriodMethodologyDTO> toDtos(
      Collection<ProjectCreditingPeriodMethodology> methodologies) {
    if (CollectionUtils.isEmpty(methodologies)) {
      return Collections.emptyList();
    }
    return methodologies.stream().map(MethodologyConverter::toDto).toList();
  }

  public Collection<RequestMethodologyDTO> toRequestMethodologyDtos(
      Collection<RequestMethodology> methodologies) {
    if (CollectionUtils.isEmpty(methodologies)) {
      return Collections.emptyList();
    }
    return methodologies.stream().map(this::toRequestMethodologyDto).toList();
  }

  public RequestMethodologyDTO toRequestMethodologyDto(RequestMethodology methodology) {
    return RequestMethodologyDTO.builder()
        .creditingPeriodId(methodology.getRequest().getCreditingPeriod())
        .methodologyId(methodology.getMethodology().getId())
        .methodologyDescription(methodology.getMethodology().getDescription())
        .methodologyVersion(methodology.getMethodologyVersion())
        .build();
  }

  public MethodologyManagementDTO toManagementDTO(Methodology methodology) {
    var inUse = periodMethodologyRepository.existsByMethodologyId(methodology.getId());
    return MethodologyManagementDTO.builder()
        .id(methodology.getId())
        .description(methodology.getDescription())
        .code(methodology.getCode())
        .active(methodology.getActive())
        .paa(methodology.isPaa())
        .inUse(inUse)
        .build();
  }

  private static CreditingPeriodMethodologyDTO toDto(
      ProjectCreditingPeriodMethodology methodology) {
    return CreditingPeriodMethodologyDTO.builder()
        .id(methodology.getMethodologyId())
        .code(methodology.getCode())
        .description(methodology.getDescription())
        .version(methodology.getVersion())
        .build();
  }
}

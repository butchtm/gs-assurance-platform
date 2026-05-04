package org.goldstandard.assurance.api.converter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.ConsultationDTO;
import org.goldstandard.assurance.api.dto.ConsultationTypeDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.entity.ConsultationType;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultationConverter {

  private final ProjectConverter projectConverter;
  private final RequestConverter requestConverter;

  public PagedResource<ConsultationDTO> toPagedDTOs(Page<StakeholderConsultation> page) {
    return new PagedResource<>(page.map(this::toDTO));
  }

  public List<ConsultationDTO> toDTOs(List<StakeholderConsultation> consultations) {
    return consultations.stream().map(this::toDTO).toList();
  }

  private ConsultationDTO toDTO(StakeholderConsultation consultation) {
    return ConsultationDTO.builder()
        .id(consultation.getId())
        .type(toTypeDTO(consultation.getConsultationType()))
        .request(requestConverter.toRequestOverviewDTO(consultation.getRequest()))
        .project(projectConverter.toOverviewProjectDto(consultation.getProject()))
        .startDate(consultation.getStartDate())
        .endDate(consultation.getEndDate())
        .build();
  }

  private static ConsultationTypeDTO toTypeDTO(ConsultationType type) {
    return ConsultationTypeDTO.builder()
        .id(type.getId())
        .userType(type.getUserType())
        .workingDays(type.getWorkingDays())
        .build();
  }
}

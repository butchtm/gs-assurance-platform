package org.goldstandard.assurance.api.converter;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.RequestActivityDTO;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.service.RequestFieldAccessPolicy;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RequestActivityConverter {
  private final RequestFieldAccessPolicy policy;
  private final AccountConverter accountConverter;

  public RequestActivityDTO toDto(RequestActivity requestActivity) {
    if (!policy.canViewActivity()) {
      return null;
    }
    return RequestActivityDTO.builder()
        .id(requestActivity.getId())
        .name(requestActivity.getDisplayName())
        .activity(requestActivity.getActivity())
        .startDate(requestActivity.getStartDate())
        .dueDate(requestActivity.getPlannedEndDate())
        .endDate(requestActivity.getEndDate())
        .skipped(requestActivity.isSkipped())
        .account(accountConverter.toDtoWithoutUsers(requestActivity.getResponsibleAccount()))
        .build();
  }
}

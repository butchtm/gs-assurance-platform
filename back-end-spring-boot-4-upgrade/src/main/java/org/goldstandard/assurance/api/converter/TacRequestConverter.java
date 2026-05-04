package org.goldstandard.assurance.api.converter;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.TacRequestDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.service.AssignedRequestAccountFinder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TacRequestConverter {

  private final AssignedRequestAccountFinder assignedAccountFinder;

  public TacRequestDTO toTacRequestDto(Request request) {
    var vvbAccount = assignedAccountFinder.getAssignedVvbAccount(request);
    return TacRequestDTO.builder()
        .id(request.getId())
        .projectName(request.getProject().getName())
        .projectGsid(request.getProject().getGsid())
        .requestStatus(request.getCurrentRequestStatus())
        .requestName(request.getRequestName())
        .vvbAccountName(vvbAccount.map(Account::getName).orElse(null))
        .vvbAccountId(vvbAccount.map(Account::getId).orElse(null))
        .lastRequestStatusChangeDate(request.getLastRequestStatusChangeDate())
        .build();
  }
}

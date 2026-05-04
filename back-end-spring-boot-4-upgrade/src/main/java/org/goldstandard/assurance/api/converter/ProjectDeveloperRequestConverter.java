package org.goldstandard.assurance.api.converter;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.ProjectDeveloperRequestDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.service.AssignedRequestAccountFinder;
import org.goldstandard.assurance.api.service.DocumentStatusCalculator;
import org.goldstandard.assurance.api.service.conversation.UnreadMessageFinder;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectDeveloperRequestConverter {

  private final DocumentStatusCalculator documentStatusCalculator;
  private final AssignedRequestAccountFinder assignedAccountFinder;
  private final AuthenticatedUserSupplier userSupplier;
  private final UnreadMessageFinder unreadMessagesFinder;

  public ProjectDeveloperRequestDTO toProjectDeveloperRequestDTO(Request request) {
    var dto = new ProjectDeveloperRequestDTO();
    dto.setId(request.getId());
    dto.setGsid(request.getProject().getGsid());
    dto.setProjectName(request.getProject().getName());
    dto.setProjectTypes(request.getProject().getProjectTypes());
    dto.setProjectLocations(request.getProject().getProjectLocations());
    dto.setCarbonStream(request.getProject().getCarbonStream());
    dto.setRequestName(request.getRequestName());
    if (request.getInvoice() != null) {
      dto.setInvoiceStatus(request.getInvoice().getStatus());
    }
    dto.setCurrentRequestStatus(request.getCurrentRequestStatus());
    dto.setCurrentRoundIndex(request.getCurrentRoundIndex());
    dto.setCurrentRoundEndDate(request.getCurrentRoundEndDate());
    dto.setBatched(request.getBatch() != null);
    dto.setLastRequestStatusChangeDate(request.getLastRequestStatusChangeDate());
    dto.setRequiredDocumentStatus(
        documentStatusCalculator.calculateRequiredDocumentsStatus(request));
    assignedAccountFinder.getAssignedVvbAccount(request).ifPresent(vvb -> setAssignedVvb(dto, vvb));
    setUnreadMessages(dto, request.getId());
    return dto;
  }

  private void setUnreadMessages(ProjectDeveloperRequestDTO dto, Integer requestId) {
    var user = userSupplier.forceGetAuthenticatedUser();
    dto.setUnreadMessages(
        unreadMessagesFinder.getUnreadMessageCountForUserAndRequest(user.getId(), requestId));
  }

  private static void setAssignedVvb(ProjectDeveloperRequestDTO dto, Account account) {
    dto.setVvbAccountId(account.getId());
    dto.setVvbAccountName(account.getName());
  }
}

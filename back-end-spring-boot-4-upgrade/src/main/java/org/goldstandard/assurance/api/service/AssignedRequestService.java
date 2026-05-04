package org.goldstandard.assurance.api.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.SpecificDashboardRequestFiltersDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.ExtendedReferenceDataType;
import org.goldstandard.assurance.api.entity.view.AssignedRequestView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssignedRequestService {

  private final AssignedRequestFinder requestFinder;
  private final AssignedRequestStatusFinder requestStatusFinder;
  private final AssignedRequestInvoiceStatusFinder invoiceStatusFinder;
  private final AssignedRequestTypeFinder requestTypeFinder;
  private final AssignedRequestProjectFinder projectFinder;
  private final AssignedRequestAccountFinder accountFinder;

  public Page<AssignedRequestView> getAssignedRequests(
      SpecificDashboardRequestFiltersDTO filters, Pageable pageable) {
    return requestFinder.getAssignedRequestPage(filters, pageable);
  }

  public List<ExtendedReferenceDataType> getAssignedRequestStatuses() {
    return requestStatusFinder.getAssignedRequestStatuses();
  }

  public List<ExtendedReferenceDataType> getAssignedRequestInvoiceStatuses() {
    return invoiceStatusFinder.getAssignedRequestInvoiceStatuses();
  }

  public List<Project> getAssignedRequestProjects() {
    return projectFinder.getAssignedRequestProjects();
  }

  public List<Account> getAssignedRequestAccounts(int accountTypeId) {
    return accountFinder.getAssignedRequestAccounts(accountTypeId);
  }

  public List<ExtendedReferenceDataType> getAssignedRequestTypes() {
    return requestTypeFinder.getAssignedRequestTypes();
  }
}

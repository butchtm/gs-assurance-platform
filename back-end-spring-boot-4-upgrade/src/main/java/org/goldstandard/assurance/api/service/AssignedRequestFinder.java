package org.goldstandard.assurance.api.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.SpecificDashboardRequestFiltersDTO;
import org.goldstandard.assurance.api.entity.view.AssignedRequestView;
import org.goldstandard.assurance.api.repository.AssignedRequestViewRepository;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssignedRequestFinder {

  private final AuthenticatedUserSupplier userSupplier;
  private final DashboardRequestSpecFactory specFactory;
  private final AssignedRequestViewRepository assignedRequestsRepository;
  private final DummyNullDateFilter nullDateFilter;

  public Page<AssignedRequestView> getAssignedRequestPage(
      SpecificDashboardRequestFiltersDTO filters, Pageable pageable) {
    var account = userSupplier.forceGetAuthenticatedUserAccount();
    Specification<AssignedRequestView> spec = specFactory.toSpec(account.getId(), filters);
    return assignedRequestsRepository
        .findAll(spec, pageable)
        .map(view -> view.removeDummyNullDates(nullDateFilter));
  }

  public List<AssignedRequestView> getAssignedRequests() {
    var account = userSupplier.forceGetAuthenticatedUserAccount();
    Specification<AssignedRequestView> spec = specFactory.isAssignedToAccount(account.getId());
    return assignedRequestsRepository.findAll(spec).stream()
        .map(view -> view.removeDummyNullDates(nullDateFilter))
        .toList();
  }
}

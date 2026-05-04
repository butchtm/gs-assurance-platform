package org.goldstandard.assurance.api.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.ExtendedReferenceDataType;
import org.goldstandard.assurance.api.entity.view.AssignedRequestView;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssignedRequestInvoiceStatusFinder {

  private final AssignedRequestFinder finder;
  private final ReferenceDataService referenceDataService;

  public List<ExtendedReferenceDataType> getAssignedRequestInvoiceStatuses() {
    Set<Integer> ids = getStatusIds();
    return referenceDataService.getInvoiceStatuses(ids);
  }

  private Set<Integer> getStatusIds() {
    return finder.getAssignedRequests().stream()
        .map(AssignedRequestView::getInvoiceStatusId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }
}

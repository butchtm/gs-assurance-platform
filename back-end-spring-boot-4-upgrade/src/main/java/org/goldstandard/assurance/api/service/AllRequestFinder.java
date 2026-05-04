package org.goldstandard.assurance.api.service;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.SpecificDashboardRequestFiltersDTO;
import org.goldstandard.assurance.api.entity.view.AllRequestView;
import org.goldstandard.assurance.api.repository.AllRequestViewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AllRequestFinder {

  private final DashboardRequestSpecFactory specFactory;
  private final AllRequestViewRepository allRequestViewRepository;
  private final DummyNullDateFilter nullDateFilter;

  public Page<AllRequestView> getAllRequestsPage(
      SpecificDashboardRequestFiltersDTO filters, Pageable pageable) {
    Specification<AllRequestView> spec = specFactory.toSpec(filters);
    return allRequestViewRepository
        .findAll(spec, pageable)
        .map(view -> view.removeDummyNullDates(nullDateFilter));
  }
}

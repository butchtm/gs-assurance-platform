package org.goldstandard.assurance.api.service;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.CommonDashboardRequestFiltersDTO;
import org.goldstandard.assurance.api.entity.view.CommonDashboardView;
import org.goldstandard.assurance.api.repository.CommonDashboardViewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommonDashboardFinder {

  private final DashboardRequestSpecFactory specFactory;
  private final CommonDashboardViewRepository repository;
  private final DummyNullDateFilter nullDateFilter;

  public Page<CommonDashboardView> getCommonDashboardPage(
      CommonDashboardRequestFiltersDTO filters, Pageable pageable) {
    Specification<CommonDashboardView> spec = specFactory.toSpec(filters);
    return repository
        .findAll(spec, pageable)
        .map(view -> view.removeDummyNullDates(nullDateFilter));
  }
}

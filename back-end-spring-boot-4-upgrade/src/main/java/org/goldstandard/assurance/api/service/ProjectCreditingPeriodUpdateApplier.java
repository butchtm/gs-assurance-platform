package org.goldstandard.assurance.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestMonitoringPeriod;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.util.CustomCollectionUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectCreditingPeriodUpdateApplier {

  private final CreditingPeriodFactory creditingPeriodFactory;

  public Collection<ProjectCreditingPeriod> createCreditingPeriodIfRequired(
      Collection<ProjectCreditingPeriod> periods, Request request, Project project) {
    var draftPeriod = request.getDraftCreditingPeriod();
    if (draftPeriod.isEmpty()) {
      return periods;
    }
    var newPeriod = createCreditingPeriod(request, draftPeriod.get(), project);
    var updatedPeriods = Optional.ofNullable(periods).map(ArrayList::new).orElse(new ArrayList<>());
    updatedPeriods.add(newPeriod);
    return updatedPeriods;
  }

  public Collection<ProjectCreditingPeriod> updateProjectMethodologies(
      Collection<ProjectCreditingPeriod> creditingPeriods, Request request) {
    return creditingPeriods.stream()
        .map(period -> updateCreditingPeriodMethodologies(period, request))
        .toList();
  }

  public List<ProjectCreditingPeriod> updateMonitoringPeriods(
      Collection<ProjectCreditingPeriod> creditingPeriods, Request request) {
    var newCreditingPeriods = new ArrayList<>(CustomCollectionUtils.nullSafeCopy(creditingPeriods));
    var index = getCreditingPeriodIndex(newCreditingPeriods, request.getCreditingPeriod());
    var creditingPeriod = newCreditingPeriods.get(index);
    var draftMonitoringPeriod = toDraftMonitoringPeriod(request);
    var updatedCreditingPeriod =
        updateCreditingPeriod(creditingPeriod, draftMonitoringPeriod, request.getId());
    newCreditingPeriods.set(index, updatedCreditingPeriod);
    return newCreditingPeriods;
  }

  private RequestMonitoringPeriod toDraftMonitoringPeriod(Request request) {
    return request
        .getDraftMonitoringPeriod()
        .orElseThrow(
            () ->
                new NotFoundException(
                    "Request " + request.getId() + " does not have a draft Monitoring Period"));
  }

  private ProjectCreditingPeriod updateCreditingPeriod(
      ProjectCreditingPeriod creditingPeriod,
      RequestMonitoringPeriod draftMonitoringPeriod,
      Integer requestId) {
    if (draftMonitoringPeriod.getPeriodId() == null) {
      return creditingPeriodFactory.addMonitoringPeriod(
          creditingPeriod, requestId, draftMonitoringPeriod);
    }
    return creditingPeriodFactory.updateMonitoringPeriod(creditingPeriod, draftMonitoringPeriod);
  }

  private ProjectCreditingPeriod createCreditingPeriod(
      Request updated, RequestCreditingPeriod draftPeriod, Project project) {
    var newPeriod =
        ProjectCreditingPeriod.builder()
            .projectId(project.getId())
            .periodId(project.getNextCreditingPeriodId())
            .startDate(draftPeriod.getStartDate())
            .endDate(draftPeriod.getEndDate())
            .build();
    var methodologies =
        creditingPeriodFactory.toProjectCreditingPeriodMethodologies(
            newPeriod, updated.getRequestMethodologies());
    return newPeriod.withMethodologies(methodologies);
  }

  private ProjectCreditingPeriod updateCreditingPeriodMethodologies(
      ProjectCreditingPeriod period, Request request) {
    if (Objects.equals(period.getPeriodId(), request.getCreditingPeriod())) {
      return period.withMethodologies(
          creditingPeriodFactory.toProjectCreditingPeriodMethodologies(
              period, request.getRequestMethodologies()));
    }
    return period;
  }

  private static Integer getCreditingPeriodIndex(
      List<ProjectCreditingPeriod> creditingPeriods, Integer creditingId) {
    for (int i = 0; i < creditingPeriods.size(); i++) {
      var period = creditingPeriods.get(i);
      if (Objects.equals(period.getPeriodId(), creditingId)) {
        return i;
      }
    }
    throw new NotFoundException("Crediting Period " + creditingId + " not found");
  }
}

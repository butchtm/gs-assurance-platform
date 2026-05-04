package org.goldstandard.assurance.api.service;

import java.util.Collection;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectCreditingPeriodUpdatePolicy {

  private final ProjectCreditingPeriodUpdateApplier creditingPeriodUpdateApplier;

  public Collection<Project> calculateProjectCreditingPeriodUpdates(
      Collection<Request> originalRequests,
      Collection<Request> updatedRequests,
      Collection<Project> projects) {
    return updatedRequests.stream()
        .map(
            updatedRequest ->
                calculateProjectCreditingPeriodUpdates(originalRequests, updatedRequest, projects))
        .toList();
  }

  private Project calculateProjectCreditingPeriodUpdates(
      Collection<Request> originalRequests, Request updatedRequest, Collection<Project> projects) {
    var originalRequest = find(originalRequests, updatedRequest.getId());
    var project = find(projects, updatedRequest);
    return calculateProjectCreditingPeriodUpdates(originalRequest, updatedRequest, project);
  }

  private Project calculateProjectCreditingPeriodUpdates(
      Request original, Request updated, Project project) {
    var creditingPeriods = project.getCreditingPeriods();
    if (shouldCreateCreditingPeriod(original, updated)) {
      creditingPeriods =
          creditingPeriodUpdateApplier.createCreditingPeriodIfRequired(
              creditingPeriods, updated, project);
    }
    if (shouldUpdateProjectMethodologies(original, updated)) {
      creditingPeriods =
          creditingPeriodUpdateApplier.updateProjectMethodologies(creditingPeriods, updated);
    }
    if (shouldCreateMonitoringPeriod(original, updated)
        || shouldUpdateMonitoringPeriod(original, updated)) {
      creditingPeriods =
          creditingPeriodUpdateApplier.updateMonitoringPeriods(creditingPeriods, updated);
    }

    return project.withCreditingPeriods(creditingPeriods);
  }

  private static Request find(Collection<Request> requests, Integer id) {
    return requests.stream().filter(r -> Objects.equals(r.getId(), id)).findFirst().orElseThrow();
  }

  private static Project find(Collection<Project> projects, Request request) {
    return projects.stream()
        .filter(p -> Objects.equals(p.getId(), request.getProject().getId()))
        .findFirst()
        .orElseThrow();
  }

  private static boolean shouldUpdateProjectMethodologies(Request original, Request updated) {
    return isNoLongerActive(original, updated)
        && updated.hasMethodologies()
        && updated.getRequestType().allowsMethodologies();
  }

  private static boolean shouldCreateCreditingPeriod(Request original, Request updated) {
    return hasBeenCompleted(original, updated)
        && updated.hasDraftCreditingPeriod()
        && updated.getRequestType().allowsCreateCreditingPeriods();
  }

  private static boolean isNoLongerActive(Request original, Request updated) {
    return hasBeenCompleted(original, updated) || hasBeenRejected(original, updated);
  }

  private static boolean shouldCreateMonitoringPeriod(Request original, Request updated) {
    return hasBeenCompleted(original, updated)
        && updated.isCreateMonitoringPeriod()
        && updated.getRequestType().allowsCreateMonitoringPeriod();
  }

  private static boolean shouldUpdateMonitoringPeriod(Request original, Request updated) {
    return hasBeenCompleted(original, updated)
        && updated.isUpdateMonitoringPeriod()
        && updated.getRequestType().allowsUpdateMonitoringPeriod();
  }

  private static boolean hasBeenCompleted(Request original, Request updated) {
    return original.isActive() && updated.hasCompletedApprovedStatus();
  }

  private static boolean hasBeenRejected(Request original, Request updated) {
    return original.isActive() && updated.hasRejectedStatus();
  }
}

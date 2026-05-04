package org.goldstandard.assurance.api.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Project;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProjectCurrentRequestUpdatePolicy {

  private final RequestFinder requestFinder;

  public boolean shouldUpdateCurrentRequest(Project project) {
    var requestId = project.getCurrentRequestId();
    if (Objects.isNull(requestId)) {
      return true;
    }
    var request = requestFinder.getRequestById(requestId);
    return !request.hasActiveOrCompletedCurrentStatus();
  }
}

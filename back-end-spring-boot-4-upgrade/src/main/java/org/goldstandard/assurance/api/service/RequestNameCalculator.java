package org.goldstandard.assurance.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestNameCalculator {

  private final RequestRepository repository;

  public String calculateName(int requestTypeId, int requestCountByType) {
    String description = repository.findRequestDescriptionForTypeId(requestTypeId);
    String name = appendRequestNameSuffix(description, requestCountByType + 1);
    log.debug(
        "calculated request name {} for request type id {} and count by type {}",
        name,
        requestTypeId,
        requestCountByType);
    return name;
  }

  private String appendRequestNameSuffix(String name, int number) {
    return String.format("%s no. %d", name, number);
  }
}

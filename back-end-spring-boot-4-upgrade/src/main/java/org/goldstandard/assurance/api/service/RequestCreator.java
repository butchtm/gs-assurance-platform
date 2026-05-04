package org.goldstandard.assurance.api.service;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO;
import org.goldstandard.assurance.api.entity.Request;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestCreator {

  private final RequestFactory factory;
  private final CreateRequestTransaction transaction;

  public Request create(Integer projectId, RequestCreateUpdateDTO create) {
    var request = factory.buildRequest(projectId, create);
    return transaction.performCreate(request);
  }
}

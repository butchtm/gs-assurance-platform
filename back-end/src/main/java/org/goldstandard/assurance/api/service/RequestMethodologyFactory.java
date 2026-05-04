package org.goldstandard.assurance.api.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.goldstandard.assurance.api.dto.RequestMethodologyDTO;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestMethodology;
import org.goldstandard.assurance.api.entity.RequestMethodologyId;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.repository.MethodologyRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestMethodologyFactory {

  private final MethodologyRepository repository;

  public List<RequestMethodology> toMethodologies(
      Collection<RequestMethodologyDTO> dtos, Request request) {
    if (CollectionUtils.isEmpty(dtos)) {
      return Collections.emptyList();
    }
    return dtos.stream().map(dto -> toMethodology(dto, request)).toList();
  }

  private RequestMethodology toMethodology(RequestMethodologyDTO dto, Request request) {
    var methodology = repository.forceFindById(dto.getMethodologyId());
    return RequestMethodology.builder()
        .methodology(methodology)
        .methodologyVersion(dto.getMethodologyVersion())
        .request(request)
        .id(toId(methodology, request))
        .build();
  }

  private RequestMethodologyId toId(Methodology methodology, Request request) {
    return RequestMethodologyId.builder()
        .methodologyId(methodology.getId())
        .requestId(request.getId())
        .build();
  }
}

package org.goldstandard.assurance.api.service;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.springframework.stereotype.Component;

@Component
public class ConsultationAuditInfoFactory {

  public Map<String, Object> build(
      int requestId, Collection<StakeholderConsultation> consultations) {
    return Map.of("requestId", requestId, "consultationIds", toIdsString(consultations));
  }

  private static String toIdsString(Collection<StakeholderConsultation> consultations) {
    return consultations.stream()
        .map(StakeholderConsultation::getId)
        .map(Object::toString)
        .collect(Collectors.joining(","));
  }
}

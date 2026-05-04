package org.goldstandard.assurance.api.service;

import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.ConsultationFiltersDTO;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StakeholderConsultationSpecFactory {

  public Specification<StakeholderConsultation> toSpec(ConsultationFiltersDTO filters) {
    Specification<StakeholderConsultation> spec = Specification.unrestricted();
    var typeId = filters.getConsultationTypeId();
    if (Objects.nonNull(typeId)) {
      spec = spec.and(hasTypeId(typeId));
    }
    var liveAt = filters.getLiveAt();
    if (Objects.nonNull(liveAt)) {
      spec = spec.and(isLiveAt(liveAt));
    }
    return spec;
  }

  private static Specification<StakeholderConsultation> hasTypeId(Integer typeId) {
    return (consultation, cq, cb) ->
        cb.equal(consultation.get("consultationType").get("id"), typeId);
  }

  private static Specification<StakeholderConsultation> isLiveAt(ZonedDateTime now) {
    return startedBefore(now).and(endsAfter(now));
  }

  private static Specification<StakeholderConsultation> startedBefore(ZonedDateTime now) {
    return (consultation, cq, cb) -> cb.lessThanOrEqualTo(consultation.get("startDate"), now);
  }

  private static Specification<StakeholderConsultation> endsAfter(ZonedDateTime now) {
    return (consultation, cq, cb) -> cb.greaterThanOrEqualTo(consultation.get("endDate"), now);
  }
}

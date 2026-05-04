package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(RequestStatusTransitionId.class)
public class RequestStatusTransition {
  @Id private Integer requestStatusId;
  @Id private Integer permittedRequestStatusId;
  private Integer minimumRoundOfReviewCount;
  private boolean permittedDuringConsultation;
}

package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class RequestStatusTransitionId implements Serializable {
  private Integer requestStatusId;
  private Integer permittedRequestStatusId;
}

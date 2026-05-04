package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@EqualsAndHashCode
@Embeddable
@ToString
public class RequestMethodologyId implements Serializable {
  private final Integer requestId;
  private final Integer methodologyId;
}

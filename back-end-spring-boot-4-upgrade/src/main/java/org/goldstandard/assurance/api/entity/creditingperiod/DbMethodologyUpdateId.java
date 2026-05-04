package org.goldstandard.assurance.api.entity.creditingperiod;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Data
@Embeddable
public class DbMethodologyUpdateId implements Serializable {
  private final Integer projectMethodologyUpdateId;
  private final Integer methodologyId;
}

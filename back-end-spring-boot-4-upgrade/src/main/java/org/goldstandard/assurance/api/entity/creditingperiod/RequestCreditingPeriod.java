package org.goldstandard.assurance.api.entity.creditingperiod;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;

@Builder(toBuilder = true)
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Getter
@Entity
@ToString
public class RequestCreditingPeriod {

  @Id
  @Column(nullable = false)
  @With
  private final Integer requestId;

  @Column(nullable = false)
  private final ZonedDateTime startDate;

  @Column(nullable = false)
  private final ZonedDateTime endDate;
}

package org.goldstandard.assurance.api.entity.view;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Entity(name = "v_public_requests")
public class PublicRequestsView {
  private final String gsid;
  @Id private final Integer requestId;
  private final String requestName;
  private final Integer requestIndex;
  private final Integer requestTypeId;
  private final String requestTypeDescription;
  private final ZonedDateTime consultationStartDate;
  private final ZonedDateTime consultationEndDate;
}

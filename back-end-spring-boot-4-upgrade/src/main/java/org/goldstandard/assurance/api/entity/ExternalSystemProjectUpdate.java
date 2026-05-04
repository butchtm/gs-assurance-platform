package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder(toBuilder = true)
@Entity
@Getter
@IdClass(ExternalSystemProjectUpdateId.class)
public class ExternalSystemProjectUpdate {

  @Id private final int projectId;
  @Id private final String destinationSystem;

  private final ZonedDateTime createdTimestamp;
  private final ZonedDateTime lastAttemptAt;
  private final int attempts;

  public ExternalSystemProjectUpdate recordFailedAttempt(ZonedDateTime now) {
    return toBuilder().attempts(attempts + 1).lastAttemptAt(now).build();
  }

  public boolean attemptsGreaterThanOrEqualTo(int maxRetries) {
    return attempts >= maxRetries;
  }
}

package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.With;

@Entity
@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder(toBuilder = true)
@With
public class DocumentPiiAnalysis {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private final Integer id;

  private final UUID documentId;
  private final Integer statusId;
  private final String comprehendJobId;
  private final String comprehendJobStatus;
  private final ZonedDateTime startedAt;
  private final ZonedDateTime textractFinishedAt;
  private final ZonedDateTime comprehendFinishedAt;
  private final long textractDurationMs;
  private final long comprehendDurationMs;
}

package org.goldstandard.assurance.api.entity.pii;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.enumeration.PiiScanStatus;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Entity
public class PiiScanRun {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private ZonedDateTime startedAt;
  private ZonedDateTime finishedAt;

  @Enumerated(EnumType.STRING)
  private PiiScanStatus status;

  private Long duration;
  private String errorMessage;
}

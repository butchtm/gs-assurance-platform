package org.goldstandard.assurance.api.entity.pii;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.enumeration.PiiProbability;
import org.goldstandard.assurance.api.enumeration.PiiType;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Entity
@Table(name = "pii_finding", schema = "gs")
public class PiiFinding {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne private PiiScanRun piiScanRun;
  private String entityType;
  private Integer projectId;
  private String fieldName;

  @Enumerated(EnumType.STRING)
  private PiiType piiType;

  @Enumerated(EnumType.STRING)
  private PiiProbability probability;

  private ZonedDateTime createdAt;
}

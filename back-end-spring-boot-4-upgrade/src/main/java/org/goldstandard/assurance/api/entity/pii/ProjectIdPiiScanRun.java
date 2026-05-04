package org.goldstandard.assurance.api.entity.pii;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.enumeration.ProjectScanStatus;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"pii_scan_run_id", "project_id"}))
public class ProjectIdPiiScanRun {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne private PiiScanRun piiScanRun;

  private Integer projectId;

  @Enumerated(EnumType.STRING)
  private ProjectScanStatus status;
}

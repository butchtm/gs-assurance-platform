package org.goldstandard.assurance.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Entity
@Table(name = "project_risk_summary", schema = "gs")
public class ProjectRiskSummary {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @Column(name = "risk", nullable = false)
  private Boolean risk;

  @Column(name = "summary", nullable = false, length = 255)
  private String summary;

  @Column(name = "date_applied", nullable = false)
  private LocalDateTime dateApplied;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}

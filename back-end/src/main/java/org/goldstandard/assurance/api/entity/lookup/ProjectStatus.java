package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import java.util.Objects;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Slf4j
@Setter
public class ProjectStatus extends ReferenceDataType {

  public static final int NEW = 1;
  public static final int LISTED = 2;
  public static final int DESIGN_CERTIFIED = 3;
  public static final int CERTIFIED_PROJECT = 4;

  private int sequence;

  public ProjectStatus() {}

  public ProjectStatus(Integer id) {
    this.id = id;
  }

  public boolean isNew() {
    return Objects.equals(id, NEW);
  }

  public boolean isAfter(ProjectStatus otherStatus) {
    boolean after = sequence > otherStatus.sequence;
    log.info(
        "project status {} with sequence {} is after {} with sequence {} {}",
        code,
        sequence,
        otherStatus.code,
        otherStatus.sequence,
        after);
    return after;
  }

  public boolean isListed() {
    return this.id.equals(LISTED);
  }

  public boolean isDesignCertified() {
    return this.id.equals(DESIGN_CERTIFIED);
  }

  public boolean isCertifiedProject() {
    return this.id.equals(CERTIFIED_PROJECT);
  }

  public static ProjectStatus newStatus() {
    return new ProjectStatus(NEW);
  }
}

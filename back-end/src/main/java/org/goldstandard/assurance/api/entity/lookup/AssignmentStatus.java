package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import java.util.Set;

@Entity
public class AssignmentStatus extends ReferenceDataType {
  public static final Integer PENDING = 0;
  public static final Integer ACCEPTED = 1;
  public static final Integer CANCELLED = 2;
  public static final Set<Integer> ALL = Set.of(PENDING, ACCEPTED, CANCELLED);

  public AssignmentStatus() {}

  public AssignmentStatus(Integer id) {
    this.id = id;
  }
}

package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;

@Entity
public class AssignmentType extends ReferenceDataType {
  public static final Integer AUDIT_ASSIGNMENT_TYPE_ID = 0;
  public static final Integer REVIEW_ASSIGNMENT_TYPE_ID = 1;
  public static final Integer QAQC_ASSIGNMENT_TYPE_ID = 2;
  public static final Integer COLLECT_FEE_ASSIGNMENT_TYPE_ID = 3;
  public static final Integer COMPLETENESS_ASSIGNMENT_TYPE_ID = 4;
  public static final Integer STAKEHOLDER_CONSULTATION_ASSIGNMENT_TYPE_ID = 5;

  public AssignmentType() {}

  public AssignmentType(Integer id) {
    this.id = id;
  }

  public boolean isAuditAssignment() {
    return this.id.equals(AUDIT_ASSIGNMENT_TYPE_ID);
  }
}

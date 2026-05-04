package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;

@Entity
public class TransitionApprovalProcedure extends ReferenceDataType {

  public TransitionApprovalProcedure() {}

  public TransitionApprovalProcedure(Integer id) {
    this.id = id;
  }
}

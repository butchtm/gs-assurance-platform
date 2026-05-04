package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(RequestTypeAssignmentTypeId.class)
public class RequestTypeAssignmentType {
  @Id private Integer requestTypeId;
  @Id private Integer assignmentTypeId;
  private Boolean mandatory;
}

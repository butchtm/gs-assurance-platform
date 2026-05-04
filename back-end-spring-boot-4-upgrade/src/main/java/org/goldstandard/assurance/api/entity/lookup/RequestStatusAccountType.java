package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(RequestStatusAccountTypeId.class)
public class RequestStatusAccountType {
  @Id private Integer requestStatusId;
  @Id private Integer accountTypeId;
  private Boolean canProgress;
}

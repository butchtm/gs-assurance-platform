package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class RequestTypeConsultation {

  @Id private Integer requestTypeId;
  private Boolean requiresConsultation; // null == consultation is optional
}

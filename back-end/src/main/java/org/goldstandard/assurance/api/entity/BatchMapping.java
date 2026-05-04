package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.goldstandard.assurance.api.entity.lookup.BatchType;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.lookup.RequestType;

@Getter
@Setter
@Entity
@IdClass(BatchMappingId.class)
public class BatchMapping {
  @Id @ManyToOne private BatchType batchType;
  @Id @ManyToOne private RequestType requestType;
  @Id @ManyToOne private ProjectClassification projectClassification;

  public Integer getBatchTypeId() {
    return batchType.getId();
  }
}

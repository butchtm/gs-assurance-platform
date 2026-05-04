package org.goldstandard.assurance.api.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.BatchType;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.lookup.RequestType;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BatchMappingId implements Serializable {
  private BatchType batchType;
  private RequestType requestType;
  private ProjectClassification projectClassification;
}

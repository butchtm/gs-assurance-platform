package org.goldstandard.assurance.api.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BatchProjectsDTO {
  private final BatchProjectDTO parent;
  private final List<BatchProjectDTO> children;

  public BatchProjectsDTO clearCreditingPeriods() {
    return BatchProjectsDTO.builder()
        .parent(parent.clearCreditingPeriods())
        .children(children.stream().map(BatchProjectDTO::clearCreditingPeriods).toList())
        .build();
  }
}

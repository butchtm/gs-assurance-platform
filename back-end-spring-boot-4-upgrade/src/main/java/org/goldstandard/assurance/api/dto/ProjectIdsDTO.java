package org.goldstandard.assurance.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProjectIdsDTO {

  @NotNull(message = "Project IDs list cannot be null")
  @NotEmpty(message = "Project IDs list cannot be empty")
  private List<Integer> projectIds;
}

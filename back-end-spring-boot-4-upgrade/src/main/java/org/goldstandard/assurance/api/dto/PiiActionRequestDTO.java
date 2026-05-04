package org.goldstandard.assurance.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.entity.lookup.PiiAction;
import org.goldstandard.assurance.api.validation.ValidPiiActionRequest;

@Getter
@Builder
@ValidPiiActionRequest
public class PiiActionRequestDTO {
  private final Integer projectId;
  private final Integer requestId;
  private final UUID documentId;
  private final Integer commentId;

  @NotNull private final PiiAction piiAction;
}

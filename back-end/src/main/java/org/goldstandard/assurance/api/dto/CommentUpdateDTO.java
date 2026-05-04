package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.entity.lookup.PiiAction;

@Getter
@Builder
public class CommentUpdateDTO {
  private Integer id;
  private Boolean publicAccess;
  private Boolean anonymous;
  private Boolean addressed;
  private Boolean suppressed;
  private Integer suppressionReasonId;
  private final PiiAction piiAction;

  public boolean isPublicAccess() {
    return Boolean.TRUE.equals(publicAccess);
  }
}

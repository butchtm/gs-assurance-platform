package org.goldstandard.assurance.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CommentCreateDTO {
  private Integer requestId;
  private String comment;
  private String usersName;
  private String usersEmail;
  private Integer accountId;
  private boolean publicAccess;
  private boolean anonymous;
  private TextAnalysisResultsDTO textAnalysisResults;
}

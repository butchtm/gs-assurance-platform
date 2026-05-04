package org.goldstandard.assurance.api.dto;

import jakarta.annotation.Nullable;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.goldstandard.assurance.api.dto.lookup.AccountTypeDTO;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
  private Integer id;
  private Integer requestId;
  private String submittedComment;
  private String uploadedByName;
  private String uploadedByEmail;
  private Integer accountId;
  private AccountTypeDTO accountType;
  private ZonedDateTime uploadedDate;
  private boolean publicAccess;
  private boolean anonymous;
  private boolean addressed;
  private boolean suppressed;
  private Integer suppressionReasonId;
  private boolean deleted;
  private Integer deletionReasonId;
  @Nullable private PiiMetadataDTO piiMetadata;
}

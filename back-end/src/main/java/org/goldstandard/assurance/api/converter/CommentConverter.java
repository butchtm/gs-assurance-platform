package org.goldstandard.assurance.api.converter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.CommentDTO;
import org.goldstandard.assurance.api.dto.lookup.AccountTypeDTO;
import org.goldstandard.assurance.api.entity.Comment;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentConverter {

  private final PiiMetadataConverter piiMetadataConverter;
  private final AccountTypeConverter accountTypeConverter;

  public Optional<CommentDTO> toDto(Optional<Comment> comment) {
    return comment.map(this::toDto);
  }

  public CommentDTO toDto(Comment comment) {
    AccountTypeDTO typeDto = null;
    Integer id = null;
    if (comment.getAccount() != null) {
      typeDto = accountTypeConverter.toDto(comment.getAccount().getAccountType());
      id = comment.getAccount().getId();
    }
    var metadataDTO =
        Optional.ofNullable(comment.getPiiMetadata()).map(piiMetadataConverter::toDto).orElse(null);
    return CommentDTO.builder()
        .id(comment.getId())
        .requestId(comment.getRequestId())
        .submittedComment(comment.getSubmittedComment())
        .accountType(typeDto)
        .accountId(id)
        .uploadedByName(comment.getUploadedByName())
        .uploadedByEmail(comment.getUploadedByEmail())
        .uploadedDate(comment.getUploadedDate())
        .anonymous(comment.isAnonymous())
        .publicAccess(comment.isPublicAccess())
        .suppressed(comment.isSuppressed())
        .suppressionReasonId(comment.getSuppressionReasonId())
        .deleted(comment.isDeleted())
        .deletionReasonId(comment.getDeletionReasonId())
        .addressed(comment.isAddressed())
        .piiMetadata(metadataDTO)
        .build();
  }
}

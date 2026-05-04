package org.goldstandard.assurance.api.service;

import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.CommentUpdateDTO;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommentPublicAccessValidator {

  public void validatePublicAccess(Comment originalComment, CommentUpdateDTO dto) {
    var publicAccess = dto.isPublicAccess();
    if (!publicAccess) {
      log.debug("no validation required as comment is private");
      return;
    }
    var piiMetadata = originalComment.getPiiMetadata();
    if (Objects.isNull(piiMetadata)) {
      log.debug("no validation required as comment has no pii identified");
      return;
    }
    var piiAction = Optional.ofNullable(dto.getPiiAction()).orElse(piiMetadata.getAction());
    if (piiAction.publicVisibilityAcceptable()) {
      log.debug(
          "public visibility of comment {} is allowed due to pii action {}",
          originalComment.getId(),
          piiAction.getId());
      return;
    }
    throw new BadRequestException(
        "comment %d cannot be made public due to pii action %d",
        originalComment.getId(), piiAction.getId());
  }
}

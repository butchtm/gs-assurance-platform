package org.goldstandard.assurance.api.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.CommentCreateDTO;
import org.goldstandard.assurance.api.dto.CommentDelete;
import org.goldstandard.assurance.api.dto.CommentUpdateDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.entity.lookup.SuppressionReason;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.CommentRepository;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.goldstandard.assurance.api.service.notification.CommentNotificationService;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

  public static final String DELETED = "Comment deleted";
  public static final String COMMENT_NOT_FOUND = "Comment with id [%s] not found.";

  private final CommentRepository commentRepository;
  private final AccountFinder accountFinder;
  private final RequestFinder requestFinder;
  private final AuthenticatedUserSupplier authenticatedUserSupplier;
  private final CommentAccessPolicy accessPolicy;
  private final CommentNotificationService commentNotificationService;
  private final CommentTransaction transaction;
  private final CommentFinder commentFinder;
  private final CommentPublicAccessValidator publicAccessValidator;

  public List<Comment> getCommentsByRequestId(Integer requestId) {
    var lookupRequestId = requestFinder.getParentRequestIdOrRequestId(requestId);
    log.info(
        "getting comments for requestId {} and lookup request id {}", requestId, lookupRequestId);
    return commentFinder.getCommentsByRequestId(lookupRequestId);
  }

  public Comment saveNewComment(CommentCreateDTO dto) {
    final var request = requestFinder.getRequestById(dto.getRequestId());
    if (request.isBatchChild()) {
      throw new BadRequestException("Comments cannot be submitted on child batch requests");
    }

    Account account = null;
    String name = dto.getUsersName();
    String email = dto.getUsersEmail();
    if (dto.getAccountId() != null) {
      account = accountFinder.forceGetAccountById(dto.getAccountId());
      name = account.getName();
      email = authenticatedUserSupplier.getAuthenticatedUserEmailAddress().orElse(null);
    }

    if (email == null || email.isEmpty()) {
      throw new BadRequestException("User has no email, comment cannot be uploaded.");
    }

    var comment =
        Comment.builder()
            .requestId(request.getId())
            .submittedComment(dto.getComment())
            .uploadedByName(name)
            .uploadedByEmail(email)
            .account(account)
            .uploadedDate(ZonedDateTime.now())
            .publicAccess(false)
            .anonymous(dto.isAnonymous())
            .addressed(false)
            .build();
    var savedComment = transaction.createComment(comment, dto.getTextAnalysisResults());
    commentNotificationService.sendCommentNotification(savedComment, request);
    return accessPolicy.anonymiseIfRequired(savedComment);
  }

  public Optional<Comment> updateComment(CommentUpdateDTO dto) {
    var originalComment =
        commentRepository
            .findById(dto.getId())
            .orElseThrow(() -> new NotFoundException(COMMENT_NOT_FOUND, dto.getId()));
    var builder = originalComment.toBuilder();

    if (dto.getAddressed() != null) {
      builder.addressed(dto.getAddressed());
    }
    if (dto.getAnonymous() != null) {
      builder.anonymous(dto.getAnonymous());
    }
    if (dto.getPublicAccess() != null) {
      publicAccessValidator.validatePublicAccess(originalComment, dto);
      builder.publicAccess(dto.getPublicAccess());
    }
    if (dto.getSuppressed() != null) {
      builder.suppressed(dto.getSuppressed());
    }
    if (dto.getSuppressionReasonId() != null) {
      builder.suppressionReason(new SuppressionReason(dto.getSuppressionReasonId()));
    }
    var updatedComment = builder.build();
    log.info(
        "Updating comment with id [{}] against requestId [{}].",
        originalComment.getId(),
        originalComment.getRequestId());
    var savedComment =
        transaction.updateComment(originalComment, updatedComment, dto.getPiiAction());
    return accessPolicy.process(savedComment);
  }

  public void deleteComment(CommentDelete commentDelete) {
    var commentToDelete =
        commentRepository
            .findById(commentDelete.getId())
            .orElseThrow(() -> new NotFoundException(COMMENT_NOT_FOUND, commentDelete.getId()));
    log.info(
        "Deleting original submitters name, email and comment for id [{}].", commentDelete.getId());
    var deletedComment = commentToDelete.delete(commentDelete.getDeletionReasonId());
    transaction.saveCommentAsDeleted(deletedComment);
  }
}

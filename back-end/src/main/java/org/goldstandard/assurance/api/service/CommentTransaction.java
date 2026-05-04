package org.goldstandard.assurance.api.service;

import jakarta.transaction.Transactional;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.TextAnalysisResultsDTO;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.entity.lookup.PiiAction;
import org.goldstandard.assurance.api.repository.CommentRepository;
import org.goldstandard.assurance.api.service.pii.PiiMetadataPersistor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentTransaction {

  private final CommentRepository commentRepository;
  private final AuditService auditService;
  private final PiiMetadataPersistor piiMetadataPersistor;

  @Transactional
  public Comment createComment(Comment comment, TextAnalysisResultsDTO dto) {
    var savedComment = commentRepository.save(comment);
    log.info(
        "saved comment with id {} against request with id {}",
        savedComment.getId(),
        savedComment.getRequestId());
    piiMetadataPersistor.persistCommentPii(dto, comment);
    sendAuditEvent(savedComment, AuditEventSubject.COMMENT_UPDATE);
    return savedComment;
  }

  @Transactional
  public Comment updateComment(Comment original, Comment updated, PiiAction piiAction) {
    var savedComment = commentRepository.save(updated);
    sendAuditEvent(savedComment, AuditEventSubject.COMMENT_UPDATE);
    piiMetadataPersistor.persist(original.getPiiMetadata(), piiAction);
    return savedComment;
  }

  @Transactional
  public void saveCommentAsDeleted(Comment comment) {
    commentRepository.save(comment);
    sendAuditEvent(comment, AuditEventSubject.COMMENT_DELETE);
  }

  private void sendAuditEvent(Comment comment, AuditEventSubject subject) {
    auditService.auditEvent(
        subject, createCommentAuditInformation(comment.getRequestId(), comment.getId()));
  }

  private Map<String, Object> createCommentAuditInformation(Integer requestId, Integer commentId) {
    return Map.ofEntries(Map.entry("requestId", requestId), Map.entry("commentId", commentId));
  }
}

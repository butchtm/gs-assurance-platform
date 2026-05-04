package org.goldstandard.assurance.api.dto.pub;

import java.util.List;
import org.goldstandard.assurance.api.dto.pub.stakeholder.attributes.CommentDTO;
import org.goldstandard.assurance.api.dto.pub.stakeholder.attributes.DocumentDTO;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.entity.view.PublicDocumentView;
import org.goldstandard.assurance.api.entity.view.PublicRequestsView;
import org.springframework.stereotype.Component;

@Component
public class PublicDataMapper {

  public PublicRequest toPublicRequest(
      PublicRequestsView request, List<PublicDocumentView> documents, List<Comment> comments) {
    return PublicRequest.builder()
        .id(request.getRequestId())
        .name(request.getRequestName())
        .index(request.getRequestIndex())
        .requestType(request.getRequestTypeDescription())
        .consultationStartDate(request.getConsultationStartDate())
        .consultationEndDate(request.getConsultationEndDate())
        .documents(toPublicDocuments(documents))
        .comments(toPublicComments(comments))
        .build();
  }

  private List<DocumentDTO> toPublicDocuments(List<PublicDocumentView> documents) {
    return documents.stream().map(this::toPublicDocument).toList();
  }

  private DocumentDTO toPublicDocument(PublicDocumentView document) {
    return DocumentDTO.builder()
        .id(document.getDocumentId())
        .filename(document.getDocumentFileName())
        .uploadedTimestamp(document.getDocumentUploaded())
        .modifiedTimestamp(document.getDocumentModified())
        .displayCategoryType(document.getDocumentDisplayCategoryDescription())
        .isMissing(document.getDocumentIsMissing())
        .build();
  }

  private List<CommentDTO> toPublicComments(List<Comment> comments) {
    return comments.stream().map(this::toPublicComment).toList();
  }

  private CommentDTO toPublicComment(Comment comment) {
    return CommentDTO.builder()
        .id(comment.getId())
        .uploadedByName(comment.getUploadedByName())
        .uploadedDate(comment.getUploadedDate())
        .submittedComment(comment.getSubmittedComment())
        .anonymous(comment.isAnonymous())
        .build();
  }
}

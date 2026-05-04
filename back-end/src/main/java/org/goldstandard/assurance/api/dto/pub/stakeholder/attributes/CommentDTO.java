package org.goldstandard.assurance.api.dto.pub.stakeholder.attributes;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.goldstandard.assurance.api.entity.view.stakeholder.attributes.RequestCommentView;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CommentDTO {
  private Integer id;
  private String submittedComment;
  private String uploadedByName;
  private ZonedDateTime uploadedDate;
  private boolean anonymous;
  private boolean addressed;

  public CommentDTO(RequestCommentView view) {
    this.id = view.getId();
    this.submittedComment = view.getSubmittedComment();
    this.uploadedByName = view.getUploadedByName();
    this.uploadedDate = view.getUploadedDate();
    this.anonymous = view.isAnonymous();
    this.addressed = view.isAddressed();
  }
}

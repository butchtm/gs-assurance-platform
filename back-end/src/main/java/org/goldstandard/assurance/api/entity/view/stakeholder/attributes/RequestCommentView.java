package org.goldstandard.assurance.api.entity.view.stakeholder.attributes;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity(name = "v_public_stakeholder_comments")
@NoArgsConstructor
@AllArgsConstructor
public class RequestCommentView {
  @Id private Integer id;
  private Integer requestId;
  private String submittedComment;
  private String uploadedByName;
  private ZonedDateTime uploadedDate;
  private boolean anonymous;
  private boolean addressed;
}

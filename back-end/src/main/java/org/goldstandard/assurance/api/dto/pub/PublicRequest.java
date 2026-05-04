package org.goldstandard.assurance.api.dto.pub;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.dto.pub.stakeholder.attributes.CommentDTO;
import org.goldstandard.assurance.api.dto.pub.stakeholder.attributes.DocumentDTO;

@Getter
@Builder
public class PublicRequest {
  private final Integer id;
  private final String name;
  private final Integer index;
  private final String requestType;
  private final List<DocumentDTO> documents;
  private final List<CommentDTO> comments;
  private final ZonedDateTime consultationStartDate;
  private final ZonedDateTime consultationEndDate;
}

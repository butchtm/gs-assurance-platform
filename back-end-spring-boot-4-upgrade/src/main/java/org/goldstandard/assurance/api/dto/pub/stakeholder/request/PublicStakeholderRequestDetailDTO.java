package org.goldstandard.assurance.api.dto.pub.stakeholder.request;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.goldstandard.assurance.api.dto.pub.stakeholder.attributes.CommentDTO;
import org.goldstandard.assurance.api.dto.pub.stakeholder.attributes.DocumentDTO;
import org.goldstandard.assurance.api.dto.pub.stakeholder.project.PublicStakeholderProjectDetailDTO;

@Getter
@Setter
@SuperBuilder
public class PublicStakeholderRequestDetailDTO extends PublicStakeholderRequestBasicDTO {
  private Integer requestCreditingPeriodId;
  private Integer emissionReductionAmount;
  private List<DocumentDTO> documents;
  private List<CommentDTO> comments;
  private List<PublicStakeholderProjectDetailDTO> batchedProjectChildren;
}

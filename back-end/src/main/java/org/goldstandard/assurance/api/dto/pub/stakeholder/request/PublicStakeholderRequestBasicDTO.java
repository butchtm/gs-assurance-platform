package org.goldstandard.assurance.api.dto.pub.stakeholder.request;

import java.time.ZonedDateTime;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.goldstandard.assurance.api.dto.CreditingPeriodDTO;
import org.goldstandard.assurance.api.dto.RequestMethodologyDTO;

@Getter
@Setter
@SuperBuilder
public class PublicStakeholderRequestBasicDTO {
  private Integer requestId;
  private String requestName;
  private String requestStatus;
  private Collection<RequestMethodologyDTO> requestMethodologies;
  private CreditingPeriodDTO requestCreditingPeriod;
  private ZonedDateTime consultationStartDate;
  private ZonedDateTime consultationEndDate;
  private Integer batchId;
}

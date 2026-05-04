package org.goldstandard.assurance.api.entity.view.stakeholder.request;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class StakeholderRequestBase {
  @Id private Integer requestId;
  private String gsid;
  private String requestName;
  private String requestStatus;
  private Integer requestCreditingPeriod;
  private Integer emissionReductionAmount;
  private ZonedDateTime consultationStartDate;
  private ZonedDateTime consultationEndDate;
  private Integer batchId;
}

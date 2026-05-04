package org.goldstandard.assurance.api.entity.view.stakeholder.request;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@Entity(name = "v_public_stakeholder_batch_children_requests")
@NoArgsConstructor
@AllArgsConstructor
public class StakeholderBatchRequestView extends StakeholderRequestBase {
  private Integer parentRequestId;
}

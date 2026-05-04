package org.goldstandard.assurance.api.entity.view.stakeholder.request;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@Entity(name = "v_public_stakeholder_requests")
@NoArgsConstructor
public class StakeholderRequestView extends StakeholderRequestBase {}

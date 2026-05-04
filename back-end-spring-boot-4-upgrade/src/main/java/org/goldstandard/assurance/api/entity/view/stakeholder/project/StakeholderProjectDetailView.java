package org.goldstandard.assurance.api.entity.view.stakeholder.project;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@Entity(name = "v_public_stakeholder_projects")
@AllArgsConstructor
public class StakeholderProjectDetailView extends ProjectBase {}

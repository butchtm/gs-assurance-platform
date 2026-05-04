package org.goldstandard.assurance.api.entity.view.stakeholder.project;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity(name = "v_public_stakeholder_batch_projects")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BatchProjectView extends ProjectBase {
  private Integer batchId;
}

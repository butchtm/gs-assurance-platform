package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Embeddable
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
public class ProjectStatusUpdateMappingId implements Serializable {

  @Column(name = "request_status_id", insertable = false, updatable = false)
  private final int requestStatusId;

  @Column(name = "request_type_id", insertable = false, updatable = false)
  private final int requestTypeId;

  @Column(name = "project_status_id", insertable = false, updatable = false)
  private final int projectStatusId;
}

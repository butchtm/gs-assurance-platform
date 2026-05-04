package org.goldstandard.assurance.api.entity.view;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Embeddable
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
@Getter
public class BatchParentIdViewId implements Serializable {

  @Column(name = "request_id", insertable = false, updatable = false)
  private final Integer requestId;

  @Column(name = "project_id", insertable = false, updatable = false)
  private final Integer projectId;
}

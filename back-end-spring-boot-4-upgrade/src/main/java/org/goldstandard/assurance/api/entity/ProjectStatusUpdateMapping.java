package org.goldstandard.assurance.api.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestType;

@Entity(name = "project_status_update_mapping")
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder
@Getter
public class ProjectStatusUpdateMapping {

  @EmbeddedId private final ProjectStatusUpdateMappingId projectStatusUpdateMappingId;

  @ManyToOne private final RequestStatus requestStatus;

  @ManyToOne private final RequestType requestType;

  @ManyToOne private final ProjectStatus projectStatus;
}

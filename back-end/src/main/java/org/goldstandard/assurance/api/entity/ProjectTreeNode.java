package org.goldstandard.assurance.api.entity;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.entity.lookup.Bundle;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;

@Getter
@Builder
public class ProjectTreeNode {

  private final String gsid;
  private final Integer id;
  private final String name;
  private final Integer accountId;
  private final Integer classificationId;
  private final Bundle bundle;
  private final ProjectStatus status;
  private final Collection<ProjectLocation> locations;
  private final Collection<ProjectType> types;
  private final Collection<ProjectTreeNode> children;
}

package org.goldstandard.assurance.api.dto;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;

@Getter
@Builder
@RequiredArgsConstructor
public class ProjectTreeNodeDTO {

  private final String gsid;
  private final Integer id;
  private final String name;
  private final Integer accountId;
  private final Integer classificationId;
  private final BundleDTO bundle;
  private final Collection<ProjectType> projectTypes;
  private final ProjectStatus projectStatus;
  private final Collection<CountryDTO> countries;
  private final Collection<ProjectTreeNodeDTO> children;
}

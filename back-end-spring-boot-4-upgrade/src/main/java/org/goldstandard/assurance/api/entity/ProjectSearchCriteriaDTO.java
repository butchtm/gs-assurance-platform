package org.goldstandard.assurance.api.entity;

import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectSearchCriteriaDTO {
  private final Integer bundleId;
  private final String name;
  private final String gsid;
  private final String search;
  private final Integer accountId;
  private final Integer masterProjectId;
  private final Integer parentProjectId;
  private final Set<Integer> projectStatusId;
  private final Set<Integer> projectIds;
}

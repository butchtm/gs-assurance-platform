package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.CarbonStream;
import org.goldstandard.assurance.api.entity.lookup.ProjectAsset;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectSize;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.goldstandard.assurance.api.entity.lookup.SustainableDevelopmentGoal;

@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
public class CandidateParentProjectDetailsDTO {

  private final Integer id;
  private final String gsid;
  private final String name;
  private final Integer projectClassificationId;
  private final ProjectClassification projectClassification;
  private final ProjectStatus projectStatus;
  private final ZonedDateTime projectStartDate;
  private final CarbonStream carbonStream;
  private final ProjectSize projectSize;
  private final List<ProjectType> projectTypes;
  private final List<ProjectLocation> projectLocations;
  private final List<ProjectAsset> projectAssets;
  private final List<SustainableDevelopmentGoal> sustainableDevelopmentGoals;
  private final List<CreditingPeriodDTO> creditingPeriods;
}

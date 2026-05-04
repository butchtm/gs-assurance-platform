package org.goldstandard.assurance.api.fixtures;

import static org.goldstandard.assurance.api.fixtures.PathwayMother.createPathwayGroup;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.activity.Activity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ActivityMother {

  public static Activity createActivity(
      int activityIndex, int pathwayGroupIndex, int pathwayIndex) {
    var pathwayGroup = createPathwayGroup(pathwayGroupIndex, pathwayIndex);
    return Activity.builder()
        .id(activityIndex)
        .index(activityIndex)
        .pathwayGroup(pathwayGroup)
        .build();
  }
}

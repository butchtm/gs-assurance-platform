package org.goldstandard.assurance.api.fixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.activity.Pathway;
import org.goldstandard.assurance.api.entity.activity.PathwayGroup;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PathwayMother {

  public static PathwayGroup createPathwayGroup(int groupIndex, int pathwayIndex) {
    var pathwayGroup = new PathwayGroup();
    pathwayGroup.setIndex(groupIndex);
    pathwayGroup.setId(groupIndex);
    var pathway = createPathway(pathwayIndex);
    pathwayGroup.setPathway(pathway);
    return pathwayGroup;
  }

  public static Pathway createPathway(int index) {
    var pathway = new Pathway();
    pathway.setIndex(index);
    pathway.setId(index);
    return pathway;
  }
}

package org.goldstandard.assurance.api.service.activity;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.activity.PathwayGroup;
import org.goldstandard.assurance.api.repository.activity.PathwayGroupRepository;
import org.goldstandard.assurance.api.repository.activity.PathwayRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PathwayGroupFinder {

  private final PathwayGroupRepository pathwayGroupRepository;
  private final PathwayRepository pathwayRepository;

  public Optional<PathwayGroup> findFirstPathwayGroup(int pathwayIndex) {
    var pathway = pathwayRepository.findByIndex(pathwayIndex);
    return pathwayGroupRepository.findFirstByPathwayOrderByIndexAsc(pathway);
  }

  public Optional<PathwayGroup> findByPathwayAndIndex(int pathwayIndex, int pathwayGroupIndex) {
    var pathway = pathwayRepository.findByIndex(pathwayIndex);
    return pathwayGroupRepository.findByPathwayAndIndex(pathway, pathwayGroupIndex);
  }
}

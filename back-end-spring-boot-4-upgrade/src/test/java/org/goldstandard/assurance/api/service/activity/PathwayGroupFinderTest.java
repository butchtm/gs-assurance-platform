package org.goldstandard.assurance.api.service.activity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.fixtures.PathwayMother.createPathway;
import static org.goldstandard.assurance.api.fixtures.PathwayMother.createPathwayGroup;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.goldstandard.assurance.api.repository.activity.PathwayGroupRepository;
import org.goldstandard.assurance.api.repository.activity.PathwayRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PathwayGroupFinderTest {

  @InjectMocks PathwayGroupFinder finder;

  @Mock PathwayGroupRepository repository;
  @Mock PathwayRepository pathwayRepository;

  @Test
  void shouldReturnFirstPathwayGroupFromPathway() {
    var pathway = createPathway(1);
    var firstGroup = createPathwayGroup(1, pathway.getIndex());
    when(pathwayRepository.findByIndex(pathway.getIndex())).thenReturn(pathway);
    when(repository.findFirstByPathwayOrderByIndexAsc(pathway)).thenReturn(Optional.of(firstGroup));

    var pathwayGroup = finder.findFirstPathwayGroup(pathway.getIndex());

    assertThat(pathwayGroup).contains(firstGroup);
  }

  @Test
  void shouldReturnPathwayGroupIfExists() {
    var pathway = createPathway(1);
    var expectedGroup = createPathwayGroup(1, pathway.getIndex());
    when(pathwayRepository.findByIndex(pathway.getIndex())).thenReturn(pathway);
    when(repository.findByPathwayAndIndex(pathway, expectedGroup.getIndex()))
        .thenReturn(Optional.of(expectedGroup));

    var pathwayGroup = finder.findByPathwayAndIndex(pathway.getIndex(), expectedGroup.getIndex());

    assertThat(pathwayGroup).contains(expectedGroup);
  }

  @Test
  void shouldReturnEmptyIfPathwayGroupDoesNotExist() {
    var pathway = createPathway(1);
    var groupIndex = 3;
    when(pathwayRepository.findByIndex(pathway.getIndex())).thenReturn(pathway);
    when(repository.findByPathwayAndIndex(pathway, groupIndex)).thenReturn(Optional.empty());

    var pathwayGroup = finder.findByPathwayAndIndex(pathway.getIndex(), groupIndex);

    assertThat(pathwayGroup).isEmpty();
  }
}

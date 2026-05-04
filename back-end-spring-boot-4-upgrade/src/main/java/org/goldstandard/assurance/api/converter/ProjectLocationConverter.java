package org.goldstandard.assurance.api.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.goldstandard.assurance.api.dto.CountryDTO;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class ProjectLocationConverter {

  public List<CountryDTO> toDtos(Collection<ProjectLocation> locations) {
    if (CollectionUtils.isEmpty(locations)) {
      return Collections.emptyList();
    }
    return locations.stream().map(this::toDto).toList();
  }

  public CountryDTO toDto(ProjectLocation location) {
    return CountryDTO.builder()
        .id(location.getId())
        .countryCode(location.getCode())
        .name(location.getDescription())
        .build();
  }
}

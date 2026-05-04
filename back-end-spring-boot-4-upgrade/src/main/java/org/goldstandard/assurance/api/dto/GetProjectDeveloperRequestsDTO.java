package org.goldstandard.assurance.api.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class GetProjectDeveloperRequestsDTO {
  private final Collection<Integer> requestStatusIds;
  private final String projectSearch;

  public Collection<Integer> getFilters() {
    return toEmptyIfNull(requestStatusIds);
  }

  public Optional<String> getProjectSearch() {
    return Optional.ofNullable(projectSearch);
  }

  private static Collection<Integer> toEmptyIfNull(Collection<Integer> values) {
    return Optional.ofNullable(values).orElse(Collections.emptySet());
  }
}

package org.goldstandard.assurance.api.fixtures;

import java.util.Collections;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.dto.GetProjectDeveloperRequestsDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetProjectDeveloperRequestsDTOMother {

  public static GetProjectDeveloperRequestsDTO empty() {
    return builder().build();
  }

  public static GetProjectDeveloperRequestsDTO.GetProjectDeveloperRequestsDTOBuilder builder() {
    return GetProjectDeveloperRequestsDTO.builder()
        .requestStatusIds(Collections.emptyList())
        .projectSearch(null);
  }
}

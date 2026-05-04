package org.goldstandard.assurance.api.service;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

@Builder(toBuilder = true)
@Getter
public class AssignmentFilters {

  private final Integer requestId;
  private final Integer assignmentStatusId;
  @With private final Integer accountTypeId;
  @With private final Integer accountId;
  private final Collection<Integer> assignmentTypeIds;
  private final Integer assignmentId;
}

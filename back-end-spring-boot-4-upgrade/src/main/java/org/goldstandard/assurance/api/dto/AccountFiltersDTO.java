package org.goldstandard.assurance.api.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AccountFiltersDTO {

  private final String email;
  private final String name;
  private final Boolean active;
  private final Integer assignmentTypeId;
  private final Collection<Integer> accountTypeIds;

  public Optional<String> getEmail() {
    return Optional.ofNullable(email);
  }

  public Optional<String> getName() {
    return Optional.ofNullable(name);
  }

  public Optional<Boolean> getActive() {
    return Optional.ofNullable(active);
  }

  public Optional<Integer> getAssignmentTypeId() {
    return Optional.ofNullable(assignmentTypeId);
  }

  public Collection<Integer> getAccountTypeIds() {
    return toEmptyIfNull(accountTypeIds);
  }

  private static Collection<Integer> toEmptyIfNull(Collection<Integer> values) {
    return Optional.ofNullable(values).orElse(Collections.emptySet());
  }
}

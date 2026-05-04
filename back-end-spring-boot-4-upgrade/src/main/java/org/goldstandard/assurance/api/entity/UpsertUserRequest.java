package org.goldstandard.assurance.api.entity;

import java.util.Collection;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpsertUserRequest {
  private final String userName;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final Collection<String> groups;
  private final Integer accountId;

  public Optional<Integer> getAccountId() {
    return Optional.ofNullable(accountId);
  }
}

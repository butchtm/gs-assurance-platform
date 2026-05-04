package org.goldstandard.assurance.api.dto;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Getter
public class UserDTO {
  private final Integer id;
  private final String userName;
  private final String emailAddress;
  private final Boolean isActive;
  private final AccountDTO account;
  private final String givenName;
  private final String familyName;
  private final Collection<String> groups;
}

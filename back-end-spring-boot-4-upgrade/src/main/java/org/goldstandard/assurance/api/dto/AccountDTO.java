package org.goldstandard.assurance.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.util.Collection;
import lombok.*;
import org.goldstandard.assurance.api.dto.lookup.AccountTypeDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
@RequiredArgsConstructor
public class AccountDTO {

  private final Integer id;
  private final String name;
  private final String emailAddress;

  @JsonProperty(value = "active")
  private final Boolean isActive;

  private final String address;
  private final AccountTypeDTO accountType;
  private final ZonedDateTime createdTime;
  @With private final Collection<UserDTO> users;
}

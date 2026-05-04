package org.goldstandard.assurance.api.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Data
@JsonPropertyOrder({"userName", "firstName", "lastName", "email", "groups", "accountId"})
public class CsvUserDTO {
  private final String userName;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final String groups;
  private final Integer accountId;
}

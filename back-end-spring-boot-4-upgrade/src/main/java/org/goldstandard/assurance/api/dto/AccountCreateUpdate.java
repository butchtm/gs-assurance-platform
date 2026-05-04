package org.goldstandard.assurance.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class AccountCreateUpdate {

  @NotNull
  @Length(min = 1)
  private String name;

  @NotNull
  @Length(min = 1)
  private String emailAddress;

  @JsonProperty(value = "active")
  private Boolean isActive;

  @NotNull
  @Length(min = 1)
  private String address;

  @NotNull private Integer accountTypeId;
  @NotNull private Set<Integer> userIds;
}

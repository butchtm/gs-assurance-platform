package org.goldstandard.assurance.api.converter;

import org.goldstandard.assurance.api.dto.lookup.AccountTypeDTO;
import org.goldstandard.assurance.api.entity.lookup.AccountType;
import org.springframework.stereotype.Component;

@Component
public class AccountTypeConverter {

  public AccountTypeDTO toDto(AccountType accountType) {
    return AccountTypeDTO.builder()
        .id(accountType.getId())
        .code(accountType.getCode())
        .description(accountType.getDescription())
        .isActive(accountType.getActive())
        .build();
  }
}

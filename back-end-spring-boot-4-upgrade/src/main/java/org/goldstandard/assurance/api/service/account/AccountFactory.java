package org.goldstandard.assurance.api.service.account;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.AccountCreateUpdate;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.lookup.AccountType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountFactory {

  private final Clock clock;

  public Account create(AccountCreateUpdate dto) {
    var account = new Account();
    account.setCreatedTime(ZonedDateTime.now(clock));
    populateUpdates(account, dto);
    return account;
  }

  public void populateUpdates(Account account, AccountCreateUpdate dto) {
    Optional.ofNullable(dto.getIsActive()).ifPresent(account::setIsActive);
    Optional.ofNullable(dto.getAccountTypeId())
        .map(AccountType::new)
        .ifPresent(account::setAccountType);
    Optional.ofNullable(dto.getAddress()).ifPresent(account::setAddress);
    Optional.ofNullable(dto.getEmailAddress()).ifPresent(account::setEmailAddress);
    Optional.ofNullable(dto.getName()).ifPresent(account::setName);
  }
}

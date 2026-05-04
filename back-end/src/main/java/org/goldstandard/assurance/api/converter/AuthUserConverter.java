package org.goldstandard.assurance.api.converter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.AccountDTO;
import org.goldstandard.assurance.api.dto.UserDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.User;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthUserConverter {

  private final AccountTypeConverter typeMapper;

  public UserDTO toUserDto(User user) {
    var builder = toUserDtoBuilder(user);
    return Optional.ofNullable(user.getAccount())
        .map(this::toAccountDto)
        .map(accountDto -> builder.account(accountDto).build())
        .orElse(builder.build());
  }

  private UserDTO.UserDTOBuilder toUserDtoBuilder(User user) {
    return UserDTO.builder()
        .id(user.getId())
        .userName(user.getUserName())
        .isActive(user.getIsActive())
        .groups(user.getGroups())
        .emailAddress(user.getEmailAddress())
        .givenName(user.getGivenName())
        .familyName(user.getFamilyName());
  }

  private AccountDTO toAccountDto(Account account) {
    return AccountDTO.builder()
        .id(account.getId())
        .name(account.getName())
        .isActive(account.getIsActive())
        .accountType(typeMapper.toDto(account.getAccountType()))
        .createdTime(account.getCreatedTime())
        .emailAddress(account.getEmailAddress())
        .address(account.getAddress())
        .build();
  }
}

package org.goldstandard.assurance.api.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.goldstandard.assurance.api.dto.AccountDTO;
import org.goldstandard.assurance.api.dto.UserDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.goldstandard.assurance.api.service.user.UserFinder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccountConverter {

  private final PermissionsService permissionsService;
  private final AccountFieldAccessPolicy accessPolicy;
  private final AccountTypeConverter typeMapper;
  private final UserFinder userService;

  public PagedResource<AccountDTO> toPagedResource(Page<Account> page) {
    var usersByAccount = toUsersByAccountIds(page);
    return new PagedResource<>(
        page.map(a -> toDto(a, usersByAccount.getOrDefault(a.getId(), Collections.emptyList()))));
  }

  public Collection<AccountDTO> toDtosWithoutUsers(Collection<Account> accounts) {
    return accounts.stream().map(this::toDtoWithoutUsers).toList();
  }

  public AccountDTO toDto(Account account) {
    return toDto(account, userService.getUsersByAccountId(account.getId()));
  }

  public UserDTO toUserDto(User user) {
    return Optional.ofNullable(user.getAccount())
        .map(this::toDtoWithoutUsers)
        .map(accountDto -> toUserDto(user, accountDto))
        .orElse(toUserDto(user, null));
  }

  public AccountDTO toDtoWithoutUsers(Account account) {
    boolean hideSensitiveData = accessPolicy.shouldHideSensitiveData(account);
    var builder =
        AccountDTO.builder()
            .id(account.getId())
            .name(account.getName())
            .isActive(account.getIsActive())
            .accountType(typeMapper.toDto(account.getAccountType()))
            .createdTime(account.getCreatedTime());
    if (!hideSensitiveData) {
      builder.emailAddress(account.getEmailAddress()).address(account.getAddress());
    }
    return builder.build();
  }

  private AccountDTO toDto(Account account, Collection<User> users) {
    var dto = toDtoWithoutUsers(account);
    return dto.withUsers(toUserDtos(dto, users));
  }

  private Map<Integer, List<User>> toUsersByAccountIds(Page<Account> page) {
    if (permissionsService.isAdminOrArm()) {
      var accountIds = page.map(Account::getId).stream().distinct().toList();
      return userService.getUsersByAccountIds(accountIds);
    }
    return Collections.emptyMap();
  }

  private Collection<UserDTO> toUserDtos(AccountDTO account, Collection<User> users) {
    if (CollectionUtils.isEmpty(users)) {
      return Collections.emptyList();
    }
    return users.stream().map(user -> toUserDto(user, account)).toList();
  }

  private UserDTO toUserDto(User user, AccountDTO accountDto) {
    var builder =
        UserDTO.builder()
            .id(user.getId())
            .userName(user.getUserName())
            .isActive(user.getIsActive())
            .groups(user.getGroups())
            .account(accountDto);
    if (permissionsService.canAccessUser(user.getIdpUserId())) {
      return builder
          .emailAddress(user.getEmailAddress())
          .givenName(user.getGivenName())
          .familyName(user.getFamilyName())
          .build();
    }
    return builder.build();
  }
}

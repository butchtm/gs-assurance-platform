package org.goldstandard.assurance.api.service.account;

import static org.springframework.data.jpa.domain.Specification.unrestricted;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.goldstandard.assurance.api.dto.AccountFiltersDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountSpecFactory {

  private final AccountTypeIdValidator accountTypeIdValidator;

  public Specification<Account> toIdInSpec(Collection<Integer> ids) {
    return (account, cq, cb) -> account.get("id").in(ids);
  }

  public Specification<Account> toSpec(AccountFiltersDTO filters) {
    Specification<Account> spec = unrestricted();
    var name = filters.getName();
    if (name.isPresent()) {
      spec = spec.and(nameIsLike(name.get()));
    }

    var email = filters.getEmail();
    if (email.isPresent()) {
      spec = spec.and(emailIsLike(email.get()));
    }

    var active = filters.getActive();
    if (active.isPresent()) {
      spec = spec.and(activeIs(active.get()));
    }

    var validatedAccountTypeIds = accountTypeIdValidator.validate(filters.getAccountTypeIds());
    if (CollectionUtils.isNotEmpty(validatedAccountTypeIds)) {
      spec = spec.and(accountTypeIdIn(validatedAccountTypeIds));
    }

    var assignmentTypeId = filters.getAssignmentTypeId();
    if (assignmentTypeId.isPresent()) {
      spec = spec.and(isAssignmentTypeId(assignmentTypeId.get()));
    }
    return spec;
  }

  private static Specification<Account> nameIsLike(String name) {
    return isLike("name", name);
  }

  private static Specification<Account> emailIsLike(String email) {
    return isLike("emailAddress", email);
  }

  private static Specification<Account> isLike(String fieldName, String value) {
    return (account, cq, cb) -> cb.like(cb.lower(account.get(fieldName)), toLikeValue(value));
  }

  private static Specification<Account> activeIs(boolean active) {
    return (account, cq, cb) -> cb.equal(account.get("isActive"), active);
  }

  private static String toLikeValue(String s) {
    return String.format("%%%s%%", s.toLowerCase());
  }

  private static Specification<Account> isAssignmentTypeId(Integer assignmentTypeId) {
    return (account, cq, cb) -> {
      var path = account.join("accountType").join("assignmentTypes").get("id");
      return cb.equal(path, assignmentTypeId);
    };
  }

  private static Specification<Account> accountTypeIdIn(Collection<Integer> accountTypeIds) {
    if (accountTypeIds.isEmpty()) {
      return unrestricted();
    }
    return (account, cq, cb) -> account.join("accountType").get("id").in(accountTypeIds);
  }
}

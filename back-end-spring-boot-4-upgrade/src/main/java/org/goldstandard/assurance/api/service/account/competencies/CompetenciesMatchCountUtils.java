package org.goldstandard.assurance.api.service.account.competencies;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompetenciesMatchCountUtils {

  public static boolean isEmissionsMatch(Integer competencyValue, Integer value) {
    if (Objects.isNull(value)) {
      return false;
    }
    if (Objects.isNull(competencyValue)) {
      return true;
    }
    return competencyValue >= value;
  }

  public static <T extends ReferenceDataType> boolean isMatch(List<T> competencies, T itemToMatch) {
    if (itemToMatch == null) {
      return false;
    }
    return hasMatch(competencies, Set.of(itemToMatch));
  }

  public static <T extends ReferenceDataType> boolean hasMatch(
      Collection<T> competencies, Collection<T> values) {
    if (CollectionUtils.isEmpty(values)) {
      return false;
    }
    Stream<Integer> competencyIds = toIdStream(competencies);
    Collection<Integer> valueIds = toIds(values);
    return hasIntMatch(competencyIds, valueIds);
  }

  public static boolean hasAccountMatch(List<Account> accounts, Integer accountId) {
    return hasIntMatch(toAccountIds(accounts), Set.of(accountId));
  }

  public static boolean hasIntMatch(Stream<Integer> competencies, Collection<Integer> values) {
    return competencies.anyMatch(values::contains);
  }

  public static boolean hasStringMatch(Collection<String> competencies, String value) {
    return competencies.stream().anyMatch(c -> Objects.equals(c, value));
  }

  private static <T extends ReferenceDataType> Collection<Integer> toIds(Collection<T> items) {
    return toIdStream(items).toList();
  }

  private static <T extends ReferenceDataType> Stream<Integer> toIdStream(Collection<T> items) {
    return items.stream().map(ReferenceDataType::getId);
  }

  private static Stream<Integer> toAccountIds(Collection<Account> accounts) {
    if (CollectionUtils.isEmpty(accounts)) {
      return Stream.of();
    }
    return accounts.stream().map(Account::getId);
  }
}

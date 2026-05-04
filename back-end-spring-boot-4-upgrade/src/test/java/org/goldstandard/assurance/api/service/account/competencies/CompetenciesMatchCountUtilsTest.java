package org.goldstandard.assurance.api.service.account.competencies;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CompetenciesMatchCountUtilsTest {

  private static final List<ReferenceDataType> TEST_COMPETENCIES_LIST =
      List.of(
          ReferenceDataType.fromId(1), ReferenceDataType.fromId(2), ReferenceDataType.fromId(3));

  @Test
  void emissionsShouldAlwaysMatchIfCompetencyValueIsNull() {
    Integer competencyValue = null;
    Integer value = 10;

    var match = CompetenciesMatchCountUtils.isEmissionsMatch(competencyValue, value);

    assertThat(match).isTrue();
  }

  @ParameterizedTest
  @MethodSource("emissionsValuesAndExpectedCompetencyMatch")
  void shouldMatchEmissionsValuesToCompetencyAmount(Integer value, boolean expectedMatch) {
    Integer competencyValue = 100;

    var match = CompetenciesMatchCountUtils.isEmissionsMatch(competencyValue, value);

    assertThat(match).isEqualTo(expectedMatch);
  }

  @Test
  void isMatchReturnsTrueIfMatchPresent() {
    var itemToMatch = ReferenceDataType.fromId(1);

    var match = CompetenciesMatchCountUtils.isMatch(TEST_COMPETENCIES_LIST, itemToMatch);

    assertThat(match).isTrue();
  }

  @Test
  void isMatchReturnsFalseIfNoMatchPresent() {
    var itemToMatch = ReferenceDataType.fromId(4);

    var match = CompetenciesMatchCountUtils.isMatch(TEST_COMPETENCIES_LIST, itemToMatch);

    assertThat(match).isFalse();
  }

  @Test
  void hasMatchReturnsFalseIfValuesEmpty() {
    var values = Collections.<ReferenceDataType>emptyList();

    var match = CompetenciesMatchCountUtils.hasMatch(TEST_COMPETENCIES_LIST, values);

    assertThat(match).isFalse();
  }

  @Test
  void hasMatchReturnsTrueIfMatchPresent() {
    var value1 = ReferenceDataType.fromId(3);
    var value2 = ReferenceDataType.fromId(4);
    var values = List.of(value1, value2);

    var match = CompetenciesMatchCountUtils.hasMatch(TEST_COMPETENCIES_LIST, values);

    assertThat(match).isTrue();
  }

  @Test
  void hasMatchReturnsFalseIfNoMatchPresent() {
    var value1 = ReferenceDataType.fromId(4);
    var value2 = ReferenceDataType.fromId(5);
    var values = List.of(value1, value2);

    var match = CompetenciesMatchCountUtils.hasMatch(TEST_COMPETENCIES_LIST, values);

    assertThat(match).isFalse();
  }

  @Test
  void hasAccountMatchReturnsTrueIfMatchPresent() {
    var accounts = List.of(new Account(1), new Account(2), new Account(3));
    var accountToMatchId = 3;

    var match = CompetenciesMatchCountUtils.hasAccountMatch(accounts, accountToMatchId);

    assertThat(match).isTrue();
  }

  @Test
  void hasAccountMatchReturnsFalseIfNoMatchPresent() {
    var accounts = List.of(new Account(1), new Account(2), new Account(3));
    var accountToMatchId = 4;

    var match = CompetenciesMatchCountUtils.hasAccountMatch(accounts, accountToMatchId);

    assertThat(match).isFalse();
  }

  @Test
  void hasIntMatchReturnsTrueIfMatchPresent() {
    var competencies = Stream.of(1, 2, 3);
    var values = List.of(3, 4);

    var match = CompetenciesMatchCountUtils.hasIntMatch(competencies, values);

    assertThat(match).isTrue();
  }

  @Test
  void hasIntMatchReturnsFalseIfNoMatchPresent() {
    var competencies = Stream.of(1, 2, 3);
    var values = List.of(4, 5);

    var match = CompetenciesMatchCountUtils.hasIntMatch(competencies, values);

    assertThat(match).isFalse();
  }

  @Test
  void hasStringMatchReturnsTrueIfMatchPresent() {
    var competencies = List.of("One", "Two", "Three");
    var value = "Three";

    var match = CompetenciesMatchCountUtils.hasStringMatch(competencies, value);

    assertThat(match).isTrue();
  }

  @Test
  void hasStringMatchReturnsFalseIfNoMatchPresent() {
    var competencies = List.of("One", "Two", "Three");
    var value = "Four";

    var match = CompetenciesMatchCountUtils.hasStringMatch(competencies, value);

    assertThat(match).isFalse();
  }

  private static Stream<Arguments> emissionsValuesAndExpectedCompetencyMatch() {
    return Stream.of(
        Arguments.of(98, true),
        Arguments.of(99, true),
        Arguments.of(100, true),
        Arguments.of(101, false),
        Arguments.of(null, false));
  }
}

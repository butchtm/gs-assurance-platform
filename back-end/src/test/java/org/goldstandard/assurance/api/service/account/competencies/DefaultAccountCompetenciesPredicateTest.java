package org.goldstandard.assurance.api.service.account.competencies;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.entity.Competencies;
import org.goldstandard.assurance.api.entity.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultAccountCompetenciesPredicateTest {

  private static final long MIN_REQUIRED_MATCHES = 11;

  @Mock ToLongFunction<Request> matchCounter;

  @Test
  void shouldReturnFalseIfCompetenciesAreNull() {
    Competencies competencies = null;
    Predicate<Request> predicate = toPredicate(competencies);
    Request request = new Request();

    boolean result = predicate.test(request);

    assertThat(result).isFalse();
  }

  @ParameterizedTest
  @MethodSource("matchCountsAndExpectedResults")
  void shouldReturnExpectedResultForNumberOfMatches(long matchCount, boolean expectedResult) {
    Competencies competencies = new Competencies();
    Predicate<Request> predicate = toPredicate(competencies);
    Request request = new Request();
    when(matchCounter.applyAsLong(request)).thenReturn(matchCount);

    boolean result = predicate.test(request);

    assertThat(result).isEqualTo(expectedResult);
  }

  private static Stream<Arguments> matchCountsAndExpectedResults() {
    return Stream.of(
        Arguments.of(MIN_REQUIRED_MATCHES, true),
        Arguments.of(MIN_REQUIRED_MATCHES + 1, true),
        Arguments.of(MIN_REQUIRED_MATCHES - 1, false));
  }

  private Predicate<Request> toPredicate(Competencies competencies) {
    return new DefaultAccountCompetenciesPredicate(
        competencies, matchCounter, MIN_REQUIRED_MATCHES);
  }
}

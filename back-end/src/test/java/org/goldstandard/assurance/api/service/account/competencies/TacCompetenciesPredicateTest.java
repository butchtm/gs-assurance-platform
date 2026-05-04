package org.goldstandard.assurance.api.service.account.competencies;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import org.goldstandard.assurance.api.entity.Competencies;
import org.goldstandard.assurance.api.entity.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TacCompetenciesPredicateTest {

  @Mock ToLongFunction<Request> matchCounter;

  @Test
  void shouldReturnTrueIfCompetenciesAreNull() {
    Competencies competencies = null;
    Predicate<Request> predicate = toPredicate(competencies);
    Request request = new Request();

    boolean result = predicate.test(request);

    assertThat(result).isTrue();
  }

  @Test
  void shouldReturnTrueIfAtLeastOneCompetencyMatches() {
    Competencies competencies = new Competencies();
    Predicate<Request> predicate = toPredicate(competencies);
    Request request = new Request();
    when(matchCounter.applyAsLong(request)).thenReturn(1L);

    boolean result = predicate.test(request);

    assertThat(result).isTrue();
  }

  @Test
  void shouldReturnFalseIfNoCompetenciesMatch() {
    Competencies competencies = new Competencies();
    Predicate<Request> predicate = toPredicate(competencies);
    Request request = new Request();
    when(matchCounter.applyAsLong(request)).thenReturn(0L);

    boolean result = predicate.test(request);

    assertThat(result).isFalse();
  }

  private Predicate<Request> toPredicate(Competencies competencies) {
    return new TacCompetenciesPredicate(competencies, matchCounter);
  }
}

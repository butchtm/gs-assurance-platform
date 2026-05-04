package org.goldstandard.assurance.api.service.account.competencies;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Competencies;
import org.goldstandard.assurance.api.entity.Request;

@RequiredArgsConstructor
public class TacCompetenciesPredicate implements Predicate<Request> {

  private final Competencies competencies;
  private final ToLongFunction<Request> matchCounter;

  public TacCompetenciesPredicate(Competencies competencies) {
    this(competencies, new CompetenciesMatchCounter(competencies));
  }

  @Override
  public boolean test(Request request) {
    if (Objects.isNull(competencies)) {
      return true;
    }
    return countMatches(request) > 0;
  }

  private long countMatches(Request request) {
    return matchCounter.applyAsLong(request);
  }
}

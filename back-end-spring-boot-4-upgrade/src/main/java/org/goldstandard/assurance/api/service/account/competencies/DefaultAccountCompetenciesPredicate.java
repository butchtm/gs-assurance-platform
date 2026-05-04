package org.goldstandard.assurance.api.service.account.competencies;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Competencies;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.service.AssignmentFinder;

@RequiredArgsConstructor
public class DefaultAccountCompetenciesPredicate implements Predicate<Request> {

  private static final long DEFAULT_MIN_REQUIRED_MATCHES = 11;

  private final Competencies competencies;
  private final ToLongFunction<Request> matchCounter;
  private final long minRequiredMatches;

  public DefaultAccountCompetenciesPredicate(
      Competencies competencies, AssignmentFinder assignmentFinder) {
    this(competencies, new DefaultCompetenciesMatchCounter(competencies, assignmentFinder));
  }

  public DefaultAccountCompetenciesPredicate(
      Competencies competencies, ToLongFunction<Request> matchCounter) {
    this(competencies, matchCounter, DEFAULT_MIN_REQUIRED_MATCHES);
  }

  @Override
  public boolean test(Request request) {
    if (Objects.isNull(competencies)) {
      return false;
    }
    return matchCounter.applyAsLong(request) >= minRequiredMatches;
  }
}

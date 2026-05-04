package org.goldstandard.assurance.api.service.account.competencies;

import static org.goldstandard.assurance.api.service.account.competencies.CompetenciesMatchCountUtils.hasAccountMatch;
import static org.goldstandard.assurance.api.service.account.competencies.CompetenciesMatchCountUtils.hasStringMatch;

import java.util.Optional;
import java.util.function.ToLongFunction;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Competencies;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.service.AssignmentFinder;

@RequiredArgsConstructor
public class DefaultCompetenciesMatchCounter implements ToLongFunction<Request> {

  private final ToLongFunction<Request> defaultMatchCounter;
  private final Competencies competencies;
  private final AssignmentFinder assignmentFinder;

  public DefaultCompetenciesMatchCounter(
      Competencies competencies, AssignmentFinder assignmentFinder) {
    this(new CompetenciesMatchCounter(competencies), competencies, assignmentFinder);
  }

  @Override
  public long applyAsLong(Request request) {
    long count = defaultMatchCounter.applyAsLong(request);
    count += countAdditionalMatches(request);
    return count;
  }

  private long countAdditionalMatches(Request request) {
    long count = 0;
    if (hasStringMatch(competencies.getProjectGsids(), request.getProject().getGsid())) {
      count += 1;
    }
    Optional<Integer> vvbId = getAssignedVvbId(request);
    if (vvbId.isPresent() && hasAccountMatch(competencies.getVvbsAssigned(), vvbId.get())) {
      count += 1;
    }
    return count;
  }

  private Optional<Integer> getAssignedVvbId(Request request) {
    return assignmentFinder.findAssignedAuditAccountsByRequestId(request.getId()).stream()
        .map(Account::getId)
        .findFirst();
  }
}

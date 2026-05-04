package org.goldstandard.assurance.api.entity;

import java.util.function.Predicate;
import org.goldstandard.assurance.api.entity.lookup.BatchType;
import org.springframework.stereotype.Component;

@Component
public class IsDirectChildProjectPredicateFactory {

  public Predicate<Project> build(Project project, BatchType batchType) {
    if (batchType.isTwoOrMoreVpaCpa()) {
      return new VpaCpaIsDirectChildProjectPredicate(project);
    }
    return new DefaultIsDirectChildProjectPredicate(project);
  }
}

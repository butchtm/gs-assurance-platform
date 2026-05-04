package org.goldstandard.assurance.api.entity;

import static org.goldstandard.assurance.api.entity.Project.hasSameParentAndNotSameId;

import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultIsDirectChildProjectPredicate implements Predicate<Project> {

  private final Project parent;

  @Override
  public boolean test(Project child) {
    if (parent.isPoa()) {
      // we cant check hasParent for real case VPAs currently as this is not set correctly in the UI
      return child.isRealCaseVpa() && child.hasMaster(parent);
    }
    if (parent.isRealCaseVpa()) {
      return child.isRegularCaseVpa() && child.hasParent(parent);
    }
    if (parent.isRegularCaseVpa()) {
      return child.isRegularCaseVpa() && hasSameParentAndNotSameId(parent, child);
    }
    return false;
  }
}

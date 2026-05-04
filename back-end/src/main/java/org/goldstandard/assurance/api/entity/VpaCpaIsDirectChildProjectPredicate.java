package org.goldstandard.assurance.api.entity;

import static org.goldstandard.assurance.api.entity.Project.hasSameParentAndNotSameId;

import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VpaCpaIsDirectChildProjectPredicate implements Predicate<Project> {

  private final Project parent;

  @Override
  public boolean test(Project child) {
    return parent.isLegacyVpaCpa() && isLegacyVpaCpaChild(child);
  }

  private boolean isLegacyVpaCpaChild(Project child) {
    return child.isLegacyVpaCpa() && hasSameParentAndNotSameId(parent, child);
  }
}

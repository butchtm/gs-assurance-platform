package org.goldstandard.assurance.api.service;

import static java.util.Objects.nonNull;

import org.goldstandard.assurance.api.entity.creditingperiod.ProjectMethodologyUpdate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProjectMethodologyUpdateSpecFactory {

  public Specification<ProjectMethodologyUpdate> build(Integer projectId, Integer periodId) {
    Specification<ProjectMethodologyUpdate> spec = hasProjectId(projectId);
    if (nonNull(periodId)) {
      spec = spec.and(hasPeriodId(periodId));
    }
    return spec;
  }

  private static Specification<ProjectMethodologyUpdate> hasPeriodId(Integer periodId) {
    return isEqualTo("periodId", periodId);
  }

  private static Specification<ProjectMethodologyUpdate> hasProjectId(Integer projectId) {
    return isEqualTo("projectId", projectId);
  }

  private static Specification<ProjectMethodologyUpdate> isEqualTo(String column, Integer value) {
    return (root, query, cb) -> cb.equal(root.get(column), value);
  }
}

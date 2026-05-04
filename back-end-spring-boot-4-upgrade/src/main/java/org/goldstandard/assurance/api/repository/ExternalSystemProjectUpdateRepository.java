package org.goldstandard.assurance.api.repository;

import java.time.ZonedDateTime;
import org.goldstandard.assurance.api.entity.ExternalSystemProjectUpdate;
import org.goldstandard.assurance.api.entity.ExternalSystemProjectUpdateId;
import org.goldstandard.assurance.api.service.external.ExternalSystem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ExternalSystemProjectUpdateRepository
    extends PagingAndSortingRepository<ExternalSystemProjectUpdate, ExternalSystemProjectUpdateId>,
        CrudRepository<ExternalSystemProjectUpdate, ExternalSystemProjectUpdateId>,
        JpaSpecificationExecutor<ExternalSystemProjectUpdate> {

  default Page<ExternalSystemProjectUpdate> getPageOfUpdates(
      int pageSize, ZonedDateTime cutoff, ExternalSystem system) {
    var request = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.ASC, "createdTimestamp"));
    var spec = lastAttemptNullOrOlderThan(cutoff).and(destinationSystemEquals(system));
    return findAll(spec, request);
  }

  private static Specification<ExternalSystemProjectUpdate> lastAttemptNullOrOlderThan(
      ZonedDateTime cutoff) {
    return lastAttemptIsNull().or(lastAttemptOlderThan(cutoff));
  }

  private static Specification<ExternalSystemProjectUpdate> lastAttemptIsNull() {
    return (update, cq, cb) -> cb.isNull(update.get("lastAttemptAt"));
  }

  private static Specification<ExternalSystemProjectUpdate> lastAttemptOlderThan(
      ZonedDateTime cutoff) {
    return (update, cq, cb) -> cb.lessThan(update.get("lastAttemptAt"), cutoff);
  }

  private static Specification<ExternalSystemProjectUpdate> destinationSystemEquals(
      ExternalSystem system) {
    return (root, query, cb) -> cb.equal(root.get("destinationSystem"), system.name());
  }
}

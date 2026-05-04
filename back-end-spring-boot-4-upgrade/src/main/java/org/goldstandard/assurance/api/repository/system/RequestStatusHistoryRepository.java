package org.goldstandard.assurance.api.repository.system;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.RequestStatusHistory;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.springframework.data.repository.CrudRepository;

public interface RequestStatusHistoryRepository
    extends CrudRepository<RequestStatusHistory, Integer> {
  List<RequestStatusHistory> findByRequestIdOrderByChangedAtDesc(Integer requestId);

  default Optional<ZonedDateTime> getFirstInProgressDate(Integer requestId) {
    return findFirstByRequestIdAndNextStatusOrderByChangedAtAsc(
            requestId, RequestStatus.inProgress())
        .map(RequestStatusHistory::getChangedAt);
  }

  Optional<RequestStatusHistory> findFirstByRequestIdAndNextStatusOrderByChangedAtAsc(
      Integer requestId, RequestStatus nextStatus);
}

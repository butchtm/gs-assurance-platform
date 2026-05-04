package org.goldstandard.assurance.api.repository;

import java.util.List;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.RequestRound;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRoundRepository extends CrudRepository<RequestRound, Integer> {
  List<RequestRound> findByRequestId(Integer requestId);

  Optional<RequestRound> findByRequestIdAndRoundIndex(Integer requestId, Integer roundIndex);
}

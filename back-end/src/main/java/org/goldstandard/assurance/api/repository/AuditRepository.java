package org.goldstandard.assurance.api.repository;

import java.util.List;
import org.goldstandard.assurance.api.entity.AuditEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends CrudRepository<AuditEvent, Integer> {

  List<AuditEvent> findAllByEventSubject(String eventSubject);
}

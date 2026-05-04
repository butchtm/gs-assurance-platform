package org.goldstandard.assurance.api.repository;

import jakarta.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StakeholderConsultationRepository
    extends JpaRepository<StakeholderConsultation, Integer>,
        JpaSpecificationExecutor<StakeholderConsultation> {

  default List<StakeholderConsultation> getAllInConsultationByRequestId(
      Integer requestId, ZonedDateTime at) {
    return findByRequestIdAndStartDateBeforeAndEndDateAfter(requestId, at, at);
  }

  default List<StakeholderConsultation> getAllUnnotifiedInConsultation(ZonedDateTime at) {
    return findByStartDateBeforeAndEndDateAfterAndNotificationSentFalse(at, at);
  }

  List<StakeholderConsultation> getAllByRequestId(Integer requestId);

  List<StakeholderConsultation> findByRequestIdAndStartDateBeforeAndEndDateAfter(
      Integer requestId, ZonedDateTime startDateTime, ZonedDateTime endDateTime);

  List<StakeholderConsultation> findByStartDateBeforeAndEndDateAfterAndNotificationSentFalse(
      ZonedDateTime startDateTime, ZonedDateTime endDateTime);

  @Modifying
  @Transactional
  @Query("UPDATE StakeholderConsultation sc SET sc.notificationSent = true WHERE sc.id IN :ids")
  void markAsNotified(@Param("ids") List<Integer> ids);
}

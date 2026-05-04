package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.creditingperiod.RequestCreditingPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestCreditingPeriodRepository
    extends JpaRepository<RequestCreditingPeriod, Integer> {}

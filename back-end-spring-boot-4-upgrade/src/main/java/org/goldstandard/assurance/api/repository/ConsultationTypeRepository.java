package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.ConsultationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultationTypeRepository extends JpaRepository<ConsultationType, Integer> {}

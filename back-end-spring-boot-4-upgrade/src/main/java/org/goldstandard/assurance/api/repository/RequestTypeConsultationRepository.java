package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.RequestTypeConsultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestTypeConsultationRepository
    extends JpaRepository<RequestTypeConsultation, Integer> {}

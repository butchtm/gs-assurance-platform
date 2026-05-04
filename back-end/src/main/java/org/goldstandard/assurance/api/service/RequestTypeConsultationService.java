package org.goldstandard.assurance.api.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.RequestTypeConsultation;
import org.goldstandard.assurance.api.repository.RequestTypeConsultationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestTypeConsultationService {

  private final RequestTypeConsultationRepository requestTypeConsultationRepository;

  public List<RequestTypeConsultation> getListOfRequestTypesAndConsultationConfiguration() {
    return requestTypeConsultationRepository.findAll();
  }
}

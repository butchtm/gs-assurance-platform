package org.goldstandard.assurance.api.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.RequestTypeAssignmentTypes;
import org.goldstandard.assurance.api.entity.RequestTypeConsultation;
import org.goldstandard.assurance.api.service.RequestTypeAssignmentTypeService;
import org.goldstandard.assurance.api.service.RequestTypeConsultationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RequestTypeController {

  private final RequestTypeAssignmentTypeService requestTypeAssignmentTypeService;
  private final RequestTypeConsultationService requestTypeConsultationService;

  @GetMapping("/request-type-assignment-types/{requestTypeId}")
  public RequestTypeAssignmentTypes getRequestTypeAssignmentTypes(
      @PathVariable(value = "requestTypeId") Integer requestTypeId) {
    return requestTypeAssignmentTypeService.getAssignmentTypesForRequestType(requestTypeId);
  }

  @GetMapping("/request-type-consultations")
  public List<RequestTypeConsultation> getAllRequestTypesAndConsultationRelationship() {
    return requestTypeConsultationService.getListOfRequestTypesAndConsultationConfiguration();
  }
}

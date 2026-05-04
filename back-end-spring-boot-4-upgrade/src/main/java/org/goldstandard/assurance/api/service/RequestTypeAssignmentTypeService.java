package org.goldstandard.assurance.api.service;

import org.goldstandard.assurance.api.dto.RequestTypeAssignmentTypes;
import org.goldstandard.assurance.api.repository.RequestTypeAssignmentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestTypeAssignmentTypeService {

  @Autowired private RequestTypeAssignmentTypeRepository requestTypeAssignmentTypeRepository;

  public RequestTypeAssignmentTypes getAssignmentTypesForRequestType(Integer requestTypeId) {
    return RequestTypeAssignmentTypes.builder()
        .requestTypeId(requestTypeId)
        .assignmentTypes(
            requestTypeAssignmentTypeRepository.findAllByRequestTypeId(requestTypeId).stream()
                .map(
                    requestTypeAssignmentType ->
                        RequestTypeAssignmentTypes.Rule.builder()
                            .assignmentTypeId(requestTypeAssignmentType.getAssignmentTypeId())
                            .mandatory(requestTypeAssignmentType.getMandatory())
                            .build())
                .toList())
        .build();
  }
}

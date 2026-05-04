package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.DigitalFormType;

@Getter
@Builder
@RequiredArgsConstructor
public class RequestTypeDocumentTypeDTO {

  private final Integer requestTypeId;

  private final Integer documentTypeId;
  private final String documentTypeDescription;

  private final Integer documentDisplayCategoryId;
  private final String documentDisplayCategoryCode;

  private final DigitalFormType formType;
}

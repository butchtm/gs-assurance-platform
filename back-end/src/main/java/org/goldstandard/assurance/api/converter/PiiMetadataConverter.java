package org.goldstandard.assurance.api.converter;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.PiiMetadataDTO;
import org.goldstandard.assurance.api.entity.pii.PiiMetadata;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PiiMetadataConverter {

  public PiiMetadataDTO toDto(PiiMetadata piiMetadata) {
    return PiiMetadataDTO.builder().piiPresent(true).action(piiMetadata.getAction()).build();
  }
}

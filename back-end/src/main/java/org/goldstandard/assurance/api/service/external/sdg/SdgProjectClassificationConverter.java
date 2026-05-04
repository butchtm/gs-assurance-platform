package org.goldstandard.assurance.api.service.external.sdg;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.service.external.ExternalProjectClassificationConverter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SdgProjectClassificationConverter {

  private final ExternalProjectClassificationConverter externalConverter;

  public SdgProjectClassificationConverter() {
    this(new ExternalProjectClassificationConverter("NONE"));
  }

  public String toSdgClassification(String code) {
    return externalConverter.toExternalClassification(code);
  }
}

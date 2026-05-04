package org.goldstandard.assurance.api.service.external.registry;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.service.external.ExternalProjectClassificationConverter;

@RequiredArgsConstructor
public class RegistryProjectClassificationConverter {

  private final ExternalProjectClassificationConverter externalConverter;

  public RegistryProjectClassificationConverter() {
    this(new ExternalProjectClassificationConverter("STANDALONE"));
  }

  public String toRegistryClassification(String code) {
    return externalConverter.toExternalClassification(code);
  }
}

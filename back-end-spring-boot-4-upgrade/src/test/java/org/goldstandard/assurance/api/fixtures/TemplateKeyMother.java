package org.goldstandard.assurance.api.fixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.service.notification.TemplateKey;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TemplateKeyMother {

  private static final String VERSION = "v0.0.1";

  public static TemplateKey build(String name) {
    return new TemplateKey(name, VERSION);
  }
}

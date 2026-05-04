package org.goldstandard.assurance.api.service.notification;

public record TemplateKey(String name, String version) {
  public String fullName() {
    return String.format("%s-%s", name(), version());
  }
}

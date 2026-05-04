package org.goldstandard.assurance.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.Bundle;

@NoArgsConstructor(force = true)
@Getter
public class BundleDTO {

  private final Integer id;
  private final String name;

  public BundleDTO(Bundle bundle) {
    this.id = bundle.getId();
    this.name = bundle.getName();
  }
}

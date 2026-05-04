package org.goldstandard.assurance.api.service.external.registry;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Data
public class RegistryProjectResponse {

  private final String id;
}

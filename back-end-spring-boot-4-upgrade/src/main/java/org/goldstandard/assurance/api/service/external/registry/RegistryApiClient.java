package org.goldstandard.assurance.api.service.external.registry;

import java.util.Optional;

public interface RegistryApiClient {

  Optional<Integer> updateProject(RegistryProject project);
}

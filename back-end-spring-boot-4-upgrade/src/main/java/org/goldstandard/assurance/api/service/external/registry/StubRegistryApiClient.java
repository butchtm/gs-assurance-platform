package org.goldstandard.assurance.api.service.external.registry;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.service.external.ExternalStubNameToStatusCodeConverter;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Slf4j
public class StubRegistryApiClient implements RegistryApiClient {

  private final ExternalStubNameToStatusCodeConverter statusCodeConverter;
  private final ObjectMapper mapper;

  public StubRegistryApiClient(ObjectMapper mapper) {
    this(new ExternalStubNameToStatusCodeConverter(), mapper);
  }

  @Override
  public Optional<Integer> updateProject(RegistryProject project) {
    try {
      throwErrorIfRequired(project.getName());
      var json = mapper.writeValueAsString(project);
      log.info("would send registry project json {}", json);
      return Optional.ofNullable(project.getId()).map(id -> id + 999);
    } catch (JacksonException e) {
      throw new RegistryApiClientException(e);
    }
  }

  private void throwErrorIfRequired(String name) {
    var errorStatusCode = statusCodeConverter.toErrorStatusCodeIfRequired(name);
    if (errorStatusCode.isEmpty()) {
      return;
    }
    throw new RegistryApiClientException(name, errorStatusCode.get());
  }
}

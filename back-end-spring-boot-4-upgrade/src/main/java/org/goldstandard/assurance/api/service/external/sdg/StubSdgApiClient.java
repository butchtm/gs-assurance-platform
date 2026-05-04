package org.goldstandard.assurance.api.service.external.sdg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.service.external.ExternalStubNameToStatusCodeConverter;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Slf4j
public class StubSdgApiClient implements SdgApiClient {

  private final ExternalStubNameToStatusCodeConverter statusCodeConverter;
  private final ObjectMapper mapper;

  public StubSdgApiClient(ObjectMapper mapper) {
    this(new ExternalStubNameToStatusCodeConverter(), mapper);
  }

  @Override
  public void updateProject(SdgProject project) {
    try {
      throwErrorIfRequired(project.getName());
      var json = mapper.writeValueAsString(project);
      log.info("would send sdg tool project json {}", json);
    } catch (JacksonException e) {
      throw new SdgApiClientException(e);
    }
  }

  private void throwErrorIfRequired(String name) {
    var errorStatusCode = statusCodeConverter.toErrorStatusCodeIfRequired(name);
    if (errorStatusCode.isEmpty()) {
      return;
    }
    throw new SdgApiClientException(name, errorStatusCode.get());
  }
}

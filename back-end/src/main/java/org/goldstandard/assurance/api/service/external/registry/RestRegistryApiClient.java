package org.goldstandard.assurance.api.service.external.registry;

import java.util.Optional;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Builder
@Slf4j
public class RestRegistryApiClient implements RegistryApiClient {

  private final String host;
  private final String token;
  private final RestTemplate restTemplate;

  @Override
  public Optional<Integer> updateProject(RegistryProject project) {
    try {
      var url = buildUrl();
      log.info("updating registry with project {}", project);
      var entity = toRequestEntity(project);
      var response = restTemplate.postForEntity(url, entity, RegistryProjectResponse.class);
      log.info(
          "registry api response code {} body {}", response.getStatusCode(), response.getBody());
      return Optional.ofNullable(response.getBody())
          .map(RegistryProjectResponse::getId)
          .map(Integer::parseInt);
    } catch (RestClientException e) {
      throw new RegistryApiClientException(e);
    }
  }

  private String buildUrl() {
    return String.format("%s/sustaincert/projects", host);
  }

  private HttpEntity<RegistryProject> toRequestEntity(RegistryProject registryProject) {
    return new HttpEntity<>(registryProject, buildHeaders());
  }

  private HttpHeaders buildHeaders() {
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("X-SustainCERT-Auth-Token", token);
    return headers;
  }
}

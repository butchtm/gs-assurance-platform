package org.goldstandard.assurance.api.service.external.sdg;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Builder
@Slf4j
public class RestSdgApiClient implements SdgApiClient {

  private final String host;
  private final String token;
  private final RestTemplate restTemplate;

  @Override
  public void updateProject(SdgProject payload) {
    try {
      var url = buildUrl(payload);
      log.info("updating sdg tool with project {}", payload);
      var entity = toRequestEntity(payload);
      var response = restTemplate.postForEntity(url, entity, String.class);
      log.info(
          "sdg tool api response code {} body {}", response.getStatusCode(), response.getBody());
    } catch (RestClientException e) {
      throw new SdgApiClientException(e);
    }
  }

  private String buildUrl(SdgProject payload) {
    return String.format("%s/api/projects/%s/", host, payload.getGsid());
  }

  private HttpEntity<SdgProject> toRequestEntity(SdgProject payload) {
    return new HttpEntity<>(payload, buildHeaders());
  }

  private HttpHeaders buildHeaders() {
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", String.format("Api-Key %s", token));
    return headers;
  }
}

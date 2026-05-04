package org.goldstandard.assurance.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.UUID;
import org.goldstandard.assurance.api.database.h2.Compatibility;
import org.goldstandard.assurance.api.populator.AccountPopulator;
import org.goldstandard.assurance.api.populator.AssignmentPopulator;
import org.goldstandard.assurance.api.populator.BundlePopulator;
import org.goldstandard.assurance.api.populator.CommentPopulator;
import org.goldstandard.assurance.api.populator.DocumentPopulator;
import org.goldstandard.assurance.api.populator.ProjectPopulator;
import org.goldstandard.assurance.api.populator.ReferenceDataPopulator;
import org.goldstandard.assurance.api.populator.RequestActivityPopulator;
import org.goldstandard.assurance.api.populator.RequestPopulator;
import org.goldstandard.assurance.api.populator.StakeholderConsultationPopulator;
import org.goldstandard.assurance.api.populator.UserBatchPopulator;
import org.goldstandard.assurance.api.populator.UserPopulator;
import org.goldstandard.assurance.api.service.document.FileStore;
import org.goldstandard.assurance.api.service.document.WritableFileStore;
import org.goldstandard.assurance.api.service.notification.EmailSender;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.MultiValueMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConfiguration("org.goldstandard.assurance.api.populator")
abstract class AbstractApiTest {
  @Value(value = "http://localhost:${local.server.port}")
  protected String baseUrl;

  @Autowired protected TestRestTemplate restTemplate;

  @Autowired protected ObjectMapper objectMapper;

  @MockitoBean(name = "documentFileStore")
  protected FileStore documentFileStore;

  @MockitoBean(name = "downloadFileStore")
  protected FileStore downloadFileStore;

  @MockitoBean(name = "salesOrderFileStore")
  protected WritableFileStore salesOrderFileStore;

  @MockitoBean(name = "projectContractFileStore")
  protected WritableFileStore projectContractFileStore;

  @MockitoBean protected EmailSender emailSender;

  protected HttpEntity<?> unauthenticated() {
    return new HttpEntity<>(new HttpHeaders());
  }

  protected HttpEntity<?> getHttpEntityWithTokenForRole(String role) {
    return new HttpEntity<>(getHttpHeadersWithToken(role, null));
  }

  protected HttpEntity<?> getHttpEntityWithTokenForRoleWithId(String role, UUID id) {
    return new HttpEntity<>(getHttpHeadersWithToken(role, id));
  }

  protected HttpEntity<?> getHttpEntityWithToken() {
    return new HttpEntity<>(getHttpHeadersWithToken(null, null));
  }

  protected <T> HttpEntity<String> getHttpEntityWithTokenAndBodyForRole(T body, String role) {
    return getHttpEntityWithTokenAndBodyForRoleWithId(body, role, null);
  }

  protected <T> HttpEntity<String> getHttpEntityWithTokenAndBody(T body) {
    var headers = getHttpHeadersWithToken(null, null);
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(toJson(body), headers);
  }

  protected <T> HttpEntity<String> getHttpEntityWithTokenAndBodyForRoleWithId(
      T body, String role, UUID id) {
    var headers = getHttpHeadersWithToken(role, id);
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(toJson(body), headers);
  }

  protected HttpEntity<MultiValueMap<String, Object>>
      getHttpEntityWithTokenAndFormBodyForRoleWithId(
          MultiValueMap<String, Object> body, String role, UUID id) {
    var headers = getHttpHeadersWithToken(role, id);
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    return new HttpEntity<>(body, headers);
  }

  private String toJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  @BeforeAll
  public static void setup(
      @Autowired ReferenceDataPopulator referenceDataPopulator,
      @Autowired UserBatchPopulator userBatchPopulator,
      @Autowired UserPopulator userPopulator,
      @Autowired AccountPopulator accountPopulator,
      @Autowired BundlePopulator bundlePopulator,
      @Autowired ProjectPopulator projectPopulator,
      @Autowired RequestActivityPopulator requestActivityPopulator,
      @Autowired RequestPopulator requestPopulator,
      @Autowired DocumentPopulator documentPopulator,
      @Autowired AssignmentPopulator assignmentPopulator,
      @Autowired Compatibility compatibility,
      @Autowired StakeholderConsultationPopulator consultationPopulator,
      @Autowired CommentPopulator commentPopulator) {

    compatibility.patch();

    if (!referenceDataPopulator.isPopulated()) {
      referenceDataPopulator.populate();
    }

    if (!userBatchPopulator.isPopulated()) {
      userBatchPopulator.populate();
    }

    if (!accountPopulator.isPopulated()) {
      accountPopulator.populate();
    }

    if (!userPopulator.isPopulated()) {
      userPopulator.populate();
    }

    if (!bundlePopulator.isPopulated()) {
      bundlePopulator.populate();
    }

    if (!projectPopulator.isPopulated()) {
      projectPopulator.populate();
    }

    if (!requestActivityPopulator.isPopulated()) {
      requestActivityPopulator.populate();
    }

    if (!requestPopulator.isPopulated()) {
      requestPopulator.populate();
    }

    if (!documentPopulator.isPopulated()) {
      documentPopulator.populate();
    }

    if (!assignmentPopulator.isPopulated()) {
      assignmentPopulator.populate();
    }

    if (!consultationPopulator.isPopulated()) {
      consultationPopulator.populate();
    }

    if (!commentPopulator.isPopulated()) {
      commentPopulator.populate();
    }
  }

  protected HttpHeaders getHttpHeadersWithToken(String role, UUID id) {
    var token = this.getToken(role, id);
    var headers = new HttpHeaders();
    headers.set(HttpHeaders.COOKIE, buildAuthCookie(token).toString());
    return headers;
  }

  private ResponseCookie buildAuthCookie(String accessToken) {
    return ResponseCookie.from("accessToken", accessToken)
        .httpOnly(true)
        .secure(true)
        .path("/api")
        .maxAge(Duration.ofDays(1))
        .build();
  }

  private String getToken(String role, UUID id) {
    String queryParam = (role != null ? "?role=" + role : "") + (id == null ? "" : "&id=" + id);

    return this.restTemplate.getForObject(baseUrl + "/token" + queryParam, String.class);
  }
}

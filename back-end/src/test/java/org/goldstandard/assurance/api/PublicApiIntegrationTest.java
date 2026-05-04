package org.goldstandard.assurance.api;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static uk.co.mruoc.file.FileLoader.loadContentFromClasspath;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.goldstandard.assurance.api.dto.CommentCreateDTO;
import org.goldstandard.assurance.api.dto.FieldTextAnalysisDTO;
import org.goldstandard.assurance.api.dto.TextAnalysisDTO;
import org.goldstandard.assurance.api.enumeration.PiiProbability;
import org.goldstandard.assurance.api.populator.DocumentPopulator;
import org.goldstandard.assurance.api.populator.ProjectPopulator;
import org.goldstandard.assurance.api.populator.StakeholderConsultationPopulator;
import org.goldstandard.assurance.api.repository.CommentRepository;
import org.goldstandard.assurance.api.repository.StakeholderConsultationRepository;
import org.goldstandard.assurance.api.service.notification.RenderedMessage;
import org.goldstandard.assurance.api.service.pub.RateLimitResetter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;

/**
 * Some APIs require unauthenticated access. These should be tested thoroughly. These tests use full
 * JSON matching on the response as we don't want to accidentally add extra data to the APIs and
 * allow that to go unnoticed.
 */
class PublicApiIntegrationTest extends AbstractApiTest {

  @Autowired CommentRepository commentRepository;

  @Autowired StakeholderConsultationRepository stakeholderConsultationRepository;

  @Autowired StakeholderConsultationPopulator consultationPopulator;

  @Autowired ProjectPopulator projectPopulator;

  @Autowired DocumentPopulator documentPopulator;

  @Autowired CacheManager configuration;

  @Autowired ApiClient client;

  @Autowired RateLimitResetter rateLimitResetter;

  @BeforeEach
  void setUp() {
    rateLimitResetter.resetAll();
  }

  @AfterEach
  void tearDown() {
    rateLimitResetter.resetAll();
  }

  @Test
  void testPublicDocumentsByGSID() {
    var project = projectPopulator.findProjectByGsid("GS1001");

    var response = client.getPublicProjectDocuments(project.getGsid());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var expectedJson = loadContentFromClasspath("json/expected-public-documents-response.json");
    assertThatJson(response.getBody()).isEqualTo(expectedJson);
  }

  @Test
  void shouldNotAllowPublicDownloadOfDocumentsFromProjectsWithANewStatus() {
    givenFileExists();
    var documentId = documentPopulator.findPublicDocumentForProjectWithNewStatus();

    var response = client.getPublicDocument(documentId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldNotAllowPublicDownloadOfPrivateDocuments() {
    givenFileExists();
    var documentId = documentPopulator.findPrivateDocumentId();

    var response = client.getPublicDocument(documentId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldRateLimitCallsToPublicDocumentsDownloadEndpoint() {
    givenFileExists();
    var documentId = documentPopulator.findPublicDocumentId();

    var response = client.getPublicDocument(documentId);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    var secondResponse = client.getPublicDocument(documentId);
    assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
  }

  @Test
  void shouldAllowPublicToPerformTextPiiAnalysis() {
    var field = "description";
    var text = "description with stubbed-pii";
    var request =
        TextAnalysisDTO.builder()
            .fieldsToAnalyse(List.of(new FieldTextAnalysisDTO(field, text)))
            .build();

    var response = client.publicAnalyseText(request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var body = response.getBody();
    assertThat(body.getProbablePii()).hasSize(1);
    var probablePii = body.getProbablePii().stream().findFirst().orElseThrow();
    assertThat(probablePii.getProbability()).isEqualTo(PiiProbability.HIGH);
    assertThat(probablePii.getField()).isEqualTo(field);
    assertThat(probablePii.getIdentifiedString()).isEqualTo(text);
    assertThat(probablePii.getEntity()).isEqualTo("STUBBED_ENTITY");
    assertThat(probablePii.getPageNumber()).isNull();
  }

  @Test
  void shouldRateLimitCallsToPublicTextPiiAnalysisEndpoint() {
    var field = "description";
    var text = "description with stubbed-pii";
    var request =
        TextAnalysisDTO.builder()
            .fieldsToAnalyse(List.of(new FieldTextAnalysisDTO(field, text)))
            .build();

    var response = client.publicAnalyseText(request);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    var secondResponse = client.publicAnalyseText(request);
    assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
  }

  /**
   * This endpoint uses caching, the first call will call the db for the second call we can delete
   * all projects from the stakeholder consultation table which would usually make this response
   * empty because its cached, it will still show the same result as the first call.
   */
  @ParameterizedTest
  @ValueSource(strings = {"fullRequest", "cachedRequest", "resetCache"})
  void publicConsultationEndpointShouldCacheResponses(String runs) {
    if (runs.equals("cachedRequest")) {
      stakeholderConsultationRepository.deleteAll();
    }
    if (runs.equals("resetCache")) {
      configuration.getCache("publicGetAllStakeholderCache").clear();
      consultationPopulator.populate();
    }
    var response = client.getPublicConsultations();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var expectedJson =
        loadContentFromClasspath("json/expected-public-stakeholder-basic-response.json");
    assertThatJson(response.getBody()).isEqualTo(expectedJson);
  }

  @Test
  void shouldReturnPublicConsultation() {
    configuration.getCache("publicGetOneStakeholderCache").clear();

    var response = client.getPublicConsultation("GS1001");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var expectedJson =
        loadContentFromClasspath("json/expected-public-stakeholder-detail-response.json");
    assertThatJson(response.getBody()).isEqualTo(expectedJson);
  }

  @Test
  void shouldRateLimitCallsToPublicCommentsEndpoint() {
    when(emailSender.sendEmailAsync(anyString(), any(RenderedMessage.class)))
        .thenReturn(CompletableFuture.completedFuture("mocked-message-reference"));
    var comment =
        CommentCreateDTO.builder()
            .comment("test")
            .usersEmail("testEmail")
            .usersName("testName")
            .requestId(1)
            .anonymous(false)
            .build();
    var response = client.publicCreateComment(comment);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    try {
      var secondResponse = client.publicCreateComment(comment);

      assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    } finally {
      var id = Objects.requireNonNull(response.getBody());
      commentRepository.deleteById(id);
    }
  }

  @Test
  void shouldReturnGsIdForRequest() {
    var response = client.publicGetRequestProjectGsId(1);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("GS1001");
  }

  private void givenFileExists() {
    var fileData = new byte[] {0, 1, 2, 3, 4};
    given(documentFileStore.objectExists(any())).willReturn(true);
    given(documentFileStore.getObjectAsStream(any()))
        .willReturn(new ByteArrayInputStream(fileData));
  }
}

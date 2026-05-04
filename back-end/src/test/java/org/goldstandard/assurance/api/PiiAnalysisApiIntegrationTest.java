package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.goldstandard.assurance.api.populator.UserPopulator.ANY_USER_ID;
import static org.goldstandard.assurance.api.populator.UserPopulator.NO_ACCOUNT_USER_ID1;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.TAC_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;

import java.util.List;
import java.util.UUID;
import org.goldstandard.assurance.api.dto.FieldTextAnalysisDTO;
import org.goldstandard.assurance.api.dto.TextAnalysisDTO;
import org.goldstandard.assurance.api.entity.RequestDocument;
import org.goldstandard.assurance.api.enumeration.PiiProbability;
import org.goldstandard.assurance.api.fixtures.DocumentMother;
import org.goldstandard.assurance.api.repository.DocumentRepository;
import org.goldstandard.assurance.api.repository.RequestDocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class PiiAnalysisApiIntegrationTest extends AbstractApiTest {

  @Autowired ApiClient client;

  @Autowired DocumentRepository documentRepository;
  @Autowired RequestDocumentRepository requestDocumentRepository;

  private static final UUID DOCUMENT_ID = UUID.fromString("83e31c05-a2f2-49b2-befc-69fe12047efb");

  @ParameterizedTest
  @ValueSource(strings = {ADMIN_ROLE, ARM_TEAM_ROLE, PROJECT_DEVELOPER_ROLE, TAC_ROLE})
  void shouldAllowTextPiiAnalysis(String role) {
    var field = "description";
    var text = "description with stubbed-pii";
    var request =
        TextAnalysisDTO.builder()
            .fieldsToAnalyse(List.of(new FieldTextAnalysisDTO(field, text)))
            .build();

    var response = client.analyseText(role, request);

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

  @ParameterizedTest
  @ValueSource(strings = {GS_REGISTRY_ROLE, FINANCE_ROLE, GS_REVIEWER_ROLE, VVB_ROLE})
  void shouldNotAllowTextPiiAnalysis(String role) {
    var request =
        TextAnalysisDTO.builder()
            .fieldsToAnalyse(List.of(new FieldTextAnalysisDTO("title", "some text")))
            .build();

    var response = client.analyseText(role, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody()).isNotNull();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        PROJECT_DEVELOPER_ROLE,
        VVB_ROLE,
        GS_REGISTRY_ROLE,
        ARM_TEAM_ROLE,
        ADMIN_ROLE,
        GS_REVIEWER_ROLE
      })
  void shouldAllowCheckingDocumentScanningCriteriaForUsersWhoCanUpload(String role) {
    var response = client.checkDocumentSupported(role, MediaType.APPLICATION_PDF_VALUE, 100L);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isTrue();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        TAC_ROLE,
        FINANCE_ROLE,
      })
  void shouldNotAllowCheckingDocumentScanningCriteriaForUsersWhoCannotUpload(String role) {
    assertThat(
            catchThrowable(
                () -> client.checkDocumentSupported(role, MediaType.APPLICATION_PDF_VALUE, 100L)))
        .isInstanceOf(Exception.class);
  }

  @Test
  void shouldNotAllowCheckingDocumentScanningCriteriaUnauthenticated() {
    var response =
        client.checkDocumentSupportedUnauthenticated(MediaType.APPLICATION_PDF_VALUE, 100L);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldAllowAdminToTriggerAnalysis() {
    createTestDocument();
    var response = client.startDocumentAnalysis(ADMIN_ROLE, NO_ACCOUNT_USER_ID1, DOCUMENT_ID);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        PROJECT_DEVELOPER_ROLE,
        TAC_ROLE,
        GS_REVIEWER_ROLE,
        VVB_ROLE,
        FINANCE_ROLE,
        GS_REGISTRY_ROLE
      })
  void shouldNotAllowNonAdminToTriggerAnalysis(String role) {
    var response = client.startDocumentAnalysis(role, ANY_USER_ID, DOCUMENT_ID);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void shouldNotAllowTriggeringAnalysisUnauthenticated() {
    var response = client.startDocumentAnalysisUnauthenticated(DOCUMENT_ID);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {PROJECT_DEVELOPER_ROLE, VVB_ROLE, GS_REGISTRY_ROLE, ARM_TEAM_ROLE, ADMIN_ROLE})
  void shouldAllowGettingAnalysisResultsForUsersWhoCanStartScan(String role) {
    var response = client.getDocumentPiiAnalysisResults(role, DOCUMENT_ID);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        TAC_ROLE,
        GS_REVIEWER_ROLE,
        FINANCE_ROLE,
      })
  void shouldNotAllowGettingAnalysisResultsForUsersWhoCannotStartScan(String role) {
    var response = client.getDocumentPiiAnalysisResults(role, DOCUMENT_ID);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void shouldNotAllowGettingAnalysisResultsUnauthenticated() {
    var response = client.getDocumentPiiAnalysisResultsUnauthenticated(DOCUMENT_ID);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  private void createTestDocument() {
    var document = DocumentMother.document(DOCUMENT_ID, 1);
    documentRepository.save(document);
    var requestDocument = new RequestDocument();
    requestDocument.setDocumentId(DOCUMENT_ID);
    requestDocument.setRequestId(1);
    requestDocumentRepository.add(requestDocument);
  }
}

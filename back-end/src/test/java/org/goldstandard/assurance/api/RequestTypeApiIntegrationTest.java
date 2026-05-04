package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.goldstandard.assurance.api.dto.RequestTypeAssignmentTypes;
import org.goldstandard.assurance.api.entity.RequestTypeConsultation;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

class RequestTypeApiIntegrationTest extends AbstractApiTest {

  private static final String ASSIGNMENTS_FOR_REQUEST_TYPE_ENDPOINT =
      "/request-type-assignment-types";
  private static final String REQUEST_TYPE_CONSULTATION_ENDPOINT = "/request-type-consultations";

  private static final Integer TEST_REQUEST_TYPE_ID = 1000;

  @Test
  void testGetAssignmentTypesForRequestType() {
    var requestEntity = this.getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var expectedAssignmentTypes = Map.of(1, true, 2, false);
    var url =
        String.format(
            "%s/%s/%d", baseUrl, ASSIGNMENTS_FOR_REQUEST_TYPE_ENDPOINT, TEST_REQUEST_TYPE_ID);
    var response =
        this.restTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<RequestTypeAssignmentTypes>() {});

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
    var requestTypeAssignmentTypes = response.getBody();
    assertNotNull(requestTypeAssignmentTypes);
    assertThat(requestTypeAssignmentTypes.getRequestTypeId()).isEqualTo(TEST_REQUEST_TYPE_ID);
    var assignmentTypes =
        requestTypeAssignmentTypes.getAssignmentTypes().stream()
            .collect(
                Collectors.toMap(
                    RequestTypeAssignmentTypes.Rule::getAssignmentTypeId,
                    RequestTypeAssignmentTypes.Rule::getMandatory));
    assertThat(assignmentTypes).isEqualTo(expectedAssignmentTypes);
  }

  @Test
  void testGetAssignmentTypesForRequestTypeUnauthenticatedIsDenied() {
    var url =
        String.format(
            "%s/%s/%d", baseUrl, ASSIGNMENTS_FOR_REQUEST_TYPE_ENDPOINT, TEST_REQUEST_TYPE_ID);
    var response = this.restTemplate.exchange(url, HttpMethod.GET, unauthenticated(), String.class);
    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
  }

  @Test
  void testGetRequestTypesWithConsultationConfiguration() {
    var requestEntity = this.getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var expectedRequestTypeConsultations = Map.of(1001, true, 1000, false, 1002, true);
    var url = String.format("%s/%s", baseUrl, REQUEST_TYPE_CONSULTATION_ENDPOINT);
    var response =
        this.restTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<List<RequestTypeConsultation>>() {});

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
    var requestTypeConsultationList = response.getBody();
    assert requestTypeConsultationList != null;
    requestTypeConsultationList.forEach(
        result -> {
          expectedRequestTypeConsultations.forEach(
              (expectedKey, expectedBool) -> {
                if (expectedKey.equals(result.getRequestTypeId())) {
                  assertEquals(expectedBool, result.getRequiresConsultation());
                }
              });
        });
  }
}

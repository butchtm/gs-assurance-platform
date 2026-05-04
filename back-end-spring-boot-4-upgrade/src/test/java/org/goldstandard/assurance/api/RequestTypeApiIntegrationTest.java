package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;

import java.util.Map;
import java.util.stream.Collectors;
import org.goldstandard.assurance.api.dto.RequestTypeAssignmentTypes;
import org.goldstandard.assurance.api.entity.RequestTypeConsultation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class RequestTypeApiIntegrationTest extends AbstractApiTest {

  private static final Integer REQUEST_TYPE_ID = 1000;

  @Autowired private ApiClient client;

  @Test
  void testGetAssignmentTypesForRequestType() {
    var response = client.getAssignmentTypes(ADMIN_ROLE, REQUEST_TYPE_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getRequestTypeId()).isEqualTo(REQUEST_TYPE_ID);
    var assignmentTypes =
        body.getAssignmentTypes().stream()
            .collect(
                Collectors.toMap(
                    RequestTypeAssignmentTypes.Rule::getAssignmentTypeId,
                    RequestTypeAssignmentTypes.Rule::getMandatory));
    assertThat(assignmentTypes).isEqualTo(Map.of(1, true, 2, false));
  }

  @Test
  void testGetAssignmentTypesForRequestTypeUnauthenticatedIsDenied() {
    var response = client.getAssignmentTypesUnauthenticated(REQUEST_TYPE_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testGetRequestTypesWithConsultationConfiguration() {
    var response = client.getRequestTypesConsultationRequired(ADMIN_ROLE);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var body = response.getBody();
    assertThat(body).isNotNull();
    var results =
        body.stream()
            .collect(
                Collectors.toMap(
                    RequestTypeConsultation::getRequestTypeId,
                    RequestTypeConsultation::getRequiresConsultation));
    assertThat(results).isEqualTo(Map.of(1001, true, 1000, false, 1002, true));
  }
}

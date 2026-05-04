package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.goldstandard.assurance.api.dto.RequestStatusDetail;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

class RequestStatusApiIntegrationTest extends AbstractApiTest {

  private static final String REQUEST_STATUS_DETAIL_ENDPOINT = "/request-status";

  private static final Integer TEST_REQUEST_STATUS_ID = 11;

  private static List<RequestStatusDetail.PermittedStatuses> setupStatuses() {
    var status1 =
        RequestStatusDetail.PermittedStatuses.builder()
            .requestStatusId(7)
            .minimumRoundOfReviewCount(null)
            .permittedDuringConsultation(false)
            .build();
    var status2 =
        RequestStatusDetail.PermittedStatuses.builder()
            .requestStatusId(8)
            .minimumRoundOfReviewCount(null)
            .permittedDuringConsultation(true)
            .build();
    var status3 =
        RequestStatusDetail.PermittedStatuses.builder()
            .requestStatusId(9)
            .minimumRoundOfReviewCount(null)
            .permittedDuringConsultation(true)
            .build();
    var status4 =
        RequestStatusDetail.PermittedStatuses.builder()
            .requestStatusId(10)
            .minimumRoundOfReviewCount(1)
            .permittedDuringConsultation(true)
            .build();
    return new ArrayList<>(List.of(status1, status2, status3, status4));
  }

  private static final Map<Integer, Boolean> EXPECTED_ACCOUNT_TYPES = Map.of(1, true);

  @Test
  void testGetDetailForRequestStatus() {
    var requestEntity = this.getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url =
        String.format("%s/%s/%d", baseUrl, REQUEST_STATUS_DETAIL_ENDPOINT, TEST_REQUEST_STATUS_ID);
    var response =
        this.restTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<RequestStatusDetail>() {});

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
    var requestStatusDetail = response.getBody();
    assertNotNull(requestStatusDetail);
    assertThat(requestStatusDetail.getRequestStatusId()).isEqualTo(TEST_REQUEST_STATUS_ID);

    var expectedStatuses = setupStatuses();
    var listOfPermittedStatuses = requestStatusDetail.getPermittedStatuses();
    listOfPermittedStatuses.forEach(
        status -> {
          expectedStatuses.forEach(
              expectedStatus -> {
                if (expectedStatus.getRequestStatusId().equals(status.getRequestStatusId())) {
                  assertEquals(
                      expectedStatus.getMinimumRoundOfReviewCount(),
                      status.getMinimumRoundOfReviewCount());
                  assertEquals(
                      expectedStatus.isPermittedDuringConsultation(),
                      status.isPermittedDuringConsultation());
                }
              });
        });
    var accountTypes =
        requestStatusDetail.getActiveAccountTypes().stream()
            .collect(
                Collectors.toMap(
                    RequestStatusDetail.InvolvedAccountTypes::getAccountTypeId,
                    RequestStatusDetail.InvolvedAccountTypes::getCanProgress));
    assertThat(accountTypes).isEqualTo(EXPECTED_ACCOUNT_TYPES);
  }

  @Test
  void testGetDetailForRequestStatusUnauthenticatedIsDenied() {
    var url =
        String.format("%s/%s/%d", baseUrl, REQUEST_STATUS_DETAIL_ENDPOINT, TEST_REQUEST_STATUS_ID);
    var response = this.restTemplate.exchange(url, HttpMethod.GET, unauthenticated(), String.class);
    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
  }
}

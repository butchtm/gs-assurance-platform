package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.goldstandard.assurance.api.dto.RequestStatusDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RequestStatusApiIntegrationTest extends AbstractApiTest {

  private static final Integer REQUEST_STATUS_ID = 11;

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

  @Autowired private ApiClient client;

  @Test
  void testGetDetailForRequestStatus() {
    var response = client.getRequestStatusDetail(REQUEST_STATUS_ID);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    var requestStatusDetail = response.getBody();
    assertNotNull(requestStatusDetail);
    assertThat(requestStatusDetail.getRequestStatusId()).isEqualTo(REQUEST_STATUS_ID);

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
    var response = client.getRequestStatusDetailUnauthenticated(REQUEST_STATUS_ID);

    assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
  }
}

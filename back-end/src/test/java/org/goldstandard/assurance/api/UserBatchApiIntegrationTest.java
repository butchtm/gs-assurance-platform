package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.populator.AccountPopulator.GS_ACCOUNT_ID;
import static org.goldstandard.assurance.api.populator.UserBatchPopulator.FAILED_BATCH_ID;
import static org.goldstandard.assurance.api.populator.UserBatchPopulator.INCOMPLETE_BATCH_ID;
import static org.goldstandard.assurance.api.populator.UserPopulator.NO_ACCOUNT_USER_ID1;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.TAC_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.goldstandard.assurance.api.entity.UserBatch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class UserBatchApiIntegrationTest extends AbstractApiTest {

  private static final String ENDPOINT_TO_TEST = "/users/batches";

  @Test
  void testGetUserBulkRequest() {
    var requestEntity = this.getHttpEntityWithTokenForRoleWithId(ADMIN_ROLE, NO_ACCOUNT_USER_ID1);
    var response =
        this.restTemplate.exchange(
            baseUrl + ENDPOINT_TO_TEST + "/1",
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<UserBatch>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var batch = response.getBody();
    assertNotNull(batch);
    assertEquals(1, batch.getId());
    assertTrue(batch.isComplete());
    assertNotNull(batch.getCreatedAt());
    assertNotNull(batch.getUpdatedAt());
    assertNotNull(batch.getErrors());
    assertEquals(0, batch.getErrors().size());
  }

  @Test
  void testGetUserBulkRequestForIncompleteBatch() {
    var requestEntity = this.getHttpEntityWithTokenForRoleWithId(ADMIN_ROLE, NO_ACCOUNT_USER_ID1);
    var response =
        this.restTemplate.exchange(
            baseUrl + ENDPOINT_TO_TEST + "/" + INCOMPLETE_BATCH_ID,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<UserBatch>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var batch = response.getBody();
    assertNotNull(batch);
    assertEquals(INCOMPLETE_BATCH_ID, batch.getId());
    assertFalse(batch.isComplete());
    assertNotNull(batch.getCreatedAt());
    assertNotNull(batch.getUpdatedAt());
    assertNotNull(batch.getErrors());
    assertEquals(0, batch.getErrors().size());
  }

  @Test
  void testGetUserBulkRequestForFailedBatch() {
    var requestEntity = this.getHttpEntityWithTokenForRoleWithId(ADMIN_ROLE, NO_ACCOUNT_USER_ID1);
    var response =
        this.restTemplate.exchange(
            baseUrl + ENDPOINT_TO_TEST + "/" + FAILED_BATCH_ID,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<UserBatch>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var batch = response.getBody();
    assertNotNull(batch);
    assertEquals(FAILED_BATCH_ID, batch.getId());
    assertTrue(batch.isComplete());
    assertNotNull(batch.getCreatedAt());
    assertNotNull(batch.getUpdatedAt());
    assertNotNull(batch.getErrors());
    assertEquals(1, batch.getErrors().size());
  }

  @Test
  void testGetUserBulkRequestForNotfound() {
    var requestEntity = this.getHttpEntityWithTokenForRoleWithId(ADMIN_ROLE, NO_ACCOUNT_USER_ID1);
    var response =
        this.restTemplate.exchange(
            baseUrl + ENDPOINT_TO_TEST + "/999",
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<UserBatch>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        ARM_TEAM_ROLE,
        GS_REGISTRY_ROLE,
        FINANCE_ROLE,
        GS_REVIEWER_ROLE,
        PROJECT_DEVELOPER_ROLE,
        VVB_ROLE,
        TAC_ROLE
      })
  void testGetUserBulkRequestForNonAdminShouldReturnForbidden(String role) {
    var requestEntity = this.getHttpEntityWithTokenForRoleWithId(role, NO_ACCOUNT_USER_ID1);
    var response =
        this.restTemplate.exchange(
            baseUrl + ENDPOINT_TO_TEST + "/1",
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<UserBatch>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void testPostUserBulkRequest() {
    var count = 10;
    var data = buildUploadData(count);
    var requestEntity =
        this.getHttpEntityWithTokenAndFormBodyForRoleWithId(data, ADMIN_ROLE, NO_ACCOUNT_USER_ID1);

    var response =
        this.restTemplate.exchange(
            baseUrl + ENDPOINT_TO_TEST,
            HttpMethod.POST,
            requestEntity,
            new ParameterizedTypeReference<UserBatch>() {});
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    var batch = response.getBody();
    assertNotNull(batch);
    assertEquals(count, batch.getCount());
    assertNotNull(batch.getCreatedAt());
    assertNotNull(batch.getUpdatedAt());
    assertNotNull(batch.getErrors());
    assertEquals(0, batch.getErrors().size());
    assertFalse(batch.isComplete());
    assertEquals(0, batch.getSuccessCount());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        ARM_TEAM_ROLE,
        GS_REGISTRY_ROLE,
        FINANCE_ROLE,
        GS_REVIEWER_ROLE,
        PROJECT_DEVELOPER_ROLE,
        VVB_ROLE,
        TAC_ROLE
      })
  void testPostUserBulkRequestForNonAdminShouldReturnForbidden(String role) {
    var requestEntity =
        this.getHttpEntityWithTokenAndFormBodyForRoleWithId(
            buildUploadData(1), role, NO_ACCOUNT_USER_ID1);
    var response =
        this.restTemplate.exchange(
            baseUrl + ENDPOINT_TO_TEST,
            HttpMethod.POST,
            requestEntity,
            new ParameterizedTypeReference<UserBatch>() {});
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  private MultiValueMap<String, Object> buildUploadData(int count) {
    MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();

    HttpHeaders parts = new HttpHeaders();
    ByteArrayResource fileData =
        new ByteArrayResource(buildCsv(count).getBytes()) {
          // This override is required because the default method returns null.
          @Override
          public String getFilename() {
            return "users.csv";
          }
        };
    HttpEntity<ByteArrayResource> csvEntity = new HttpEntity<ByteArrayResource>(fileData, parts);

    data.add("data", csvEntity);
    return data;
  }

  private String buildCsv(int count) {
    StringBuilder sb = new StringBuilder("userName,firstName,lastName,email,groups,accountId\n");
    for (var i = 0; i < count; i++) {
      sb.append(
          String.format(
              "userName%d,firstName%d,lastName%d,username%d@email.com,grp%d~grp1%d~grp2%d,%d\n",
              i, i, i, i, i, i, i, GS_ACCOUNT_ID));
    }
    return sb.toString();
  }
}

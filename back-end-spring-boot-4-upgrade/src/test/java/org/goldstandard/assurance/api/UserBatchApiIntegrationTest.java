package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.populator.AccountPopulator.GS_ACCOUNT_ID;
import static org.goldstandard.assurance.api.populator.UserBatchPopulator.FAILED_BATCH_ID;
import static org.goldstandard.assurance.api.populator.UserBatchPopulator.INCOMPLETE_BATCH_ID;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.TAC_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class UserBatchApiIntegrationTest extends AbstractApiTest {

  @Autowired ApiClient client;

  @Test
  void testGetUserBulkRequest() {
    var response = client.getUserBatch(1);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var batch = response.getBody();
    assertThat(batch).isNotNull();
    assertThat(batch.getId()).isOne();
    assertThat(batch.isComplete()).isTrue();
    assertThat(batch.getCreatedAt()).isNotNull();
    assertThat(batch.getUpdatedAt()).isNotNull();
    assertThat(batch.getErrors()).isEmpty();
  }

  @Test
  void testGetUserBulkRequestForIncompleteBatch() {
    var response = client.getUserBatch(INCOMPLETE_BATCH_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var batch = response.getBody();
    assertThat(batch).isNotNull();
    assertThat(batch.getId()).isEqualTo(INCOMPLETE_BATCH_ID);
    assertThat(batch.isComplete()).isFalse();
    assertThat(batch.getCreatedAt()).isNotNull();
    assertThat(batch.getUpdatedAt()).isNotNull();
    assertThat(batch.getErrors()).isEmpty();
  }

  @Test
  void testGetUserBulkRequestForFailedBatch() {
    var response = client.getUserBatch(FAILED_BATCH_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var batch = response.getBody();
    assertThat(batch).isNotNull();
    assertThat(batch.getId()).isEqualTo(FAILED_BATCH_ID);
    assertThat(batch.isComplete()).isTrue();
    assertThat(batch.getCreatedAt()).isNotNull();
    assertThat(batch.getUpdatedAt()).isNotNull();
    assertThat(batch.getErrors()).hasSize(1);
  }

  @Test
  void testGetUserBulkRequestForNotFound() {
    var response = client.getUserBatch(999);

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
    var response = client.getUserBatch(role, 1);

    assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
  }

  @Test
  void testPostUserBulkRequest() {
    var count = 10;
    var data = buildUploadData(count);

    var response = client.createUserBatch(ADMIN_ROLE, data);

    assertThat(response.getStatusCode()).isEqualTo(ACCEPTED);
    var batch = response.getBody();
    assertThat(batch).isNotNull();
    assertThat(batch.getId()).isPositive();
    assertThat(batch.getCount()).isEqualTo(count);
    assertThat(batch.isComplete()).isFalse();
    assertThat(batch.getCreatedAt()).isNotNull();
    assertThat(batch.getUpdatedAt()).isNotNull();
    assertThat(batch.getErrors()).isEmpty();
    assertThat(batch.isComplete()).isFalse();
    assertThat(batch.getSuccessCount()).isZero();
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
    var data = buildUploadData(1);

    var response = client.createUserBatch(role, data);

    assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
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
    HttpEntity<ByteArrayResource> csvEntity = new HttpEntity<>(fileData, parts);

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

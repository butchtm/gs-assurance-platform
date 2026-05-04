package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.populator.ProjectPopulator.TEST_PROJECTS;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class BundleApiIntegrationTest extends AbstractApiTest {

  @Autowired ApiClient client;

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3})
  void testGetBundleProjects(int bundleId) {
    var projectInBundle = TEST_PROJECTS.get(bundleId - 1);
    var response = client.getBundleProjects(bundleId);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var bundleProjects = response.getBody().getContent();
    assertThat(bundleProjects).hasSizeGreaterThanOrEqualTo(1);
    assertThat(bundleProjects.getFirst().getId()).isEqualTo(projectInBundle.getId());
    assertThat(bundleProjects.getFirst().getCurrentRequest().getId())
        .isEqualTo(projectInBundle.getCurrentRequestId());
  }
}

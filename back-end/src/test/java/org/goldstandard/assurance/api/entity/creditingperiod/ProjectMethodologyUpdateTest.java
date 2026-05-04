package org.goldstandard.assurance.api.entity.creditingperiod;

import static org.assertj.core.api.Assertions.assertThat;

import org.goldstandard.assurance.api.entity.Request;
import org.junit.jupiter.api.Test;

class ProjectMethodologyUpdateTest {

  @Test
  void shouldReturnRequestIdIfRequestPresent() {
    var expectedRequestId = 1;
    var request = Request.builder().id(expectedRequestId).build();
    var update = ProjectMethodologyUpdate.builder().request(request).build();

    assertThat(update.getRequestId()).isEqualTo(expectedRequestId);
  }

  @Test
  void shouldReturnNullRequestIdIfRequestNotPresent() {
    var update = ProjectMethodologyUpdate.builder().build();

    assertThat(update.getRequestId()).isNull();
  }
}

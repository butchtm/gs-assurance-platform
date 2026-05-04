package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.entity.view.ReferenceDataView;
import org.goldstandard.assurance.api.entity.view.ReferenceDataViewId;
import org.goldstandard.assurance.api.repository.ReferenceDataViewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class ReferenceDataApiIntegrationTest extends AbstractApiTest {

  private static final String ENDPOINT_TO_TEST = "/reference-data-types";

  // This view will not be created as the code currently doesn't maintain the database so we have to
  // mock this one
  @MockitoBean private ReferenceDataViewRepository mockRepo;

  @Test
  void getReferenceData() {
    given(mockRepo.findAll()).willReturn(generateMockReferenceData());

    var requestEntity = this.getHttpEntityWithToken();
    var response =
        this.restTemplate.exchange(
            baseUrl + ENDPOINT_TO_TEST,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<Map<String, List<ReferenceDataType>>>() {});

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertNotNull(response.getBody());
    var referenceData = response.getBody();
    assertNotNull(referenceData);

    // Verify we get back all the keys we expect for reference data
    assertThat(referenceData.keySet().size()).isEqualTo(3);
    assertThat(referenceData.get("REF 1").size()).isEqualTo(2);
    assertThat(referenceData.get("REF 1").getFirst().getId()).isEqualTo(1);
    assertThat(referenceData.get("REF 1").getFirst().getCode()).isEqualTo("CODE 1 - 1");
    assertThat(referenceData.get("REF 1").getFirst().getDescription())
        .isEqualTo("Description 1 - 1");
  }

  @Test
  void testGetUsersUnauthenticatedIsDenied() {
    var response =
        this.restTemplate.exchange(
            baseUrl + ENDPOINT_TO_TEST, HttpMethod.GET, unauthenticated(), String.class);

    assertThat(response.getStatusCode().value()).isEqualTo(401);
  }

  private List<ReferenceDataView> generateMockReferenceData() {
    var lst = new ArrayList<ReferenceDataView>();
    for (int i = 1; i <= 3; i++) {
      var ref1_1 = new ReferenceDataView();
      ref1_1.setId(new ReferenceDataViewId("REF " + i, 1));
      ref1_1.setCode("CODE 1 - " + i);
      ref1_1.setDescription("Description 1 - " + i);

      var ref1_2 = new ReferenceDataView();
      ref1_2.setId(new ReferenceDataViewId("REF " + i, 2));
      ref1_2.setCode("CODE 2 - " + i);
      ref1_2.setDescription("Description 2 - " + i);

      lst.add(ref1_1);
      lst.add(ref1_2);
    }

    return lst;
  }
}

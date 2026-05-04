package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.goldstandard.assurance.api.dto.RequestMethodologyDTO;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.repository.MethodologyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestMethodologyFactoryTest {

  @InjectMocks RequestMethodologyFactory factory;

  @Mock MethodologyRepository repository;

  @Test
  void toRequestMethodology_shouldCreateRequestMethodology() {
    var dto = RequestMethodologyDTO.builder().methodologyId(10).methodologyVersion(2.0).build();
    var expectedMethodology = givenMethodologyFound(dto.getMethodologyId());
    var request = buildRequest();

    var requestMethodologies = factory.toMethodologies(List.of(dto), request);

    assertThat(requestMethodologies).hasSize(1);
    var requestMethodology = requestMethodologies.getFirst();
    var id = requestMethodology.getId();
    assertThat(id.getRequestId()).isEqualTo(request.getId());
    assertThat(id.getMethodologyId()).isEqualTo(expectedMethodology.getId());
    assertThat(requestMethodology.getMethodologyVersion()).isEqualTo(dto.getMethodologyVersion());
    assertThat(requestMethodology.getMethodology()).isEqualTo(expectedMethodology);
    assertThat(requestMethodology.getRequest()).isEqualTo(request);
  }

  private Methodology givenMethodologyFound(int id) {
    var methodology = mock(Methodology.class);
    when(methodology.getId()).thenReturn(id);
    when(repository.forceFindById(id)).thenReturn(methodology);
    return methodology;
  }

  private static Request buildRequest() {
    return Request.builder().id(987).creditingPeriod(3).build();
  }
}

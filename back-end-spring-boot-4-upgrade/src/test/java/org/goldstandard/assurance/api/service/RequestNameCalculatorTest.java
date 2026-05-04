package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.repository.RequestRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestNameCalculatorTest {

  @InjectMocks private RequestNameCalculator calculator;

  @Mock private RequestRepository repository;

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2})
  void shouldIncrementRequestCountByTypeWhenCalculatingRequestName(int requestCountByType) {
    int requestTypeId = 99;
    String description = "My Request";
    when(repository.findRequestDescriptionForTypeId(requestTypeId)).thenReturn(description);

    String name = calculator.calculateName(requestTypeId, requestCountByType);

    assertThat(name).isEqualTo(String.format("%s no. %d", description, requestCountByType + 1));
  }
}

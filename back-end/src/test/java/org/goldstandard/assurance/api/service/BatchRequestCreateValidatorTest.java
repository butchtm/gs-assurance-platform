package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.stream.IntStream;
import org.assertj.core.api.ThrowableAssert;
import org.goldstandard.assurance.api.dto.BatchRequestCreateDTO;
import org.goldstandard.assurance.api.dto.BatchRequestCreateDTO.ProjectRequestInfo;
import org.goldstandard.assurance.api.exception.BatchRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BatchRequestCreateValidatorTest {

  @InjectMocks private BatchRequestCreateValidator validator;

  @Test
  void shouldNotThrowExceptionIfCreateDTOIsValid() {
    BatchRequestCreateDTO create = batchCreateWithProjects(2);

    ThrowableAssert.ThrowingCallable call = () -> validator.validate(create);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 0})
  void shouldThrowExceptionIfCreateDTOHasLessThanTwoProjects(int numberOfProjects) {
    BatchRequestCreateDTO create = batchCreateWithProjects(numberOfProjects);

    Throwable error = catchThrowable(() -> validator.validate(create));

    assertThat(error)
        .isInstanceOf(BatchRequestException.class)
        .hasMessage("a batch request must contain more than one project");
  }

  private static BatchRequestCreateDTO batchCreateWithProjects(int numberOfProjects) {
    return BatchRequestCreateDTO.builder()
        .projectRequestInfos(createBatchProjects(numberOfProjects))
        .build();
  }

  private static List<BatchRequestCreateDTO.ProjectRequestInfo> createBatchProjects(
      int numberOfProjects) {
    return IntStream.range(0, numberOfProjects)
        .mapToObj(i -> mock(ProjectRequestInfo.class))
        .toList();
  }
}

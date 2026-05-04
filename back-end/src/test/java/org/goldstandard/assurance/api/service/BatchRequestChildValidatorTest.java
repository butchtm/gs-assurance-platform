package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.goldstandard.assurance.api.entity.Batch;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.exception.BatchRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BatchRequestChildValidatorTest {

  @InjectMocks private BatchRequestChildValidator validator;

  @Test
  void shouldNotThrowExceptionForParentRequest() {
    var parentRequest = createRequest(1);
    createBatch(parentRequest);

    ThrowingCallable call = () -> validator.validateIsNotBatchChild(parentRequest);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldNotThrowExceptionForNonBatchRequest() {
    var request = createRequest(2);

    ThrowingCallable call = () -> validator.validateIsNotBatchChild(request);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldThrowExceptionForChildRequest() {
    var parent = createRequest(3);
    var batch = createBatch(parent);
    var child = createRequest(4);
    child.setBatch(batch);

    Throwable error = catchThrowable(() -> validator.validateIsNotBatchChild(child));

    assertThat(error).isInstanceOf(BatchRequestException.class);
  }

  private static Batch createBatch(Request request) {
    var batch = new Batch();
    batch.setParentRequest(request);
    request.setBatch(batch);
    return batch;
  }

  private static Request createRequest(int id) {
    var request = new Request();
    request.setId(id);
    return request;
  }
}

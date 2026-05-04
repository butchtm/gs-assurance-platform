package org.goldstandard.assurance.api.service.external;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

@ExtendWith(MockitoExtension.class)
class ExternalUpdateFailureHandlerTest {

  @InjectMocks ExternalUpdateFailureHandler failureHandler;

  @Mock ExternalUpdateConfig config;

  @Test
  void shouldThrowAbortExceptionIfStatusCodeIsInAbortCodes() {
    when(config.getAbortCodes()).thenReturn(List.of(429, 500));
    when(config.getSystem()).thenReturn(ExternalSystem.REGISTRY);
    var error = Mockito.mock(ExternalApiClientException.class);
    when(error.getStatusCode()).thenReturn(Optional.of(HttpStatusCode.valueOf(429)));

    var result = catchThrowable(() -> failureHandler.abortIfRequired(error));

    assertThat(result).isInstanceOf(ExternalUpdateAbortException.class).hasCause(error);
  }

  @Test
  void shouldNotThrowAbortExceptionIfStatusCodeNotInAbortCodes() {
    when(config.getAbortCodes()).thenReturn(List.of(429, 500));
    var error = mock(ExternalApiClientException.class);
    when(error.getStatusCode()).thenReturn(Optional.of(HttpStatusCode.valueOf(404)));

    ThrowingCallable call = () -> failureHandler.abortIfRequired(error);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldNotThrowAbortExceptionIfStatusCodeNotPresent() {
    var error = mock(ExternalApiClientException.class);
    when(error.getStatusCode()).thenReturn(Optional.empty());

    ThrowingCallable call = () -> failureHandler.abortIfRequired(error);

    assertThatCode(call).doesNotThrowAnyException();
  }
}

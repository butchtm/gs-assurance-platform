package org.goldstandard.assurance.api.service.external.registry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatusCode;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

class StubRegistryApiClientTest {

  private final RegistryApiClient client = new StubRegistryApiClient(new ObjectMapper());

  @ParameterizedTest
  @MethodSource("nameAndExpectedStatus")
  void shouldThrowErrorOfSpecifiedType(String name, int expectedStatus) {
    var project = RegistryProject.builder().name(name).build();

    var error =
        catchThrowableOfType(RegistryApiClientException.class, () -> client.updateProject(project));

    assertThat(error).hasMessage(project.getName());
    assertThat(error.getStatusCode()).contains(HttpStatusCode.valueOf(expectedStatus));
  }

  @Test
  void shouldNotThrowErrorIfNameDoesNotContainError() {
    var project = RegistryProject.builder().name("name").build();

    ThrowingCallable call = () -> client.updateProject(project);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldThrowExceptionIfJsonConversionOfRegistryProjectFails() {
    var mapper = mock(ObjectMapper.class);
    var project = RegistryProject.builder().name("name").build();
    var expectedCause = mock(JacksonException.class);
    doThrow(expectedCause).when(mapper).writeValueAsString(project);
    var stubClient = new StubRegistryApiClient(mapper);

    var error =
        catchThrowableOfType(
            RegistryApiClientException.class, () -> stubClient.updateProject(project));

    assertThat(error).hasCause(expectedCause);
  }

  private static Stream<Arguments> nameAndExpectedStatus() {
    return Stream.of(
        Arguments.of("error-429", 429),
        Arguments.of("error-404", 404),
        Arguments.of("error-  429  ", 429),
        Arguments.of("error-abc", 500),
        Arguments.of("error-", 500),
        Arguments.of("error", 500));
  }
}

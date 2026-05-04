package org.goldstandard.assurance.api.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RemoteAddressExtractorTest {

  @Mock HttpServletRequest request;

  private final RemoteAddressExtractor extractor = new RemoteAddressExtractor();

  @Test
  void shouldReturnNullIfBothRemoteAddressAndForwardedForAreNull() {
    when(request.getHeader("X-Forwarded-For")).thenReturn(null);
    when(request.getRemoteAddr()).thenReturn(null);

    var result = extractor.extractRemoteAddress(request);

    assertThat(result).isNull();
  }

  @Test
  void shouldReturnForwardedForHeaderValueIfProvided() {
    var forwardedFor = "2.2.2.2";
    when(request.getHeader("X-Forwarded-For")).thenReturn(forwardedFor);
    var remoteAddress = "3.3.3.3";
    when(request.getRemoteAddr()).thenReturn(remoteAddress);

    var result = extractor.extractRemoteAddress(request);

    assertThat(result).isEqualTo(forwardedFor);
  }

  @ParameterizedTest
  @ValueSource(strings = {""})
  @NullSource
  void shouldReturnRemoteAddressIfForwardedForHeaderValueIsNotProvided(String forwardedFor) {
    when(request.getHeader("X-Forwarded-For")).thenReturn(forwardedFor);
    var remoteAddress = "1.1.1.1";
    when(request.getRemoteAddr()).thenReturn(remoteAddress);

    var result = extractor.extractRemoteAddress(request);

    assertThat(result).isEqualTo(remoteAddress);
  }
}

package org.goldstandard.assurance.api.service.mfa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.goldstandard.assurance.api.dto.mfa.MfaSettingsDTO;
import org.goldstandard.assurance.api.dto.mfa.VerifySoftwareTokenRequestDTO;
import org.goldstandard.assurance.api.exception.MfaException;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.goldstandard.assurance.api.service.auth.CognitoClient;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AssociateSoftwareTokenResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.VerifySoftwareTokenResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.VerifySoftwareTokenResponseType;

@ExtendWith(MockitoExtension.class)
class MfaServiceTest {

  @InjectMocks private MfaService service;

  @Mock private CognitoClient cognitoClient;
  @Mock private PermissionsService permissionsService;
  @Mock private AuthenticatedUserSupplier userSupplier;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(service, "userPoolId", "TEST_POOL");
  }

  @Test
  void getMfaSettingsAsAdmin() {
    when(permissionsService.isAdmin()).thenReturn(true);
    var targetUsername = "someone@example.com";
    var resp =
        GetUserResponse.builder()
            .username(targetUsername)
            .userMFASettingList("SOFTWARE_TOKEN_MFA", "EMAIL_OTP")
            .build();
    when(cognitoClient.adminGetUser("TEST_POOL", targetUsername)).thenReturn(resp);

    var dto = service.getMfaSettings(targetUsername);

    assertThat(dto.getUsername()).isEqualTo(targetUsername);
    assertThat(dto.isSoftwareTokenMfaEnabled()).isTrue();
    assertThat(dto.isEmailMfaEnabled()).isTrue();
  }

  @Test
  void getMfaSettingsAsUser() {
    when(permissionsService.isAdmin()).thenReturn(false);
    when(userSupplier.getAuthenticatedUsername()).thenReturn(Optional.of("self@example.com"));

    when(userSupplier.forceGetAuthenticatedAccessToken()).thenReturn("access-token");

    var response =
        GetUserResponse.builder()
            .username("self@example.com")
            .userMFASettingList("EMAIL_OTP")
            .build();

    when(cognitoClient.getUser("access-token")).thenReturn(response);

    var dto = service.getMfaSettings(null);

    assertThat(dto.getUsername()).isEqualTo("self@example.com");
    assertThat(dto.isSoftwareTokenMfaEnabled()).isFalse();
    assertThat(dto.isEmailMfaEnabled()).isTrue();
  }

  @Test
  void updateMfaSettingsAsAdmin() {
    when(permissionsService.isAdmin()).thenReturn(true);

    var request =
        MfaSettingsDTO.builder()
            .username("target@example.com")
            .softwareTokenMfaEnabled(true)
            .emailMfaEnabled(false)
            .build();

    var response = service.updateMfaSettings(request);

    assertThat(response.isSuccess()).isTrue();
    assertThat(response.isSoftwareTokenMfaEnabled()).isTrue();
    assertThat(response.isEmailMfaEnabled()).isFalse();

    var dtoCaptor = ArgumentCaptor.forClass(MfaSettingsDTO.class);
    verify(cognitoClient)
        .setUserMfaPreferenceAsAdmin(
            eq("TEST_POOL"), eq("target@example.com"), dtoCaptor.capture());
    var sent = dtoCaptor.getValue();
    assertThat(sent.isSoftwareTokenMfaEnabled()).isTrue();
    assertThat(sent.isEmailMfaEnabled()).isFalse();
  }

  @Test
  void updateMfaSettingsAsUser() {
    when(permissionsService.isAdmin()).thenReturn(false);
    when(userSupplier.forceGetAuthenticatedAccessToken()).thenReturn("AT");

    var request =
        MfaSettingsDTO.builder()
            .username(null)
            .softwareTokenMfaEnabled(false)
            .emailMfaEnabled(true)
            .build();

    var response = service.updateMfaSettings(request);

    assertThat(response.isSuccess()).isTrue();
    assertThat(response.isSoftwareTokenMfaEnabled()).isFalse();
    assertThat(response.isEmailMfaEnabled()).isTrue();

    var dtoCaptor = ArgumentCaptor.forClass(MfaSettingsDTO.class);
    verify(cognitoClient).setUserMfaPreference(eq("AT"), dtoCaptor.capture());
    var sent = dtoCaptor.getValue();
    assertThat(sent.isSoftwareTokenMfaEnabled()).isFalse();
    assertThat(sent.isEmailMfaEnabled()).isTrue();
  }

  @Test
  void updateMfaSettingsShowsMfaException() {
    when(permissionsService.isAdmin()).thenReturn(false);
    when(userSupplier.forceGetAuthenticatedAccessToken()).thenReturn("AT");
    var request = MfaSettingsDTO.builder().softwareTokenMfaEnabled(true).build();
    var expectedCause = new RuntimeException("boom");
    doThrow(expectedCause).when(cognitoClient).setUserMfaPreference("AT", request);

    var error = catchThrowable(() -> service.updateMfaSettings(request));

    assertThat(error).isInstanceOf(MfaException.class).hasCause(expectedCause);
  }

  @Test
  void setupSoftwareTokenReturnsSecretAndOtpAuthUrl() {
    when(userSupplier.forceGetAuthenticatedAccessToken()).thenReturn("access-token");
    when(userSupplier.forceGetAuthenticatedUsername()).thenReturn("self@example.com");

    when(cognitoClient.associateSoftwareToken("access-token"))
        .thenReturn(AssociateSoftwareTokenResponse.builder().secretCode("ABC123").build());

    var dto = service.setupSoftwareToken();

    assertThat(dto.getSecretCode()).isEqualTo("ABC123");
    assertThat(dto.getOtpAuthUrl())
        .startsWith("otpauth://totp/GoldStandardAssurance:self@example.com")
        .contains("secret=ABC123")
        .contains("issuer=GoldStandardAssurance");
  }

  @Test
  void setupSoftwareTokenShowsMfaException() {
    when(userSupplier.forceGetAuthenticatedAccessToken()).thenReturn("AT");
    var expectedCause = new RuntimeException("nope");
    when(cognitoClient.associateSoftwareToken("AT")).thenThrow(expectedCause);

    var error = catchThrowable(() -> service.setupSoftwareToken());

    assertThat(error).isInstanceOf(MfaException.class).hasCause(expectedCause);
  }

  @Test
  void verifySoftwareTokenEnablesSoftwareToken() {
    when(userSupplier.forceGetAuthenticatedAccessToken()).thenReturn("AT");
    when(cognitoClient.verifySoftwareToken("AT", "123456", true))
        .thenReturn(
            VerifySoftwareTokenResponse.builder()
                .status(VerifySoftwareTokenResponseType.SUCCESS)
                .build());

    var result =
        service.verifySoftwareToken(
            VerifySoftwareTokenRequestDTO.builder().code("123456").emailMfaEnabled(false).build());

    assertThat(result.isVerified()).isTrue();

    var dtoCaptor = ArgumentCaptor.forClass(MfaSettingsDTO.class);
    verify(cognitoClient).setUserMfaPreference(eq("AT"), dtoCaptor.capture());
    var sent = dtoCaptor.getValue();
    assertThat(sent.isSoftwareTokenMfaEnabled()).isTrue();
    assertThat(sent.isEmailMfaEnabled()).isFalse();
  }

  @Test
  void verifySoftwareTokenFailReturnsFalse() {
    when(userSupplier.forceGetAuthenticatedAccessToken()).thenReturn("AT");
    when(cognitoClient.verifySoftwareToken("AT", "000000", true))
        .thenReturn(
            VerifySoftwareTokenResponse.builder()
                .status(VerifySoftwareTokenResponseType.ERROR)
                .build());

    var result =
        service.verifySoftwareToken(
            VerifySoftwareTokenRequestDTO.builder().code("000000").emailMfaEnabled(false).build());

    assertThat(result.isVerified()).isFalse();
    verify(cognitoClient, never()).setUserMfaPreference(anyString(), any(MfaSettingsDTO.class));
  }
}

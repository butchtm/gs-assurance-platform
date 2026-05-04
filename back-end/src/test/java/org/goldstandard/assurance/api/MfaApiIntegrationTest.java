package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.populator.UserPopulator.PROJECT_DEVELOPER_USER_ID;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.mfa.MfaSettingsDTO;
import org.goldstandard.assurance.api.dto.mfa.SoftwareTokenSetupResponseDTO;
import org.goldstandard.assurance.api.dto.mfa.VerifySoftwareTokenRequestDTO;
import org.goldstandard.assurance.api.dto.mfa.VerifySoftwareTokenResponseDTO;
import org.goldstandard.assurance.api.service.auth.CognitoClient;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AssociateSoftwareTokenResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.VerifySoftwareTokenResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.VerifySoftwareTokenResponseType;

@RequiredArgsConstructor
class MfaApiIntegrationTest extends AbstractApiTest {

  @Autowired ApiClient client;

  @MockitoBean CognitoClient cognitoClient;

  @Test
  void getMfaSettingsAsAdmin() {
    var targetUsername = "target.user@example.com";
    when(cognitoClient.adminGetUser(any(), eq(targetUsername)))
        .thenReturn(
            GetUserResponse.builder()
                .username(targetUsername)
                .userMFASettingList("SOFTWARE_TOKEN_MFA", "EMAIL_OTP")
                .build());

    var response = client.getMfaSettingsAsAdmin(targetUsername);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getUsername()).isEqualTo(targetUsername);
    assertThat(body.isSoftwareTokenMfaEnabled()).isTrue();
    assertThat(body.isEmailMfaEnabled()).isTrue();
  }

  @Test
  void getMfaSettingsAsUser() {
    when(cognitoClient.getUser(any()))
        .thenReturn(
            GetUserResponse.builder()
                .username("self.user@example.com")
                .userMFASettingList("EMAIL_OTP")
                .build());

    var response = client.getMfaSettings(PROJECT_DEVELOPER_ROLE, PROJECT_DEVELOPER_USER_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.isSoftwareTokenMfaEnabled()).isFalse();
    assertThat(body.isEmailMfaEnabled()).isTrue();
  }

  @Test
  void updateMfaSettingsAsAdmin() {
    var request =
        MfaSettingsDTO.builder()
            .username("admin.target@example.com")
            .softwareTokenMfaEnabled(true)
            .emailMfaEnabled(false)
            .build();

    var response = client.updateMfaSettings(ADMIN_ROLE, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.isSuccess()).isTrue();
    assertThat(body.isSoftwareTokenMfaEnabled()).isTrue();
    assertThat(body.isEmailMfaEnabled()).isFalse();

    var dtoCaptor = ArgumentCaptor.forClass(MfaSettingsDTO.class);

    verify(cognitoClient)
        .setUserMfaPreferenceAsAdmin(any(), eq("admin.target@example.com"), dtoCaptor.capture());

    var sent = dtoCaptor.getValue();
    assertThat(sent.isSoftwareTokenMfaEnabled()).isTrue();
    assertThat(sent.isEmailMfaEnabled()).isFalse();
  }

  @Test
  void updateMfaSettingsAsUser() {
    var request =
        MfaSettingsDTO.builder()
            .username(null)
            .softwareTokenMfaEnabled(false)
            .emailMfaEnabled(true)
            .build();

    var response =
        client.updateMfaSettings(PROJECT_DEVELOPER_ROLE, PROJECT_DEVELOPER_USER_ID, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.isSuccess()).isTrue();
    assertThat(body.isSoftwareTokenMfaEnabled()).isFalse();
    assertThat(body.isEmailMfaEnabled()).isTrue();

    var dtoCaptor = ArgumentCaptor.forClass(MfaSettingsDTO.class);
    verify(cognitoClient).setUserMfaPreference(any(), dtoCaptor.capture());

    var sent = dtoCaptor.getValue();
    assertThat(sent.isSoftwareTokenMfaEnabled()).isFalse();
    assertThat(sent.isEmailMfaEnabled()).isTrue();
  }

  @Test
  void setupSoftwareTokenReturnsSecretAndOtpAuthUrl() {
    when(cognitoClient.associateSoftwareToken(any()))
        .thenReturn(
            AssociateSoftwareTokenResponse.builder()
                .secretCode("ABC123DEF456")
                .session("session-xyz")
                .build());

    var response = client.setupSoftwareToken(PROJECT_DEVELOPER_ROLE, PROJECT_DEVELOPER_USER_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    SoftwareTokenSetupResponseDTO body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getSecretCode()).isEqualTo("ABC123DEF456");
    assertThat(body.getOtpAuthUrl()).contains("otpauth://totp/GoldStandardAssurance:");
    assertThat(body.getOtpAuthUrl()).contains("secret=ABC123DEF456");
  }

  @Test
  void verifySoftwareTokenSuccessEnablesSoftwareTokenMfa() {
    when(cognitoClient.verifySoftwareToken(any(), eq("123456"), eq(true)))
        .thenReturn(
            VerifySoftwareTokenResponse.builder()
                .status(VerifySoftwareTokenResponseType.SUCCESS)
                .build());

    doNothing().when(cognitoClient).setUserMfaPreference(any(), any(MfaSettingsDTO.class));

    var request =
        VerifySoftwareTokenRequestDTO.builder().code("123456").emailMfaEnabled(false).build();
    var response =
        client.verifySoftwareToken(PROJECT_DEVELOPER_ROLE, PROJECT_DEVELOPER_USER_ID, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    VerifySoftwareTokenResponseDTO body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.isVerified()).isTrue();

    verify(cognitoClient).setUserMfaPreference(any(), any(MfaSettingsDTO.class));
  }

  @Test
  void verifySoftwareTokenFailDoesNotEnableMfa() {
    when(cognitoClient.verifySoftwareToken(any(), eq("000000"), eq(true)))
        .thenReturn(
            VerifySoftwareTokenResponse.builder()
                .status(VerifySoftwareTokenResponseType.ERROR)
                .build());

    var request =
        VerifySoftwareTokenRequestDTO.builder().code("000000").emailMfaEnabled(false).build();
    var response =
        client.verifySoftwareToken(PROJECT_DEVELOPER_ROLE, PROJECT_DEVELOPER_USER_ID, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.isVerified()).isFalse();
  }
}

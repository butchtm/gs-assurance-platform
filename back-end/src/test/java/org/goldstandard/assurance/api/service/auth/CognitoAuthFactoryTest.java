package org.goldstandard.assurance.api.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType.EMAIL_OTP;
import static software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType.SELECT_MFA_TYPE;
import static software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType.SOFTWARE_TOKEN_MFA;

import java.util.Collections;
import org.goldstandard.assurance.api.dto.auth.MfaChallengeResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;

@ExtendWith(MockitoExtension.class)
class CognitoAuthFactoryTest {
  private CognitoAuthFactory factory;

  private static final String CLIENT_ID = "clientId";
  private static final String SESSION = "cognito session";
  private static final String USERNAME = "user1";

  @BeforeEach
  void setup() {
    factory = new CognitoAuthFactory();
  }

  @Test
  void shouldIncludeAvailableChallengesWhenAuthChallengeIsSelectMfaType() {
    var availableChallenges = "[\"EMAIL_OTP\",\"SOFTWARE_TOKEN_MFA\"]";
    var challengeParameters = Collections.singletonMap("MFAS_CAN_CHOOSE", availableChallenges);
    var authResponse =
        InitiateAuthResponse.builder()
            .challengeName(SELECT_MFA_TYPE)
            .session(SESSION)
            .challengeParameters(challengeParameters)
            .build();

    var result = factory.createAuthResultFromInitiateAuth(authResponse);

    assertThat(result.getAvailableChallengeTypes())
        .contains(EMAIL_OTP.name(), SOFTWARE_TOKEN_MFA.name());
    assertThat(result.isMfaChallengeRequired()).isTrue();
    assertThat(result.getSession()).isEqualTo(SESSION);
    assertThat(result.isSuccessful()).isFalse();
  }

  @Test
  void shouldBuildAnswerToSelectMfaTypeChallengeWithChallengeTypeSelection() {
    var selectedChallenge = EMAIL_OTP.name();
    var dto =
        MfaChallengeResponseDTO.builder()
            .username(USERNAME)
            .challengeType(SELECT_MFA_TYPE.name())
            .session(SESSION)
            .challengeSelection(selectedChallenge)
            .build();

    var result = factory.createMfaChallengeRequest(CLIENT_ID, dto);

    assertThat(result.clientId()).isEqualTo(CLIENT_ID);
    assertThat(result.session()).isEqualTo(SESSION);
    assertThat(result.challengeResponses().get("ANSWER")).isEqualTo(selectedChallenge);
    assertThat(result.challengeResponses().get("USERNAME")).isEqualTo(USERNAME);
  }
}

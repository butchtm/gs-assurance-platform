package org.goldstandard.assurance.api.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.fixtures.ZonedDateTimeMother;
import org.goldstandard.assurance.api.repository.ConversationMessageRepository;
import org.goldstandard.assurance.api.repository.ConversationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConversationMessageNotificationPolicyTest {

  private static final int CONVERSATION_ID = 1;

  @InjectMocks ConversationMessageNotificationPolicy policy;

  @Mock ConversationRepository conversationRepository;
  @Mock ConversationMessageRepository messageRepository;

  @Test
  void shouldReturnNotFirstMessageAfterOpenIfStatusAppliedDateIsNotPopulated() {
    when(conversationRepository.findStatusAppliedAtById(CONVERSATION_ID))
        .thenReturn(Optional.empty());

    var isFirstAfterOpen = policy.isFirstMessageAfterOpened(CONVERSATION_ID);

    assertThat(isFirstAfterOpen).isFalse();
  }

  @ParameterizedTest
  @MethodSource("numberOfMessagesAndExpectedFirstMessageFlag")
  void shouldReturnNotFirstMessageAfterOpenIfMessagesHaveBeenSentSinceStatusUpdateWasApplied(
      int messagesSinceStatusApplied, boolean expectedIsFirst) {
    ZonedDateTime statusAppliedAt = ZonedDateTimeMother.fixed();
    when(conversationRepository.findStatusAppliedAtById(CONVERSATION_ID))
        .thenReturn(Optional.of(statusAppliedAt));
    when(messageRepository.countByConversationIdAndSentAtAfter(CONVERSATION_ID, statusAppliedAt))
        .thenReturn(messagesSinceStatusApplied);

    var isFirst = policy.isFirstMessageAfterOpened(CONVERSATION_ID);

    assertThat(isFirst).isEqualTo(expectedIsFirst);
  }

  private static Stream<Arguments> numberOfMessagesAndExpectedFirstMessageFlag() {
    return Stream.of(Arguments.of(0, true), Arguments.of(1, true), Arguments.of(2, false));
  }
}

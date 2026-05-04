package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.goldstandard.assurance.api.entity.Conversation;
import org.goldstandard.assurance.api.entity.ConversationStatus;
import org.goldstandard.assurance.api.service.conversation.ConversationFinder;
import org.goldstandard.assurance.api.service.conversation.ConversationTransaction;
import org.goldstandard.assurance.api.service.conversation.ConversationUpdater;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConversationUpdaterTest {

  @InjectMocks ConversationUpdater updater;

  @Mock ConversationFinder finder;

  @Mock ConversationTransaction transaction;

  @Mock Clock clock;

  @Captor ArgumentCaptor<Conversation> captor;

  @Test
  void updateConversationStatus() {
    var now = mockTimeNow();
    var conversationId = 1;
    var original = givenConversationWithStatus(conversationId, ConversationStatus.buildClosed());
    when(finder.forceFindById(conversationId)).thenReturn(original);
    var openStatus = ConversationStatus.buildOpen();

    updater.updateConversationStatus(conversationId, openStatus);

    verify(transaction).update(captor.capture());
    var savedConversation = captor.getValue();
    assertThat(savedConversation)
        .usingRecursiveComparison()
        .ignoringFields("status", "statusAppliedAt")
        .isEqualTo(original);
    assertThat(savedConversation.getStatus()).isEqualTo(openStatus);
    assertThat(savedConversation.getStatusAppliedAt()).isEqualTo(now);
  }

  private ZonedDateTime mockTimeNow() {
    var now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
    return now;
  }

  private Conversation givenConversationWithStatus(
      Integer conversationId, ConversationStatus status) {
    return Conversation.builder().id(conversationId).requestId(101).status(status).build();
  }
}

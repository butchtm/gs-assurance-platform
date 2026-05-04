package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.ConversationStatus.CONVERSATION_CLOSED_STATUS_ID;
import static org.goldstandard.assurance.api.entity.ConversationStatus.CONVERSATION_OPEN_STATUS_ID;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.entity.Conversation;
import org.goldstandard.assurance.api.entity.ConversationStatus;
import org.goldstandard.assurance.api.repository.ConversationRepository;
import org.goldstandard.assurance.api.service.conversation.ConversationTransaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConversationTransactionTest {

  @InjectMocks ConversationTransaction transaction;

  @Mock ConversationRepository repository;

  @Captor ArgumentCaptor<Conversation> captor;

  @Test
  void shouldCreateConversation() {
    var conversation =
        Conversation.builder()
            .requestId(1)
            .status(new ConversationStatus(CONVERSATION_OPEN_STATUS_ID))
            .build();
    var savedConversation = Conversation.builder().id(10).build();
    when(repository.save(conversation)).thenReturn(savedConversation);

    var conversationId = transaction.create(conversation);

    assertThat(conversationId).isEqualTo(savedConversation.getId());
  }

  @Test
  void shouldUpdateConversation() {
    var updatedConversation =
        Conversation.builder()
            .id(1)
            .requestId(1)
            .status(new ConversationStatus(CONVERSATION_CLOSED_STATUS_ID))
            .build();

    transaction.update(updatedConversation);

    verify(repository).save(captor.capture());
    assertThat(captor.getValue()).isEqualTo(updatedConversation);
  }
}

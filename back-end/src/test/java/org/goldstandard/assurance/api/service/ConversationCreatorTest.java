package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.ConversationStatus.CONVERSATION_OPEN_STATUS_ID;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.entity.Conversation;
import org.goldstandard.assurance.api.entity.ConversationStatus;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.service.conversation.ConversationCreator;
import org.goldstandard.assurance.api.service.conversation.ConversationFactory;
import org.goldstandard.assurance.api.service.conversation.ConversationTransaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConversationCreatorTest {

  @InjectMocks ConversationCreator creator;

  @Mock ConversationFactory factory;

  @Mock ConversationTransaction transaction;

  @Test
  void createConversation() {
    var request = Request.builder().id(101).build();
    var conversation =
        Conversation.builder()
            .requestId(request.getId())
            .status(new ConversationStatus(CONVERSATION_OPEN_STATUS_ID))
            .build();
    when(factory.buildConversation(request)).thenReturn(conversation);
    var expectedConversationId = 1;
    when(transaction.create(conversation)).thenReturn(expectedConversationId);

    var conversationId = creator.create(request);

    assertThat(conversationId).isEqualTo(expectedConversationId);
  }
}

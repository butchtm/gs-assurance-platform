package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.goldstandard.assurance.api.entity.Conversation;
import org.goldstandard.assurance.api.exception.ConflictException;
import org.goldstandard.assurance.api.service.conversation.ConversationFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConversationValidatorTest {

  private static final int CONVERSATION_ID = 1;

  @InjectMocks ConversationValidator validator;

  @Mock ConversationFinder finder;

  @Mock Conversation conversation;

  @BeforeEach
  void setUp() {
    when(finder.forceFindById(CONVERSATION_ID)).thenReturn(conversation);
  }

  @Test
  void shouldDoNothingIfConversationIsNotClosed() {
    when(conversation.hasClosedStatus()).thenReturn(false);

    ThrowingCallable call = () -> validator.validateCanSendMessage(CONVERSATION_ID);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldThrowExceptionIfConversationIsClosed() {
    when(conversation.hasClosedStatus()).thenReturn(true);

    var error = catchThrowable(() -> validator.validateCanSendMessage(CONVERSATION_ID));

    assertThat(error)
        .isInstanceOf(ConflictException.class)
        .hasMessage("message cannot be sent because conversation %d is closed", CONVERSATION_ID);
  }
}

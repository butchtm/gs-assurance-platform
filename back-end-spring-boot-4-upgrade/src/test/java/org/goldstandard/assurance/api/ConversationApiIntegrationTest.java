package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.collections4.IterableUtils;
import org.goldstandard.assurance.api.dto.ConversationMessageDTO;
import org.goldstandard.assurance.api.dto.MessageCreateDTO;
import org.goldstandard.assurance.api.entity.Conversation;
import org.goldstandard.assurance.api.entity.ConversationMessage;
import org.goldstandard.assurance.api.entity.ConversationMessageReceipt;
import org.goldstandard.assurance.api.entity.ConversationStatus;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.notification.Notification;
import org.goldstandard.assurance.api.repository.ConversationMessageRepository;
import org.goldstandard.assurance.api.repository.ConversationRepository;
import org.goldstandard.assurance.api.repository.UserRepository;
import org.goldstandard.assurance.api.repository.notification.NotificationRepository;
import org.goldstandard.assurance.api.service.notification.RenderedMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.support.TransactionTemplate;

class ConversationApiIntegrationTest extends AbstractApiTest {

  private static final int PROJECT_ID = 1;
  private static final int REQUEST_ID = 2;

  @Autowired ApiClient client;

  @Autowired ConversationRepository conversationRepository;
  @Autowired ConversationMessageRepository messageRepository;
  @Autowired UserRepository userRepository;
  @Autowired NotificationRepository notificationRepository;

  @Autowired TransactionTemplate transactionTemplate;

  @Test
  void shouldOpenConversation() {
    var response = client.openConversation(PROJECT_ID, REQUEST_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var body = response.getBody();
    assertThat(body).isNotNull();
    try {
      var conversation = conversationRepository.findByRequestId(REQUEST_ID).orElseThrow();
      assertThat(conversation.getId()).isPositive();
      assertThat(conversation.getRequestId()).isEqualTo(REQUEST_ID);
      assertThat(conversation.hasOpenStatus()).isTrue();
    } finally {
      deleteConversationAndMessages(response.getBody().getId());
    }
  }

  @Test
  void shouldUpdateConversationStatus() {
    var originalConversation =
        Conversation.builder().requestId(REQUEST_ID).status(ConversationStatus.buildOpen()).build();
    var savedConversation = conversationRepository.save(originalConversation);

    try {
      var response =
          client.updateConversation(
              PROJECT_ID, REQUEST_ID, savedConversation.getId(), ConversationStatus.buildClosed());

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      var body = response.getBody();
      assertThat(body).isNotNull();
      var updatedConversation = conversationRepository.findByRequestId(REQUEST_ID).orElseThrow();
      assertThat(updatedConversation)
          .usingRecursiveComparison()
          .ignoringFields("status", "statusAppliedAt")
          .isEqualTo(originalConversation);
      assertThat(updatedConversation.hasClosedStatus()).isTrue();
      assertThat(updatedConversation.getStatusAppliedAt()).isNotNull();
    } finally {
      deleteConversationAndMessages(savedConversation.getId());
    }
  }

  @Test
  void shouldSendMessageToConversation() {
    var originalConversation =
        Conversation.builder().requestId(REQUEST_ID).status(ConversationStatus.buildOpen()).build();
    var savedConversation = conversationRepository.save(originalConversation);
    var message = new MessageCreateDTO("Hello, this is a test message 1");
    try {
      var response = client.sendMessage(PROJECT_ID, REQUEST_ID, savedConversation.getId(), message);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      var body = response.getBody();
      assertThat(body).isNotNull();
      var messages = messageRepository.findAllByConversationId(savedConversation.getId());
      assertThat(messages).hasSize(1);
      var savedMessage = IterableUtils.get(messages, 0);
      assertThat(savedMessage.getId()).isPositive();
      assertThat(savedMessage.getConversationId()).isEqualTo(savedConversation.getId());
      assertThat(savedMessage.getSentByUser().getUserName()).isEqualTo("test-arm");
      assertThat(savedMessage.getSentAt()).isNotNull();
      assertThat(savedMessage.getReceipts()).isEmpty();
      assertThat(savedMessage.getContent()).isEqualTo(message.getMessage());
    } finally {
      deleteConversationAndMessages(savedConversation.getId());
    }
  }

  @Test
  void shouldReturnConversationMessages() {
    var originalConversation =
        Conversation.builder().requestId(REQUEST_ID).status(ConversationStatus.buildOpen()).build();
    var savedConversation = conversationRepository.save(originalConversation);
    var message1 = buildMessage(savedConversation.getId(), 1);
    var message2 = buildMessage(savedConversation.getId(), 2);
    messageRepository.saveAll(List.of(message1, message2));
    try {
      var response = client.getMessages(PROJECT_ID, REQUEST_ID, savedConversation.getId());

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      var body = response.getBody();
      assertThat(body).isNotNull();
      var content = body.getContent();
      assertThat(content).hasSize(2);
      assertThat(content)
          .map(ConversationMessageDTO::getContent)
          .containsExactlyInAnyOrder(message1.getContent(), message2.getContent());
      assertThat(content)
          .map(ConversationMessageDTO::getSenderUsername)
          .containsExactlyInAnyOrder(
              message1.getSentByUser().getUserName(), message2.getSentByUser().getUserName());
    } finally {
      deleteConversationAndMessages(savedConversation.getId());
    }
  }

  @Test
  void shouldMarkConversationMessageAsRead() {
    var originalConversation =
        Conversation.builder().requestId(REQUEST_ID).status(ConversationStatus.buildOpen()).build();
    var savedConversation = conversationRepository.save(originalConversation);
    var message1 = buildMessage(savedConversation.getId(), 1);
    var savedMessage = messageRepository.save(message1);
    messageRepository.flush();
    try {
      var response = client.markMessagesAsRead(PROJECT_ID, REQUEST_ID, savedConversation.getId());

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      assertThat(response.getBody()).isNull();
      var updatedMessage = messageRepository.findById(savedMessage.getId()).orElseThrow();
      assertThat(updatedMessage.getReceipts())
          .map(ConversationMessageReceipt::getReadByUser)
          .map(User::getUserName)
          .containsExactly("test-arm");
    } finally {
      deleteConversationAndMessages(savedConversation.getId());
    }
  }

  @Test
  void shouldSendEmailNotificationToProjectDevelopersAndVVBsWhenFirstMessageSent() {
    when(emailSender.sendEmailAsync(anyString(), any(RenderedMessage.class)))
        .thenReturn(CompletableFuture.completedFuture("mocked-message-reference"));
    var originalConversation =
        Conversation.builder()
            .requestId(REQUEST_ID)
            .status(ConversationStatus.buildOpen())
            .statusAppliedAt(ZonedDateTime.now().minusHours(1))
            .build();
    var savedConversation = conversationRepository.save(originalConversation);
    var message = new MessageCreateDTO("Hello, this is a test message 2");
    try {
      var response = client.sendMessage(PROJECT_ID, REQUEST_ID, savedConversation.getId(), message);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      var sort = Sort.by("createdTimestamp").descending();
      var notifications = notificationRepository.findAll(PageRequest.of(0, 2, sort));
      assertThat(notifications).hasSize(2);
      assertThat(notifications)
          .map(Notification::getSubject)
          .containsExactly("New message received for request", "New message received for request");
    } finally {
      deleteConversationAndMessages(savedConversation.getId());
    }
  }

  private ConversationMessage buildMessage(int conversationId, int index) {
    return ConversationMessage.builder()
        .content(String.format("message %d", index))
        .sentAt(ZonedDateTime.now())
        .sentByUser(userRepository.findById(index).orElseThrow())
        .receipts(Collections.emptyList())
        .conversationId(conversationId)
        .build();
  }

  public void deleteConversationAndMessages(Integer conversationId) {
    transactionTemplate.executeWithoutResult(
        status -> {
          messageRepository.deleteAllByConversationId(conversationId);
          conversationRepository.deleteById(conversationId);
        });
  }
}

package org.goldstandard.assurance.api.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.notification.DocumentPiiNotificationService.TEMPLATE_KEY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.pii.PiiMetadata;
import org.goldstandard.assurance.api.service.AssignmentFinder;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.goldstandard.assurance.api.service.document.DocumentFinder;
import org.goldstandard.assurance.api.service.notification.model.NotificationModel;
import org.goldstandard.assurance.api.service.pii.PiiMetadataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentPiiNotificationServiceTest {

  @InjectMocks DocumentPiiNotificationService service;

  @Mock NotificationService notificationService;
  @Mock NotificationModelFactory modelFactory;
  @Mock PiiMetadataService metadataService;
  @Mock AssignmentFinder assignmentFinder;
  @Mock AccountFinder accountFinder;
  @Mock DocumentFinder documentFinder;
  @Captor ArgumentCaptor<List<Document>> documentsCaptor;
  @Captor ArgumentCaptor<NotificationModel> modelCaptor;

  @Test
  void shouldSendDocumentPiiNotification_WithNoAuditAccounts() {
    var request = buildRequest();
    var document = buildDocument();
    var piiMetadata = buildPiiMetadata(document.getId());
    when(metadataService.findNonNotifiedDocumentMetadata(request.getId()))
        .thenReturn(List.of(piiMetadata));
    when(assignmentFinder.findAssignedAuditAccountsByRequestId(request.getId()))
        .thenReturn(Collections.emptyList());
    var pd = mock(Account.class);
    when(accountFinder.getAccountById(request.getProject().getAccountId()))
        .thenReturn(Optional.of(pd));
    when(documentFinder.getDocuments(anyList())).thenReturn(List.of(document));
    var model = mock(NotificationModel.class);
    when(modelFactory.toNotificationModel(anyList(), eq(request))).thenReturn(model);

    service.sendDocumentPiiNotification(request);

    verify(notificationService).sendAccountNotifications(TEMPLATE_KEY, model, List.of(pd));
    verify(metadataService, times(1)).markDocumentAsNotified(piiMetadata);
  }

  @Test
  void shouldSendDocumentPiiNotification_WithAuditAccounts() {
    var auditAccount = buildAccount();
    var request = buildRequest();
    var document = buildDocument();
    var piiMetadata = buildPiiMetadata(document.getId());
    when(metadataService.findNonNotifiedDocumentMetadata(request.getId()))
        .thenReturn(List.of(piiMetadata));
    when(assignmentFinder.findAssignedAuditAccountsByRequestId(request.getId()))
        .thenReturn(List.of(auditAccount));
    var pd = mock(Account.class);
    when(accountFinder.getAccountById(request.getProject().getAccountId()))
        .thenReturn(Optional.of(pd));
    when(documentFinder.getDocuments(anyList())).thenReturn(List.of(document));
    var model = mock(NotificationModel.class);
    when(modelFactory.toNotificationModel(anyList(), eq(request))).thenReturn(model);

    service.sendDocumentPiiNotification(request);

    verify(notificationService)
        .sendAccountNotifications(TEMPLATE_KEY, model, List.of(auditAccount, pd));
    verify(metadataService, times(1)).markDocumentAsNotified(piiMetadata);
  }

  @Test
  void shouldSendDocumentPiiNotification_WithMultipleDocuments() {
    var request = buildRequest();
    var document = buildDocument();
    var piiMetadata = buildPiiMetadata(document.getId());
    when(metadataService.findNonNotifiedDocumentMetadata(request.getId()))
        .thenReturn(List.of(piiMetadata, piiMetadata));
    when(assignmentFinder.findAssignedAuditAccountsByRequestId(request.getId()))
        .thenReturn(Collections.emptyList());
    var pd = mock(Account.class);
    when(accountFinder.getAccountById(request.getProject().getAccountId()))
        .thenReturn(Optional.of(pd));
    when(documentFinder.getDocuments(anyList())).thenReturn(List.of(document, document));
    var model = mock(NotificationModel.class);
    when(modelFactory.toNotificationModel(anyList(), eq(request))).thenReturn(model);

    service.sendDocumentPiiNotification(request);

    verify(notificationService)
        .sendAccountNotifications(eq(TEMPLATE_KEY), modelCaptor.capture(), eq(List.of(pd)));

    verify(modelFactory).toNotificationModel(documentsCaptor.capture(), eq(request));
    assertThat(documentsCaptor.getValue()).hasSize(2);
    verify(metadataService, times(2)).markDocumentAsNotified(piiMetadata);
  }

  @Test
  void shouldNotSendDocumentPiiNotification_NoPiiMetadata() {
    var request = buildRequest();
    when(metadataService.findNonNotifiedDocumentMetadata(request.getId()))
        .thenReturn(Collections.emptyList());

    service.sendDocumentPiiNotification(request);

    verifyNoInteractions(notificationService);
    verify(metadataService, times(0)).markDocumentAsNotified(any());
  }

  private static Request buildRequest() {
    return Request.builder().id(1).project(buildProject()).build();
  }

  private static Project buildProject() {
    return Project.builder().id(0).gsid("gsid").accountId(1).build();
  }

  private static Document buildDocument() {
    return Document.builder().id(UUID.randomUUID()).fileName("document name").build();
  }

  private static PiiMetadata buildPiiMetadata(UUID documentId) {
    return PiiMetadata.builder().id(1).documentId(documentId).build();
  }

  private static Account buildAccount() {
    return Account.builder().id(1).build();
  }
}

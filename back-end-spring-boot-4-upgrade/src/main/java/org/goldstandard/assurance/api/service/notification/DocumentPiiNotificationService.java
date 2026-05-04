package org.goldstandard.assurance.api.service.notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.pii.PiiMetadata;
import org.goldstandard.assurance.api.service.AssignmentFinder;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.goldstandard.assurance.api.service.document.DocumentFinder;
import org.goldstandard.assurance.api.service.pii.PiiMetadataService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentPiiNotificationService {

  public static final TemplateKey TEMPLATE_KEY = new TemplateKey("DOCUMENT_PII_FOUND", "v0.0.3");

  private final NotificationService notificationService;
  private final NotificationModelFactory modelFactory;
  private final PiiMetadataService metadataService;
  private final AssignmentFinder assignmentFinder;
  private final AccountFinder accountFinder;
  private final DocumentFinder documentFinder;

  public void sendDocumentPiiNotification(Request request) {
    var documentPiiMetadata = metadataService.findNonNotifiedDocumentMetadata(request.getId());
    if (documentPiiMetadata.isEmpty()) {
      return;
    }
    var model =
        modelFactory.toNotificationModel(
            extractListOfNonActionedDocuments(documentPiiMetadata), request);
    var accounts = getAssociatedAccounts(request);
    notificationService.sendAccountNotifications(TEMPLATE_KEY, model, accounts);
    documentPiiMetadata.forEach(metadataService::markDocumentAsNotified);
  }

  private Collection<Account> getAssociatedAccounts(Request request) {
    List<Account> accounts =
        new ArrayList<>(assignmentFinder.findAssignedAuditAccountsByRequestId(request.getId()));
    accountFinder.getAccountById(request.getProject().getAccountId()).ifPresent(accounts::add);
    return accounts;
  }

  private List<Document> extractListOfNonActionedDocuments(List<PiiMetadata> documentPiiMetadata) {
    var ids = documentPiiMetadata.stream().map(PiiMetadata::getDocumentId).toList();
    return documentFinder.getDocuments(ids);
  }
}

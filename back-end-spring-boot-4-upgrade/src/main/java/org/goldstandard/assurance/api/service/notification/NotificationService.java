package org.goldstandard.assurance.api.service.notification;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Ngo;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.notification.Notification;
import org.goldstandard.assurance.api.entity.notification.NotificationTemplate;
import org.goldstandard.assurance.api.repository.notification.NotificationRepository;
import org.goldstandard.assurance.api.service.notification.model.NotificationModel;
import org.goldstandard.assurance.api.service.notification.model.NotificationParams;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {
  private final ContactMappingService contactMappingService;
  private final TemplateService templateService;
  private final EmailNotificationSender notificationSender;
  private final NotificationModelFactory modelFactory;
  private final NotificationRepository notificationRepository;
  private final AuthenticatedUserSupplier userSupplier;

  public Page<Notification> getNotifications(Pageable pageable) {
    return notificationRepository.findAll(pageable);
  }

  public void sendAccountNotifications(
      TemplateKey key, Request request, Collection<Account> accounts) {
    var template = templateService.forceGetTemplate(key);
    var model = modelFactory.toNotificationModel(request);
    var recipients = accounts.stream().map(NotificationRecipientAccount::new).toList();
    sendNotifications(template, model, recipients);
  }

  public void sendAccountNotification(
      TemplateKey key, NotificationModel model, Account account, User authenticatedUser) {
    var template = templateService.forceGetTemplate(key);
    var recipient = new NotificationRecipientAccount(account);
    sendNotification(template, model, recipient, authenticatedUser);
  }

  public void sendAccountNotifications(
      TemplateKey key, NotificationModel model, Collection<Account> accounts) {
    sendAccountNotifications(
        key, model, accounts, userSupplier.getAuthenticatedUser().orElse(null));
  }

  public void sendAccountNotifications(
      TemplateKey key,
      NotificationModel model,
      Collection<Account> accounts,
      User authenticatedUser) {
    var template = templateService.forceGetTemplate(key);
    var recipients = accounts.stream().map(NotificationRecipientAccount::new).toList();
    sendNotifications(template, model, recipients, authenticatedUser);
  }

  public void sendNgoNotifications(
      TemplateKey key, NotificationModel model, Collection<Ngo> ngos, User authenticatedUser) {
    var template = templateService.forceGetTemplate(key);
    var recipients = ngos.stream().map(NotificationRecipientNgo::new).toList();
    sendNotifications(template, model, recipients, authenticatedUser);
  }

  public <T extends NotificationRecipient> void sendNotifications(
      NotificationTemplate template, NotificationModel model, Collection<T> recipients) {
    sendNotifications(
        template, model, recipients, userSupplier.getAuthenticatedUser().orElse(null));
  }

  public void sendUserNotification(
      TemplateKey key, NotificationModel model, User recipientUser, User authenticatedUser) {
    var template = templateService.forceGetTemplate(key);
    var recipient = new NotificationRecipientUser(recipientUser);
    sendNotification(template, model, recipient, authenticatedUser);
  }

  public void sendAccountNotification(
      NotificationTemplate template, Request request, Account account) {
    var recipient = new NotificationRecipientAccount(account);
    var model = modelFactory.toNotificationModel(request);
    sendNotification(template, model, recipient);
  }

  public void sendNotification(
      NotificationTemplate template, NotificationModel model, NotificationRecipient recipient) {
    sendNotification(template, model, recipient, userSupplier.getAuthenticatedUser().orElse(null));
  }

  private <T extends NotificationRecipient> void sendNotifications(
      NotificationTemplate template,
      NotificationModel model,
      Collection<T> recipients,
      User authenticatedUser) {
    recipients.forEach(
        recipient -> sendNotification(template, model, recipient, authenticatedUser));
    log.info(
        "Notification sent to recipients [{}] with requestId [{}] using template [{}].",
        toNames(recipients),
        Optional.ofNullable(model.getRequest())
            .map(NotificationModel.RequestModel::getId)
            .orElse(null),
        template.baseName());
  }

  private void sendNotification(
      NotificationTemplate template,
      NotificationModel model,
      NotificationRecipient recipient,
      User authenciatedUser) {
    var recipientAddressOverride = contactMappingService.mapContact(recipient.getEmailAddress());
    if (Objects.isNull(recipientAddressOverride)) {
      log.warn(
          "no recipient email address mapped for {}, not sending notification",
          recipient.getEmailAddress());
      return;
    }
    var params =
        NotificationParams.builder()
            .template(template)
            .model(model)
            .recipient(recipient)
            .recipientAddressOverride(recipientAddressOverride)
            .authenticatedUser(authenciatedUser)
            .build();
    notificationSender.sendNotification(params);
  }

  private static <T extends NotificationRecipient> String toNames(Collection<T> recipients) {
    return recipients.stream()
        .map(NotificationRecipient::getName)
        .collect(Collectors.joining(", "));
  }
}

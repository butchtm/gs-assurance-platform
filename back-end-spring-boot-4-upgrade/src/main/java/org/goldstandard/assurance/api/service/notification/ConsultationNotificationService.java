package org.goldstandard.assurance.api.service.notification;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.repository.NgoRepository;
import org.goldstandard.assurance.api.service.StakeholderConsultationService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultationNotificationService {

  public static final TemplateKey TEMPLATE_KEY = new TemplateKey("CONSULTATION_STARTED", "v0.0.6");
  public static final TemplateKey NGO_TEMPLATE_KEY =
      new TemplateKey("NGO_CONSULTATION_NOTIFICATION", "v0.0.3");

  private final StakeholderConsultationService consultationService;
  private final NotificationService notificationService;
  private final NotificationModelFactory modelService;
  private final AccountConsultationsGrouper accountConsultationsGrouper;
  private final NgoRepository ngoRepository;

  public void sendConsultationNotifications(User authenticatedUser) {
    var consultations = consultationService.getLiveUnnotifiedStakeholderConsultations();
    sendAccountNotifications(consultations, authenticatedUser);
    sendNgoNotifications(consultations, authenticatedUser);
    consultationService.markNotificationsSent(consultations);
  }

  private void sendAccountNotifications(
      Collection<StakeholderConsultation> consultations, User authenticatedUser) {
    var privateConsultations =
        consultations.stream().filter(StakeholderConsultation::isPrivate).toList();
    if (!privateConsultations.isEmpty()) {
      var accountConsultations =
          accountConsultationsGrouper.groupConsultationsByAccount(privateConsultations);
      sendAccountNotifications(accountConsultations, authenticatedUser);
      return;
    }
    log.info("no private consultations found so no notifications will be sent");
  }

  private void sendAccountNotifications(
      Map<Account, Collection<StakeholderConsultation>> accountConsultations,
      User authenticatedUser) {
    accountConsultations.forEach(
        (account, consultationsForAccount) -> {
          var model = modelService.toNotificationModel(consultationsForAccount);
          notificationService.sendAccountNotifications(
              TEMPLATE_KEY, model, List.of(account), authenticatedUser);
        });
  }

  private void sendNgoNotifications(
      Collection<StakeholderConsultation> consultations, User authenticatedUser) {
    var publicConsultations =
        consultations.stream().filter(StakeholderConsultation::isPublic).toList();
    if (!publicConsultations.isEmpty()) {
      var ngos = ngoRepository.findAll();
      var model = modelService.toNotificationModel(publicConsultations);
      notificationService.sendNgoNotifications(NGO_TEMPLATE_KEY, model, ngos, authenticatedUser);
      return;
    }
    log.info("no public consultations found so no NGO notifications will be sent");
  }
}

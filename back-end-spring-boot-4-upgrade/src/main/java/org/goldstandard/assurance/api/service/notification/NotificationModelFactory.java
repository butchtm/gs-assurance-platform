package org.goldstandard.assurance.api.service.notification;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.PiiAnalysisConverter;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestRound;
import org.goldstandard.assurance.api.entity.RequestStatusHistory;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.entity.view.GroupedDocumentPiiView;
import org.goldstandard.assurance.api.repository.system.RequestStatusHistoryRepository;
import org.goldstandard.assurance.api.service.StakeholderConsultationFinder;
import org.goldstandard.assurance.api.service.activity.PathwayGroupDeadlineCalculator;
import org.goldstandard.assurance.api.service.document.pii.analysis.DocumentPiiDbService;
import org.goldstandard.assurance.api.service.notification.model.NotificationModel;
import org.goldstandard.assurance.api.service.notification.model.NotificationModel.ChangeLogModel;
import org.goldstandard.assurance.api.service.notification.model.NotificationModel.ConsultationModel;
import org.goldstandard.assurance.api.service.notification.model.NotificationModel.ProjectModel;
import org.goldstandard.assurance.api.service.notification.model.NotificationModel.RequestActivityModel;
import org.goldstandard.assurance.api.service.notification.model.NotificationModel.RequestModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationModelFactory {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  @Value("${web.ui-hostname}")
  private String webUiHostname;

  private final RequestStatusHistoryRepository requestStatusHistoryRepository;
  private final StakeholderConsultationFinder consultationFinder;
  private final PathwayGroupDeadlineCalculator pathwayGroupDeadlineCalculator;
  private final DocumentPiiDbService documentPiiDbService;
  private final PiiAnalysisConverter piiConverter;

  public NotificationModel toNotificationModel(List<Document> documents, Request request) {
    return NotificationModel.builder()
        .multiDocumentPii(toDocumentPiiModelList(documents))
        .request(toRequestModel(request))
        .project(toProjectModel(request.getProject()))
        .build();
  }

  public NotificationModel toNotificationModel(Request request) {
    return toNotificationModel(
        request, consultationFinder.getPrivateConsultation(request).orElse(null));
  }

  public NotificationModel toNotificationModel(
      Request request, StakeholderConsultation consultation) {
    var consultationModel =
        Optional.ofNullable(consultation)
            .map(this::toConsultationModel)
            .orElse(toEmptyConsultationModel());
    return NotificationModel.builder()
        .project(toProjectModel(request.getProject()))
        .request(toRequestModel(request))
        .consultation(consultationModel)
        .build();
  }

  public NotificationModel toNotificationModel(Collection<StakeholderConsultation> consultations) {
    return NotificationModel.builder()
        .webUiHostname(webUiHostname)
        .consultations(consultations.stream().map(this::toDetailedConsultationModel).toList())
        .build();
  }

  public List<NotificationModel> toNotificationModelList(
      List<UnreadNotificationData> unreadNotificationData) {
    return unreadNotificationData.stream()
        .map(
            unread -> {
              var unreadModel =
                  NotificationModel.UnreadMessageModel.builder()
                      .recipient(unread.account())
                      .requests(toUnreadRequestModel(unread.requests()))
                      .build();
              return NotificationModel.builder().unread(unreadModel).build();
            })
        .toList();
  }

  private List<NotificationModel.UnreadRequestModel> toUnreadRequestModel(List<Request> requests) {
    return requests.stream()
        .map(
            request ->
                NotificationModel.UnreadRequestModel.builder()
                    .request(toRequestModel(request))
                    .project(toProjectModel(request.getProject()))
                    .build())
        .toList();
  }

  private List<NotificationModel.DocumentPiiModel> toDocumentPiiModelList(
      List<Document> documents) {
    return documents.stream()
        .map(
            doc -> {
              var pii = documentPiiDbService.getAllProbablePiiSorted(doc.getId());
              return toDocumentPiiModel(doc, pii);
            })
        .toList();
  }

  private NotificationModel.DocumentPiiModel toDocumentPiiModel(
      Document document, Collection<GroupedDocumentPiiView> documentPii) {
    return NotificationModel.DocumentPiiModel.builder()
        .documentName(document.getFileName())
        .probablePii(documentPii.stream().map(piiConverter::toPageGroupedPii).toList())
        .build();
  }

  private RequestModel toRequestModel(Request request) {
    return RequestModel.builder()
        .id(request.getId())
        .name(request.getRequestName())
        .feedbackRound(request.getCurrentRoundIndex())
        .dueDate(formatCurrentRoundEndDate(request))
        .latestRequestActivity(toActivityModel(request))
        .changeLog(toChangeLogModels(request))
        .link(toRequestLink(request))
        .cancellationReason(toCancellationReason(request))
        .build();
  }

  private String toCancellationReason(Request request) {
    return Optional.ofNullable(request.getCancellationReason())
        .map(
            reason ->
                reason.isOtherReason()
                    ? request.getOtherCancellationReason()
                    : reason.getDescription())
        .orElse("No reason provided");
  }

  private RequestActivityModel toActivityModel(Request request) {
    return request
        .getCurrentActivity()
        .map(activity -> toActivityModel(request, activity))
        .orElse(buildEmptyActivityModel());
  }

  private RequestActivityModel toActivityModel(Request request, RequestActivity activity) {
    var reviewDueDate =
        pathwayGroupDeadlineCalculator.calculateCurrentPathwayGroupEndDateForRequest(request);
    return RequestActivityModel.builder()
        .activityName(activity.getDisplayName())
        .plannedEndDate(formatDateTime(activity.getPlannedEndDate()))
        .reviewDueDate(reviewDueDate.map(d -> d.format(DATE_FORMATTER)).orElse(""))
        .build();
  }

  private List<ChangeLogModel> toChangeLogModels(Request request) {
    var history =
        requestStatusHistoryRepository.findByRequestIdOrderByChangedAtDesc(request.getId());
    return history.stream().map(NotificationModelFactory::toChangeLogModel).toList();
  }

  private static String formatCurrentRoundEndDate(Request request) {
    return request
        .findCurrentRound()
        .map(RequestRound::getEndDate)
        .map(DATE_FORMATTER::format)
        .orElse("");
  }

  private static RequestActivityModel buildEmptyActivityModel() {
    return RequestActivityModel.builder()
        .activityName("")
        .plannedEndDate("")
        .reviewDueDate("")
        .build();
  }

  private static ProjectModel toProjectModel(Project project) {
    return ProjectModel.builder()
        .name(project.getName())
        .gsid(project.getGsid())
        .projectTypes(toDescription(project.getProjectTypes()))
        .projectStatus(toDescription(project.getProjectStatus()))
        .projectSize(toDescription(project.getProjectSize()))
        .projectStream(toDescription(project.getCarbonStream()))
        .gsVersion(toDescription(project.getCurrentStandardVersion()))
        .country(toDescription(project.getProjectLocations()))
        .build();
  }

  private static <T extends ReferenceDataType> String toDescription(Collection<T> types) {
    return types.stream().map(ReferenceDataType::getDescription).collect(Collectors.joining(", "));
  }

  private ConsultationModel toConsultationModel(StakeholderConsultation consultation) {
    return ConsultationModel.builder()
        .reviewStartDate(formatDateTime(consultation.getStartDate()))
        .reviewEndDate(formatDateTime(consultation.getEndDate()))
        .build();
  }

  private ConsultationModel toDetailedConsultationModel(StakeholderConsultation consultation) {
    return ConsultationModel.builder()
        .reviewStartDate(formatDateTime(consultation.getStartDate()))
        .reviewEndDate(formatDateTime(consultation.getEndDate()))
        .request(toRequestModel(consultation.getRequest()))
        .project(NotificationModelFactory.toProjectModel(consultation.getProject()))
        .build();
  }

  private String toRequestLink(Request request) {
    var gsid = request.getProject().getGsid();
    return String.format("%s/projects/%s/requests?expand=%d", webUiHostname, gsid, request.getId());
  }

  private static ConsultationModel toEmptyConsultationModel() {
    return ConsultationModel.builder().reviewStartDate("").reviewEndDate("").build();
  }

  private static ChangeLogModel toChangeLogModel(RequestStatusHistory history) {
    return ChangeLogModel.builder()
        .changedByAccount(toUserAccountName(history.getUpdatedByUser()))
        .changedByUser(formatName(history.getUpdatedByUser()))
        .previousState(toDescription(history.getPreviousStatus()))
        .nextState(toDescription(history.getNextStatus()))
        .changedAt(formatDateTime(history.getChangedAt()))
        .build();
  }

  private static String toUserAccountName(User user) {
    return Optional.ofNullable(user).map(User::getAccount).map(Account::getName).orElse("");
  }

  private static String formatName(User user) {
    if (Objects.isNull(user)) {
      return "";
    }
    return String.format("%s %s", user.getGivenName(), user.getFamilyName());
  }

  private static String formatDateTime(ZonedDateTime dateTime) {
    return Optional.ofNullable(dateTime).map(d -> d.format(DATE_FORMATTER)).orElse("");
  }

  private static String toDescription(ReferenceDataType type) {
    if (Objects.isNull(type)) {
      return "";
    }
    return type.getDescription();
  }
}

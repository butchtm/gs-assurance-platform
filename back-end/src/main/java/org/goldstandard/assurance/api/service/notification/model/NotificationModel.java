package org.goldstandard.assurance.api.service.notification.model;

import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.service.document.pii.analysis.PageGroupedPii;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class NotificationModel {
  @With private final String recipient;
  @With private final ProjectModel project;
  @With private final RequestModel request;
  @With private final ConsultationModel consultation;
  @With private final Collection<ConsultationModel> consultations;
  @With private final DownloadModel download;
  @With private final Collection<DocumentPiiModel> multiDocumentPii;
  @With private final String webUiHostname;
  @With private final UnreadMessageModel unread;

  @Getter
  @Builder
  public static final class ProjectModel {
    private final String name;
    private final String gsid;
    private final String projectTypes;
    private final String projectStatus;
    private final String projectSize;
    private final String projectStream;
    private final String gsVersion;
    private final String country;
  }

  @Getter
  @Builder
  public static class RequestModel {
    private final Integer id;
    private final String name;
    private final Integer feedbackRound;
    private final String dueDate;
    private final List<ChangeLogModel> changeLog;
    private final RequestActivityModel latestRequestActivity;
    private final String link;
    private final String cancellationReason;
  }

  @Getter
  @Builder
  public static class RequestActivityModel {
    private final String activityName;
    private final String plannedEndDate;
    private final String reviewDueDate;
  }

  @Getter
  @Builder
  public static class ConsultationModel {
    private final String reviewStartDate;
    private final String reviewEndDate;
    private final RequestModel request;
    private final ProjectModel project;
  }

  @Getter
  @Builder
  public static class ChangeLogModel {
    private final String changedByAccount;
    private final String changedByUser;
    private final String changedAt;
    private final String previousState;
    private final String nextState;
  }

  @Getter
  @Builder
  public static class DownloadModel {
    private final String presignedUrl;
    private final String key;
    private final String checksum;
    private final String expiryDate;
    private final String expiryTime;
  }

  @Getter
  @Builder
  public static class DocumentPiiModel {
    private final String documentName;
    private final Collection<PageGroupedPii> probablePii;
  }

  @Getter
  @Builder
  public static class UnreadMessageModel {
    private final Account recipient;
    private final List<UnreadRequestModel> requests;
  }

  @Getter
  @Builder
  public static class UnreadRequestModel {
    private final RequestModel request;
    private final ProjectModel project;
  }
}

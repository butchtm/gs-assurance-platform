package org.goldstandard.assurance.api.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.ConsultationType.TAC_CONSULTATION;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.ConsultationType;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestCancellationReason;
import org.goldstandard.assurance.api.entity.RequestRound;
import org.goldstandard.assurance.api.entity.RequestStatusHistory;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.fixtures.ZonedDateTimeMother;
import org.goldstandard.assurance.api.repository.system.RequestStatusHistoryRepository;
import org.goldstandard.assurance.api.service.StakeholderConsultationFinder;
import org.goldstandard.assurance.api.service.activity.PathwayGroupDeadlineCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationModelFactoryTest {

  private static final ZonedDateTime DATE = ZonedDateTimeMother.fixed();

  @InjectMocks NotificationModelFactory notificationModelFactory;

  @Mock RequestStatusHistoryRepository requestStatusHistoryRepository;

  @Mock StakeholderConsultationFinder stakeholderConsultationFinder;
  @Mock PathwayGroupDeadlineCalculator pathwayGroupDeadlineCalculator;

  @Test
  void shouldPopulateCommonDetails() {
    var request = buildRequest();

    var model = notificationModelFactory.toNotificationModel(request);

    assertThat(model.getRecipient()).isNull();
    var modelRequest = model.getRequest();
    assertThat(modelRequest.getName()).isEqualTo(request.getRequestName());
    assertThat(modelRequest.getFeedbackRound()).isZero();
    assertThat(modelRequest.getDueDate()).isEmpty();
    assertThat(modelRequest.getChangeLog()).isEmpty();
    var project = request.getProject();
    var modelProject = model.getProject();
    assertThat(modelProject.getName()).isEqualTo(project.getName());
    assertThat(modelProject.getGsid()).isEqualTo(project.getGsid());
    var status = project.getProjectStatus();
    assertThat(modelProject.getProjectStatus()).isEqualTo(status.getDescription());
    assertThat(modelProject.getProjectTypes()).isEqualTo(toString(project.getProjectTypes()));
    var modelRequestActivity = modelRequest.getLatestRequestActivity();
    assertThat(modelRequestActivity.getActivityName()).isEmpty();
    assertThat(modelRequestActivity.getPlannedEndDate()).isEmpty();
    assertThat(modelRequestActivity.getReviewDueDate()).isEmpty();
    var modelConsultation = model.getConsultation();
    assertThat(modelConsultation.getReviewStartDate()).isEmpty();
    assertThat(modelConsultation.getReviewEndDate()).isEmpty();
  }

  @Test
  void shouldPopulateRoundOfReviewDetailsIfPresent() {
    var request = buildRequest();
    var roundIndex = 1;
    request.setCurrentRoundIndex(roundIndex);
    request.setRequestRounds(buildRequestRounds(roundIndex));

    var model = notificationModelFactory.toNotificationModel(request);

    var modelRequest = model.getRequest();
    assertThat(modelRequest.getFeedbackRound()).isOne();
    assertThat(modelRequest.getDueDate())
        .isEqualTo(format(request.findCurrentRound().orElseThrow().getEndDate()));
  }

  @Test
  void shouldPopulateCurrentActivityDetailsIfPresent() {
    var request = buildRequest();
    request.setCurrentActivity(buildRequestActivity());

    var expectedReviewDate = DATE.plusDays(10);
    when(pathwayGroupDeadlineCalculator.calculateCurrentPathwayGroupEndDateForRequest(request))
        .thenReturn(Optional.of(expectedReviewDate));

    var model = notificationModelFactory.toNotificationModel(request);

    var modelRequest = model.getRequest();
    var requestActivity = request.getCurrentActivity().orElseThrow();
    var modelRequestActivity = modelRequest.getLatestRequestActivity();
    assertThat(modelRequestActivity.getActivityName()).isEqualTo(requestActivity.getDisplayName());
    assertThat(modelRequestActivity.getPlannedEndDate())
        .isEqualTo(format(requestActivity.getPlannedEndDate()));
    assertThat(modelRequestActivity.getReviewDueDate()).isEqualTo(format(expectedReviewDate));
  }

  @Test
  void shouldPopulateChangeLogDetailsIfPresent() {
    var request = buildRequest();
    var history = buildRequestStatusHistory();
    when(requestStatusHistoryRepository.findByRequestIdOrderByChangedAtDesc(request.getId()))
        .thenReturn(List.of(history));

    var model = notificationModelFactory.toNotificationModel(request);

    var changeLog = model.getRequest().getChangeLog();
    assertThat(changeLog).hasSize(1);
    var entry = changeLog.getFirst();
    assertThat(entry.getPreviousState()).isEqualTo(history.getPreviousStatus().getDescription());
    assertThat(entry.getNextState()).isEqualTo(history.getNextStatus().getDescription());
    assertThat(entry.getChangedAt()).isEqualTo(format(history.getChangedAt()));
    var user = history.getUpdatedByUser();
    assertThat(entry.getChangedByUser()).isEqualTo(formatName(user));
    assertThat(entry.getChangedByAccount()).isEqualTo(user.getAccount().getName());
  }

  @Test
  void shouldPopulateConsultationDetailsIfPresent() {
    var request = buildRequest();
    var consultation = buildConsultation(request, Duration.ofDays(7));
    when(stakeholderConsultationFinder.getPrivateConsultation(request))
        .thenReturn(Optional.of(consultation));

    var model = notificationModelFactory.toNotificationModel(request);

    var modelConsultation = model.getConsultation();
    assertThat(modelConsultation.getReviewStartDate())
        .isEqualTo(format(consultation.getStartDate()));
    assertThat(modelConsultation.getReviewEndDate()).isEqualTo(format(consultation.getEndDate()));
  }

  @Test
  void shouldPopulateProjectAndRequestDetailsForConsultations() {
    var request = buildRequest();
    var consultation = buildConsultation(request, Duration.ofDays(7));

    var model = notificationModelFactory.toNotificationModel(List.of(consultation));

    var modelConsultation = model.getConsultations().stream().toList().getFirst();
    assertThat(modelConsultation.getReviewStartDate())
        .isEqualTo(format(consultation.getStartDate()));
    assertThat(modelConsultation.getReviewEndDate()).isEqualTo(format(consultation.getEndDate()));
    assertThat(modelConsultation.getProject().getGsid()).isEqualTo(request.getProject().getGsid());
    assertThat(modelConsultation.getRequest().getId()).isEqualTo(request.getId());
  }

  @Test
  void shouldReturnNoReasonProvidedWhenCancellationReasonIsNull() {
    var request = buildRequest();
    request.setCancellationReason(null);
    request.setOtherCancellationReason(null);

    var model = notificationModelFactory.toNotificationModel(request);

    assertThat(model.getRequest().getCancellationReason()).isEqualTo("No reason provided");
  }

  @Test
  void shouldReturnOtherCancellationReasonTextWhenReasonIsOther() {
    var request = buildRequest();
    var cancellationReason = buildOtherCancellationReason();
    request.setCancellationReason(cancellationReason);
    request.setOtherCancellationReason("Custom cancellation reason text");

    var model = notificationModelFactory.toNotificationModel(request);

    assertThat(model.getRequest().getCancellationReason())
        .isEqualTo("Custom cancellation reason text");
  }

  @Test
  void shouldReturnStandardCancellationReasonDescription() {
    var request = buildRequest();
    var cancellationReason = buildStandardCancellationReason("Request was duplicated");
    request.setCancellationReason(cancellationReason);

    var model = notificationModelFactory.toNotificationModel(request);

    assertThat(model.getRequest().getCancellationReason()).isEqualTo("Request was duplicated");
  }

  private static String format(ZonedDateTime dateTime) {
    return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
  }

  private static String formatName(User user) {
    return String.format("%s %s", user.getGivenName(), user.getFamilyName());
  }

  private static String toString(List<ProjectType> types) {
    return types.stream().map(ReferenceDataType::getDescription).collect(Collectors.joining(", "));
  }

  private Request buildRequest() {
    return Request.builder()
        .id(123)
        .requestName("A test request")
        .project(buildProject())
        .currentRoundIndex(0)
        .build();
  }

  private static RequestActivity buildRequestActivity() {
    return RequestActivity.builder()
        .plannedEndDate(DATE.plusDays(5))
        .displayName("Activity display name")
        .build();
  }

  private static Project buildProject() {
    return Project.builder()
        .id(456)
        .gsid("GS8637")
        .name("Test Project")
        .projectStatus(buildProjectStatus())
        .projectTypes(buildProjectTypes())
        .projectLocations(List.of(buildProjectLocation()))
        .build();
  }

  private static List<ProjectType> buildProjectTypes() {
    return Stream.of("Project Type 1", "Project Type 2")
        .map(NotificationModelFactoryTest::buildProjectType)
        .toList();
  }

  private static ProjectType buildProjectType(String description) {
    var type = new ProjectType();
    type.setDescription(description);
    return type;
  }

  private static ProjectLocation buildProjectLocation() {
    var location = new ProjectLocation();
    location.setDescription("London");
    return location;
  }

  private static ProjectStatus buildProjectStatus() {
    var status = new ProjectStatus();
    status.setDescription("Project Status");
    return status;
  }

  private static List<RequestRound> buildRequestRounds(int currentIndex) {
    return IntStream.rangeClosed(0, currentIndex)
        .mapToObj(NotificationModelFactoryTest::buildRequestRound)
        .toList();
  }

  private static RequestRound buildRequestRound(int index) {
    return RequestRound.builder().roundIndex(index).endDate(DATE.plusDays(index)).build();
  }

  private static RequestStatusHistory buildRequestStatusHistory() {
    return RequestStatusHistory.builder()
        .previousStatus(buildRequestStatus("From Status"))
        .nextStatus(buildRequestStatus("To Status"))
        .changedAt(DATE.plusDays(3))
        .updatedByUser(buildUser())
        .build();
  }

  private static RequestStatus buildRequestStatus(String description) {
    var status = new RequestStatus();
    status.setDescription(description);
    return status;
  }

  private static User buildUser() {
    var user = new User();
    user.setGivenName("A");
    user.setFamilyName("Non");
    user.setAccount(buildAccount());
    return user;
  }

  private static Account buildAccount() {
    var account = new Account();
    account.setName("Test Account");
    return account;
  }

  private static StakeholderConsultation buildConsultation(Request request, Duration duration) {
    return StakeholderConsultation.builder()
        .consultationType(new ConsultationType(TAC_CONSULTATION))
        .project(request.getProject())
        .request(request)
        .startDate(DATE)
        .endDate(DATE.plus(duration))
        .build();
  }

  private static RequestCancellationReason buildOtherCancellationReason() {
    var reason = new RequestCancellationReason(8);
    reason.setDescription("Other");
    return reason;
  }

  private static RequestCancellationReason buildStandardCancellationReason(String description) {
    var reason = new RequestCancellationReason(1);
    reason.setDescription(description);
    return reason;
  }
}

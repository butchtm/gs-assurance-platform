package org.goldstandard.assurance.api.helper;

import jakarta.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.Arrays;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestAccountAssignment;
import org.goldstandard.assurance.api.entity.activity.Activity;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.entity.lookup.AssignmentStatus;
import org.goldstandard.assurance.api.entity.lookup.AssignmentType;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.fixtures.CreditingPeriodDTOMother;
import org.goldstandard.assurance.api.fixtures.ProjectMother;
import org.goldstandard.assurance.api.fixtures.RequestAccountAssignmentMother;
import org.goldstandard.assurance.api.repository.AccountRepository;
import org.goldstandard.assurance.api.repository.RequestAccountAssignmentRepository;
import org.goldstandard.assurance.api.repository.RequestMethodologyRepository;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.goldstandard.assurance.api.repository.RequestRoundRepository;
import org.goldstandard.assurance.api.repository.StakeholderConsultationRepository;
import org.goldstandard.assurance.api.repository.activity.RequestActivityRepository;
import org.goldstandard.assurance.api.repository.system.RequestStatusHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.domain.DeleteSpecification;
import org.springframework.stereotype.Component;

@TestConfiguration
@Component
public class RequestHelper {
  @Autowired private RequestRepository requestRepository;

  @Autowired private RequestAccountAssignmentRepository requestAccountAssignmentRepository;

  @Autowired private AccountRepository accountRepository;

  @Autowired private StakeholderConsultationRepository stakeholderConsultationRepository;

  @Autowired private RequestMethodologyRepository requestMethodologyRepository;

  @Autowired private RequestActivityRepository requestActivityRepository;

  @Autowired private RequestRoundRepository requestRoundRepository;

  @Autowired private RequestStatusHistoryRepository requestStatusHistoryRepository;

  public Request createTestRequest(int projectId) {
    var project = ProjectMother.withIdOnly(projectId);
    return createTestRequestWithRequestStatus(project, 6, false);
  }

  public Request createTestRequestWithRequestStatus(Project project, int statusId) {
    return createTestRequestWithRequestStatus(project, statusId, false);
  }

  public Request createTestRequestWithRequestStatusAndAssignment(
      int projectId, int statusId, int assignmentTypeId, Account account) {
    var project = ProjectMother.withIdOnly(projectId);
    var request = createTestRequestWithRequestStatus(project, statusId, false);
    createAssignmentForRequest(assignmentTypeId, account, request.getId());
    return request;
  }

  public Request createTestRequestWithRequestTypeStatusAndAssignment(
      Project project, int statusId, int assignmentTypeId, Account account, int requestTypeId) {
    var request =
        createTestRequestWithRequestStatusAndType(project, statusId, false, requestTypeId);
    createAssignmentForRequest(assignmentTypeId, account, request.getId());
    return request;
  }

  public Request createTestRequestWithRequestStatus(
      Project project, int statusId, boolean isMigrated) {
    return createTestRequestWithRequestStatusAndType(project, statusId, isMigrated, 1001);
  }

  public Request createTestRequestWithRequestStatusAndType(
      Project project, int statusId, boolean isMigrated, int typeId) {
    var request = new Request();
    request.setProject(project);
    request.setRequestName("test request no. 1");
    request.setRequestType(new RequestType(typeId));
    request.setIndex(0);
    request.setSameRequestTypeCount(0);
    request.setCurrentRequestStatus(new RequestStatus(statusId));
    request.setManuallyAdded(true);
    request.setMigrated(isMigrated);
    return requestRepository.save(request);
  }

  public void createAssignmentForRequest(int assignmentTypeId, Account account, Integer requestId) {
    var assignment =
        RequestAccountAssignmentMother.builder()
            .requestId(requestId)
            .account(account)
            .assignmentType(new AssignmentType(assignmentTypeId))
            .assignmentStatus(new AssignmentStatus(1))
            .build();
    requestAccountAssignmentRepository.save(assignment);
  }

  public void createRequestActivityForRequest(
      Integer requestId, int activityId, ZonedDateTime plannedEndDate) {
    var activity = new Activity(activityId, null, 0, 0, false, null, null, null);
    var requestActivity =
        RequestActivity.builder()
            .requestId(requestId)
            .activity(activity)
            .plannedEndDate(plannedEndDate)
            .startDate(ZonedDateTime.now().minusDays(5))
            .displayName("Test Activity")
            .build();
    requestActivityRepository.save(requestActivity);
  }

  public void deleteTestRequests(Request... requests) {
    Arrays.stream(requests).forEach(this::deleteTestRequest);
  }

  @Transactional
  public void deleteTestRequest(Request request) {
    request.setCurrentActivity(null);
    requestRepository.save(request);
    deleteTestRequest(request.getId());
  }

  @Transactional
  public void deleteTestRequest(Integer requestId) {
    stakeholderConsultationRepository
        .getAllByRequestId(requestId)
        .forEach(sc -> stakeholderConsultationRepository.delete(sc));
    requestAccountAssignmentRepository.delete(hasRequestId(requestId));
    requestMethodologyRepository
        .getAllByRequestId(requestId)
        .forEach(requestMethodologyRepository::delete);
    requestActivityRepository
        .findAllByRequestId(requestId)
        .forEach(ra -> requestActivityRepository.delete(ra));
    requestRoundRepository
        .findByRequestId(requestId)
        .forEach(rr -> requestRoundRepository.delete(rr));
    requestStatusHistoryRepository
        .findByRequestIdOrderByChangedAtDesc(requestId)
        .forEach(rsh -> requestStatusHistoryRepository.delete(rsh));
    requestRepository.deleteById(requestId);
  }

  private static DeleteSpecification<RequestAccountAssignment> hasRequestId(Integer requestId) {
    return DeleteSpecification.where(
        (assignment, cq, cb) -> cb.equal(assignment.get("requestId"), requestId));
  }

  public RequestCreateUpdateDTO buildCreateRequestDTO(ZonedDateTime time) {
    return RequestCreateUpdateDTO.builder()
        .requestTypeId(1000)
        .startDate(time)
        .creditingPeriod(CreditingPeriodDTOMother.withId(1))
        .emissionReductionAmount(456)
        .manuallyAdded(true)
        .build();
  }

  public RequestCreateUpdateDTO toStartConsultationDTO(int requestId) {
    return RequestCreateUpdateDTO.builder()
        .id(requestId)
        .startStakeholderConsultation(true)
        .manuallyAdded(true)
        .build();
  }

  public RequestCreateUpdateDTO toCompletedRequestDTO(int requestId) {
    return RequestCreateUpdateDTO.builder()
        .id(requestId)
        .requestStatusId(RequestStatus.COMPLETED_APPROVED_ID)
        .build();
  }

  public RequestCreateUpdateDTO toInProgressDTO(int requestId) {
    return RequestCreateUpdateDTO.builder()
        .id(requestId)
        .requestStatusId(RequestStatus.IN_PROGRESS_ID)
        .build();
  }
}

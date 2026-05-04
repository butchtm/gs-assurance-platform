package org.goldstandard.assurance.api.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestMonitoringPeriod;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.exception.RequestDoesNotBelongToProjectException;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.util.CollectionUtils;

@Slf4j
@Getter
@Setter
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Request {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private ZonedDateTime startDate;
  private ZonedDateTime endDate;
  private ZonedDateTime bookingDate;
  @ManyToOne private RequestType requestType;

  @OneToOne(cascade = CascadeType.ALL)
  private Invoice invoice;

  private Integer index;
  private String requestName;

  @Column(insertable = false, updatable = false) // It's maintained by the database
  private ZonedDateTime lastRequestStatusChangeDate;

  @ManyToOne private RequestStatus currentRequestStatus;

  @Fetch(FetchMode.JOIN)
  private Integer creditingPeriod;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn
  private RequestCreditingPeriod draftCreditingPeriod;

  private Integer monitoringPeriodId;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn
  private RequestMonitoringPeriod draftMonitoringPeriod;

  private Integer sameRequestTypeCount;
  private Integer requestDocumentStatus;
  private Integer emissionReductionAmount;
  private Boolean manuallyAdded;
  private Integer updatedByUserId;
  private Integer submittedByUserId;
  @ManyToOne private RequestCancellationReason cancellationReason;
  private String otherCancellationReason;

  @ManyToOne
  @JoinTable(
      name = "base_project_request",
      joinColumns = @JoinColumn(name = "request_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"))
  private Project project;

  private Integer currentRoundIndex;

  @Fetch(FetchMode.JOIN)
  @OneToMany(mappedBy = "request")
  private List<RequestRound> requestRounds;

  @ManyToOne private Batch batch;

  @Column(updatable = false)
  private Boolean migrated;

  private Boolean fastTracked;

  private Boolean internal;

  @With @ManyToOne private RequestActivity currentActivity;

  @OneToMany(mappedBy = "requestId", fetch = FetchType.LAZY)
  private List<RequestAccountAssignment> requestAccountAssignments;

  @OneToMany(mappedBy = "request", fetch = FetchType.EAGER)
  @With
  private List<RequestMethodology> requestMethodologies;

  @Column(updatable = false)
  private Boolean deltaExcluded;

  public Optional<RequestRound> findCurrentRound() {
    return findRound(currentRoundIndex);
  }

  public Optional<RequestRound> findRound(Integer index) {
    if (CollectionUtils.isEmpty(requestRounds)) {
      return Optional.empty();
    }
    return requestRounds.stream()
        .filter(round -> Objects.equals(round.getRoundIndex(), index))
        .findFirst();
  }

  public ZonedDateTime getCurrentRoundEndDate() {
    return findCurrentRound().map(RequestRound::getEndDate).orElse(null);
  }

  public void validateBelongsToProject(int projectId) {
    if (!belongsToProject(projectId)) {
      throw new RequestDoesNotBelongToProjectException(id, projectId);
    }
  }

  public boolean belongsToProject(int otherProjectId) {
    if (Objects.isNull(project)) {
      return false;
    }
    return Objects.equals(project.getId(), otherProjectId);
  }

  public Request getParentIfChild() {
    if (isBatchChild()) {
      log.info(
          "Using parent request [{}] in place of child request [{}].",
          batch.getParentRequest().getId(),
          id);
      return batch.getParentRequest();
    }
    return this;
  }

  public boolean isBatchChild() {
    if (Objects.isNull(batch)) {
      return false;
    }
    return !isBatchParent();
  }

  public boolean isBatchParent() {
    if (Objects.isNull(batch)) {
      return false;
    }
    return Objects.equals(batch.getParentRequest().getId(), id);
  }

  public boolean hasCompletedApprovedStatus() {
    return hasCurrentStatus(RequestStatus::isCompletedApproved);
  }

  public boolean hasRejectedStatus() {
    return hasCurrentStatus(RequestStatus::isRejected);
  }

  public boolean isActive() {
    return hasCurrentStatus(RequestStatus::isActive);
  }

  public boolean hasInactiveCurrentStatus() {
    return hasCurrentStatus(RequestStatus::isInactive);
  }

  public boolean hasActiveOrCompletedCurrentStatus() {
    return hasCurrentStatus(RequestStatus::isActiveOrCompleted);
  }

  public boolean isMigrated() {
    return Boolean.TRUE.equals(migrated);
  }

  public boolean statusIsInRoundCompletion() {
    return hasCurrentStatus(RequestStatus::isInRoundCompletion);
  }

  public boolean statusIsInProgress() {
    return hasCurrentStatus(RequestStatus::isInProgress);
  }

  public boolean hasCurrentStatus(Predicate<RequestStatus> predicate) {
    if (Objects.isNull(currentRequestStatus)) {
      return false;
    }
    return predicate.test(currentRequestStatus);
  }

  public boolean isPreliminaryReview() {
    return requestType.isPreliminaryReview();
  }

  public boolean isFastTracked() {
    return Boolean.TRUE.equals(fastTracked);
  }

  public Optional<RequestActivity> getCurrentActivity() {
    return Optional.ofNullable(currentActivity);
  }

  public boolean hasCurrentActivity() {
    return getCurrentActivity().isPresent();
  }

  public boolean isCurrentActivityHarmonisationOfFeedback() {
    return isCurrentActivity(RequestActivity::isHarmonisationOfFeedback);
  }

  public boolean isCurrentActivityComplete() {
    return getCurrentActivity().map(RequestActivity::getEndDate).isPresent();
  }

  public boolean hasInternal() {
    return Objects.nonNull(internal);
  }

  public boolean isInternal() {
    return Boolean.TRUE.equals(internal);
  }

  public boolean hasMethodologies() {
    return !getRequestMethodologies().isEmpty();
  }

  public Optional<RequestCreditingPeriod> getDraftCreditingPeriod() {
    return Optional.ofNullable(draftCreditingPeriod);
  }

  public Optional<RequestMonitoringPeriod> getDraftMonitoringPeriod() {
    return Optional.ofNullable(draftMonitoringPeriod);
  }

  public boolean hasDraftCreditingPeriod() {
    return Objects.nonNull(draftCreditingPeriod);
  }

  public boolean hasDraftMonitoringPeriod() {
    return Objects.nonNull(draftMonitoringPeriod);
  }

  private boolean isCurrentActivity(Predicate<RequestActivity> predicate) {
    return getCurrentActivity().filter(predicate).isPresent();
  }

  public boolean isDeltaExcluded() {
    return Boolean.TRUE.equals(deltaExcluded);
  }

  public boolean isCreateMonitoringPeriod() {
    return Objects.nonNull(draftMonitoringPeriod)
        && Objects.nonNull(draftMonitoringPeriod.getStartDate())
        && Objects.nonNull(draftMonitoringPeriod.getEndDate());
  }

  public boolean isUpdateMonitoringPeriod() {
    return Objects.nonNull(draftMonitoringPeriod)
        && Objects.nonNull(draftMonitoringPeriod.getPeriodId());
  }

  public boolean isApprovedPreliminaryReviewRequest() {
    return this.isPreliminaryReview() && this.hasCompletedApprovedStatus();
  }

  public boolean shouldCalculateDesignCertificationDate() {
    return requestType.shouldCalculateDesignCertificationDate();
  }

  public boolean hasIssuanceType() {
    return requestType.isIssuance();
  }
}

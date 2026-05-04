package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

@ToString
@Entity
public class RequestStatus extends ReferenceDataType {

  public static final int NOT_REQUESTED_ID = 0;
  public static final int COMPLETED_APPROVED_ID = 4;
  public static final int COMPLETED_REJECTED_ID = 5;
  public static final int IN_PREPARATION_ID = 6;
  public static final int OMITTED_ID = 7;
  public static final int CANCELLED_ID = 8;
  public static final int COMPLETENESS_CHECK_ID = 9;
  public static final int IN_PROGRESS_ID = 10;
  public static final int IN_AUDIT_ID = 11;
  public static final int IN_ASSIGNMENT_ID = 12;
  public static final int IN_ROUND_COMPLETION_ID = 13;

  private static final Collection<Integer> COMPLETED_IDS =
      List.of(COMPLETED_APPROVED_ID, COMPLETED_REJECTED_ID);

  private static final Collection<Integer> INACTIVE_IDS =
      CollectionUtils.union(COMPLETED_IDS, List.of(OMITTED_ID, CANCELLED_ID, NOT_REQUESTED_ID));

  public RequestStatus() {}

  public RequestStatus(Integer id) {
    this.id = id;
  }

  public static RequestStatus inPreparation() {
    return new RequestStatus(IN_PREPARATION_ID);
  }

  public static RequestStatus inAssignment() {
    return new RequestStatus(IN_ASSIGNMENT_ID);
  }

  public static RequestStatus completedApproved() {
    return new RequestStatus(COMPLETED_APPROVED_ID);
  }

  public static RequestStatus completedRejected() {
    return new RequestStatus(COMPLETED_REJECTED_ID);
  }

  public static RequestStatus inProgress() {
    return new RequestStatus(IN_PROGRESS_ID);
  }

  public static RequestStatus inRoundOfCompletion() {
    return new RequestStatus(IN_ROUND_COMPLETION_ID);
  }

  public boolean isCompletedApproved() {
    return Objects.equals(this.id, COMPLETED_APPROVED_ID);
  }

  public boolean isCompletedRejected() {
    return Objects.equals(this.id, COMPLETED_REJECTED_ID);
  }

  public boolean isOmitted() {
    return Objects.equals(this.id, OMITTED_ID);
  }

  public boolean isCancelled() {
    return Objects.equals(this.id, CANCELLED_ID);
  }

  public boolean isRejected() {
    return isCompletedRejected() || isOmitted() || isCancelled();
  }

  public boolean isInPreparation() {
    return Objects.equals(this.id, IN_PREPARATION_ID);
  }

  public boolean isInCompletenessCheck() {
    return Objects.equals(this.id, COMPLETENESS_CHECK_ID);
  }

  public boolean isInProgress() {
    return Objects.equals(this.id, IN_PROGRESS_ID);
  }

  public boolean isInAudit() {
    return Objects.equals(this.id, IN_AUDIT_ID);
  }

  public boolean isInAssignment() {
    return Objects.equals(this.id, IN_ASSIGNMENT_ID);
  }

  public static boolean isInAssignment(Integer id) {
    return Objects.equals(id, IN_ASSIGNMENT_ID);
  }

  public boolean isInRoundCompletion() {
    return Objects.equals(this.id, IN_ROUND_COMPLETION_ID);
  }

  public boolean isActive() {
    return !isInactive();
  }

  public boolean isActiveOrCompleted() {
    return isActive() || isCompleted();
  }

  public boolean isCompleted() {
    return hasState(COMPLETED_IDS);
  }

  public boolean isInactive() {
    return hasState(INACTIVE_IDS);
  }

  public boolean isClosed() {
    return isCompleted() || isCancelled() || isOmitted();
  }

  private boolean hasState(Collection<Integer> ids) {
    if (Objects.isNull(id)) {
      return false;
    }
    return ids.contains(id);
  }
}

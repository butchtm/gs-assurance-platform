package org.goldstandard.assurance.api.entity.activity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.goldstandard.assurance.api.entity.Account;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class RequestActivity {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  private final Integer id;

  private final Integer requestId;

  private final String displayName;
  @ManyToOne private final Activity activity;
  private final ZonedDateTime startDate;
  private final ZonedDateTime plannedEndDate;
  @With private final ZonedDateTime endDate;
  @ManyToOne private final Account responsibleAccount;
  private final Boolean skipped;

  public String getDisplayName() {
    if (Objects.isNull(displayName)) {
      // this has been added temporarily for the DEV env as any previously
      // created request activities won't have a display name populated,
      // this can be removed before deploying to staging and prod
      return activity.getName();
    }
    return displayName;
  }

  public boolean hasActivityId(Integer otherId) {
    return Objects.equals(activity.getId(), otherId);
  }

  public Integer getActivityId() {
    return activity.getId();
  }

  public boolean isComplete() {
    return Objects.nonNull(endDate);
  }

  public boolean isAssignmentOfCompletenessCheck() {
    return activity.isAssignmentOfCompletenessCheck();
  }

  public boolean isAssignmentOfProjectReview() {
    return activity.isAssignmentOfProjectReview();
  }

  public boolean isHarmonisationOfFeedback() {
    return activity.isHarmonisationOfFeedback();
  }

  public PathwayGroup getPathwayGroup() {
    return activity.getPathwayGroup();
  }

  public int getPathwayGroupTotalDays() {
    return activity.getPathwayGroupTotalDays();
  }

  public boolean isBefore(RequestActivity otherActivity) {
    return activity.isBefore(otherActivity.getActivity());
  }

  public RequestActivity withStartAndEndDate(ZonedDateTime date) {
    return toBuilder().startDate(date).endDate(date).build();
  }

  public boolean isSkipped() {
    return Boolean.TRUE.equals(skipped);
  }
}

package org.goldstandard.assurance.api.entity.activity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.AccountType;

@Entity
@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class Activity {

  public static final int ASSIGNMENT_OF_CC_ID = 0;
  public static final int ROUND_1_COMPLETENESS_CHECK_ID = 1;
  public static final Collection<Integer> ASSIGNMENT_OF_REVIEW_IDS =
      List.of(3, 6, 11, 16, 21, 26, 31, 36, 41);
  public static final Collection<Integer> CONCLUSION_OF_REVIEW_IDS =
      List.of(4, 5, 7, 9, 12, 14, 17, 19, 22, 24, 27, 29, 32, 34, 37, 39, 42, 44);
  public static final Collection<Integer> HARMONISATION_OF_FEEDBACK_IDS =
      List.of(8, 10, 13, 15, 18, 20, 23, 25, 28, 30, 33, 35, 38, 40, 43, 45);

  @Id private final Integer id;
  private final String name;
  private final int index;
  private final int totalDays;
  private final boolean internal;
  private final String displayName;

  @ManyToOne private final AccountType accountType;

  @ManyToOne private final PathwayGroup pathwayGroup;

  public boolean isOnReviewPathway() {
    return pathwayGroup.isReview();
  }

  public boolean isOnCompletenessCheckPathway() {
    return pathwayGroup.isCompletenessCheck();
  }

  public boolean isAssignmentOfCompletenessCheck() {
    return hasName("Assignment of CC");
  }

  public boolean isHarmonisationOfFeedback() {
    return hasName("Harmonisation of Feedback");
  }

  public boolean isAssignmentOfProjectReview() {
    return idIsOneOf(ASSIGNMENT_OF_REVIEW_IDS);
  }

  public int getPathwayGroupTotalDays() {
    return pathwayGroup.getTotalDays();
  }

  private boolean hasName(String otherName) {
    return Optional.ofNullable(name).map(n -> n.equalsIgnoreCase(otherName)).orElse(false);
  }

  private boolean idIsOneOf(Collection<Integer> ids) {
    if (Objects.isNull(id)) {
      return false;
    }
    return ids.contains(id);
  }

  public boolean isBefore(Activity otherActivity) {
    return index < otherActivity.index;
  }
}

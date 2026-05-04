package org.goldstandard.assurance.api.entity.activity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Pathway {

  public static final int CC_PATHWAY_INDEX = 0;
  public static final int FAST_TRACK_PATHWAY_INDEX = 1;
  public static final int QAQC_INTERNAL_PATHWAY_INDEX = 2;
  public static final int ROE_EXTERNAL_PATHWAY_INDEX = 3;
  public static final int PERFORMANCE_CERTIFICATION_EXTERNAL_PATHWAY_INDEX = 4;
  public static final int PERFORMANCE_CERTIFICATION_INTERNAL_PATHWAY_INDEX = 5;
  public static final int INCLUSION_EXTERNAL_PATHWAY_INDEX = 6;
  public static final int INCLUSION_INTERNAL_PATHWAY_INDEX = 7;
  public static final int COMBINED_EXTERNAL_PATHWAY_INDEX = 8;
  public static final int COMBINED_INTERNAL_PATHWAY_INDEX = 9;

  private static final Collection<Integer> REVIEW_IDS =
      List.of(
          FAST_TRACK_PATHWAY_INDEX,
          QAQC_INTERNAL_PATHWAY_INDEX,
          ROE_EXTERNAL_PATHWAY_INDEX,
          PERFORMANCE_CERTIFICATION_EXTERNAL_PATHWAY_INDEX,
          PERFORMANCE_CERTIFICATION_INTERNAL_PATHWAY_INDEX);

  @Id private Integer id;
  private String name;
  private int index;
  private Boolean fastTrack;

  public Pathway(Integer id) {
    this.id = id;
  }

  public boolean isReview() {
    return REVIEW_IDS.contains(id);
  }

  public boolean isCompletenessCheck() {
    return Objects.equals(id, CC_PATHWAY_INDEX);
  }
}

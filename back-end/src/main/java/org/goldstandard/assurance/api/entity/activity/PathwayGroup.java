package org.goldstandard.assurance.api.entity.activity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class PathwayGroup {

  public static final int CC_ROUND_2_PLUS_PG_INDEX = 1;
  public static final int FAST_TRACK_ROUND_2_PLUS_PG_INDEX = 1;
  public static final int QAQC_INTERNAL_ROUND_2_PLUS_PG_INDEX = 1;
  public static final int ROE_EXTERNAL_ROUND_2_PLUS_PG_INDEX = 1;
  public static final int PERFORMANCE_EXTERNAL_ROUND_2_PLUS_PG_INDEX = 1;
  public static final int PERFORMANCE_INTERNAL_ROUND_2_PLUS_PG_INDEX = 1;
  public static final int INCLUSION_EXTERNAL_ROUND_2_PLUS_PG_INDEX = 1;
  public static final int INCLUSION_INTERNAL_ROUND_2_PLUS_PG_INDEX = 1;
  public static final int COMBINED_EXTERNAL_ROUND_2_PLUS_PG_INDEX = 1;
  public static final int COMBINED_INTERNAL_ROUND_2_PLUS_PG_INDEX = 1;

  @Id private Integer id;
  private String name;
  private int totalDays;
  private int index;

  @ManyToOne private Pathway pathway;

  public boolean isReview() {
    return pathway.isReview();
  }

  public boolean isCompletenessCheck() {
    return pathway.isCompletenessCheck();
  }
}

package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
public class RequestType extends ExtendedReferenceDataType {

  public static final int PRELIMINARY_REVIEW_ID = 1000;
  public static final int INCLUSION_REGULAR_VPA = 1004;
  public static final int PERFORMANCE_CERTIFICATION_PROJECT = 1005;
  public static final int PERFORMANCE_CERTIFICATION_VPA = 1006;
  public static final int LEGACY_PERFORMANCE_REVIEW_ID = 5;
  public static final int DESIGN_CERTIFICATION_PROJECT = 1001;
  public static final int DESIGN_CERTIFICATION_POA_WITH_REAL_CASE_VPA = 1002;
  public static final int DESIGN_CERTIFICATION_REAL_CASE_VPA = 1003;
  public static final int DESIGN_CERTIFICATION_RENEWAL_PROJECT = 1007;
  public static final int DESIGN_CERTIFICATION_RENEWAL_POA_WITH_REAL_CASE_VPA = 1008;
  public static final int DESIGN_CERTIFICATION_RENEWAL_REAL_CASE_VPA = 1009;
  public static final int DESIGN_CERTIFICATION_RENEWAL_REGULAR_CASE_VPA = 1010;
  public static final int DESIGN_CHANGE_WITH_VVB = 1011;
  public static final int ISSUANCE = 1012;
  public static final int GENERIC = 1013;
  public static final int COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_PROJECT = 1017;
  public static final int COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_POA = 1018;
  public static final int COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_VPA = 1019;

  private static final Collection<Integer> PRELIMINARY_REVIEW_IDS =
      List.of(0, 1, 13, 15, 16, PRELIMINARY_REVIEW_ID);

  private static final Collection<Integer> ALLOW_METHODOLOGIES_IDS =
      List.of(
          DESIGN_CERTIFICATION_RENEWAL_PROJECT,
          DESIGN_CERTIFICATION_RENEWAL_POA_WITH_REAL_CASE_VPA,
          DESIGN_CHANGE_WITH_VVB,
          DESIGN_CERTIFICATION_RENEWAL_REGULAR_CASE_VPA,
          DESIGN_CERTIFICATION_RENEWAL_REAL_CASE_VPA);

  private static final Collection<Integer> ALLOW_CREATE_MONITORING_PERIOD_IDS =
      List.of(
          PERFORMANCE_CERTIFICATION_PROJECT,
          PERFORMANCE_CERTIFICATION_VPA,
          ISSUANCE,
          COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_POA,
          COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_PROJECT,
          COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_VPA,
          GENERIC);

  private static final Collection<Integer> ALLOW_UPDATE_MONITORING_PERIOD_IDS =
      List.of(
          PERFORMANCE_CERTIFICATION_PROJECT,
          PERFORMANCE_CERTIFICATION_VPA,
          COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_POA,
          COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_PROJECT,
          COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_VPA,
          GENERIC);

  private static final Collection<Integer> ALLOW_CREATE_CREDITING_PERIODS_IDS =
      List.of(
          DESIGN_CERTIFICATION_RENEWAL_PROJECT,
          DESIGN_CERTIFICATION_RENEWAL_POA_WITH_REAL_CASE_VPA,
          DESIGN_CERTIFICATION_RENEWAL_REGULAR_CASE_VPA,
          DESIGN_CERTIFICATION_RENEWAL_REAL_CASE_VPA);

  private static final Collection<Integer> DESIGN_CERTIFICATION_DATE_REQUEST_TYPES =
      List.of(
          DESIGN_CERTIFICATION_PROJECT,
          DESIGN_CERTIFICATION_POA_WITH_REAL_CASE_VPA,
          DESIGN_CERTIFICATION_REAL_CASE_VPA,
          COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_POA,
          COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_PROJECT,
          COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_VPA,
          INCLUSION_REGULAR_VPA);

  public RequestType() {}

  public RequestType(Integer id) {
    this.id = id;
  }

  public boolean isPreliminaryReview() {
    return PRELIMINARY_REVIEW_IDS.contains(id);
  }

  public boolean isIssuance() {
    return Objects.equals(id, ISSUANCE);
  }

  public boolean isOneOfPerformanceCertification() {
    return List.of(
            PERFORMANCE_CERTIFICATION_PROJECT,
            PERFORMANCE_CERTIFICATION_VPA,
            LEGACY_PERFORMANCE_REVIEW_ID)
        .contains(id);
  }

  public boolean isInclusionRegularCase() {
    return id == INCLUSION_REGULAR_VPA;
  }

  public boolean isOneOfCombinedDesignAndPerformanceCertification() {
    return List.of(
            COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_PROJECT,
            COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_POA,
            COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_VPA)
        .contains(id);
  }

  public boolean allowsMethodologies() {
    return ALLOW_METHODOLOGIES_IDS.contains(id);
  }

  public boolean allowsCreateCreditingPeriods() {
    return ALLOW_CREATE_CREDITING_PERIODS_IDS.contains(id);
  }

  public boolean allowsCreateMonitoringPeriod() {
    return ALLOW_CREATE_MONITORING_PERIOD_IDS.contains(id);
  }

  public boolean allowsUpdateMonitoringPeriod() {
    return ALLOW_UPDATE_MONITORING_PERIOD_IDS.contains(id);
  }

  public boolean shouldCalculateDesignCertificationDate() {
    return DESIGN_CERTIFICATION_DATE_REQUEST_TYPES.contains(id);
  }
}

package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import java.util.Collection;
import java.util.List;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Entity
public class ProjectClassification extends ExtendedReferenceDataType {

  private static final int LEGACY_POA = 1;
  private static final int REAL_OR_REGULAR_CASE_POA = 5;
  private static final int POA_ID = 7;
  private static final Collection<Integer> POA_IDS =
      List.of(LEGACY_POA, REAL_OR_REGULAR_CASE_POA, POA_ID);

  private static final int LEGACY_REAL_CASE_VPA_ID = 3;
  public static final int REAL_CASE_VPA_ID = 8;
  public static final int REAL_CASE_VPA_CROSS_LINKAGE_ID = 10;
  public static final String REAL_CASE_VPA_CODE = "REAL_CASE_VPA";
  public static final String REAL_CASE_VPA_CROSS_LINKAGE_CODE = "REAL_CASE_VPA_CROSS_LINKAGE";
  public static final String REAL_CASE_VPA_DESCRIPTION = "Real Case VPA";

  private static final Collection<Integer> REAL_CASE_VPA_IDS =
      List.of(LEGACY_REAL_CASE_VPA_ID, REAL_CASE_VPA_ID, REAL_CASE_VPA_CROSS_LINKAGE_ID);

  private static final int LEGACY_REGULAR_CASE_VPA_ID = 4;
  public static final int REGULAR_CASE_VPA_ID = 9;
  public static final int REGULAR_VPA_CROSS_LINKAGE_ID = 11;
  public static final String REGULAR_VPA_CODE = "REGULAR_VPA";
  public static final String REGULAR_VPA_CROSS_LINKAGE_CODE = "REGULAR_VPA_CROSS_LINKAGE";
  public static final String REGULAR_VPA_DESCRIPTION = "Regular VPA";

  private static final Collection<Integer> REGULAR_CASE_VPA_IDS =
      List.of(LEGACY_REGULAR_CASE_VPA_ID, REGULAR_CASE_VPA_ID, REGULAR_VPA_CROSS_LINKAGE_ID);

  private static final int LEGACY_VPA_CPA = 2;

  public ProjectClassification() {}

  public boolean isPoa() {
    return POA_IDS.contains(id);
  }

  public boolean isRealCaseVpa() {
    return REAL_CASE_VPA_IDS.contains(id);
  }

  public boolean isRegularCaseVpa() {
    return REGULAR_CASE_VPA_IDS.contains(id);
  }

  public boolean isLegacyVpaCpa() {
    return id == LEGACY_VPA_CPA;
  }

  public ProjectClassification(Integer id) {
    this.id = id;
  }

  public static ProjectClassification programmeOfActivities() {
    return new ProjectClassification(POA_ID);
  }

  public static ProjectClassification realCaseVpa() {
    return new ProjectClassification(REAL_CASE_VPA_ID);
  }

  public static ProjectClassification regularVpa() {
    return new ProjectClassification(REGULAR_CASE_VPA_ID);
  }

  public static ProjectClassification legacyVpaCpa() {
    return new ProjectClassification(LEGACY_VPA_CPA);
  }

  public static Collection<Integer> getPOAIds() {
    return POA_IDS;
  }

  public static Collection<Integer> getRealCaseVpaIds() {
    return REAL_CASE_VPA_IDS;
  }
}

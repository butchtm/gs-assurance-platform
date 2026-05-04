package org.goldstandard.assurance.api.fixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.DesignCertDateMatrix;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DesignCertDateMatrixMother {

  private static final Integer DEFAULT_ID = 10;
  private static final Integer DEFAULT_REQUEST_TYPE_ID = 2;

  public static DesignCertDateMatrix withVvbAssigned(Integer days) {
    return new DesignCertDateMatrix(DEFAULT_ID, DEFAULT_REQUEST_TYPE_ID, true, days);
  }

  public static DesignCertDateMatrix withoutVvbAssigned(Integer days) {
    return new DesignCertDateMatrix(DEFAULT_ID, DEFAULT_REQUEST_TYPE_ID, false, days);
  }

  public static DesignCertDateMatrix build(
      Integer requestTypeId, boolean vvbAssigned, Integer days) {
    return new DesignCertDateMatrix(DEFAULT_ID, requestTypeId, vvbAssigned, days);
  }
}

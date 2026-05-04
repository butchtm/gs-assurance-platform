package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Entity
@NoArgsConstructor(force = true)
public class DocumentPiiScanStatus extends ReferenceDataType {
  public static final int IN_PROGRESS = 1;
  public static final int COMPLETED_NO_PII = 2;
  public static final int COMPLETED_PII_FOUND = 3;
  public static final int FAILED = 4;
  public static final List<Integer> COMPLETED_STATUSES =
      List.of(COMPLETED_NO_PII, COMPLETED_PII_FOUND, FAILED);

  public DocumentPiiScanStatus(Integer id) {
    this.id = id;
  }

  public static boolean isCompleted(Integer statusId) {
    return statusId != null && COMPLETED_STATUSES.contains(statusId);
  }
}

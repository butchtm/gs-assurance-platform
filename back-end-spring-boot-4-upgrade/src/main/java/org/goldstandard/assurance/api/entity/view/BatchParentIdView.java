package org.goldstandard.assurance.api.entity.view;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity(name = "v_batch_parent_id")
@IdClass(BatchParentIdViewId.class)
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder(toBuilder = true)
@Getter
public class BatchParentIdView {

  @Id private final Integer requestId;

  @Id private final Integer projectId;

  private final Integer parentRequestId;
  private final Integer parentProjectId;

  public Integer getParentRequestIdOrRequestId() {
    return Optional.ofNullable(parentRequestId).orElse(requestId);
  }

  public Integer getParentProjectIdOrProjectId() {
    return Optional.ofNullable(parentProjectId).orElse(projectId);
  }

  public boolean isBatchParent() {
    if (Objects.isNull(parentRequestId)) {
      return false;
    }
    return Objects.equals(requestId, parentRequestId);
  }

  public static Comparator<BatchParentIdView> parentsFirst() {
    return Comparator.comparing(BatchParentIdView::isBatchParent, Comparator.reverseOrder());
  }
}

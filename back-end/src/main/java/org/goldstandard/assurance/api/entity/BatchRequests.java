package org.goldstandard.assurance.api.entity;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
public class BatchRequests {
  private Batch batch;
  private List<BatchRequest> requests;

  public BatchRequest getParent() {
    var isParent = buildIsParent();
    return requests.stream()
        .filter(isParent)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("batch request should have parent request"));
  }

  public List<BatchRequest> getChildren() {
    var isParent = buildIsParent();
    return requests.stream().filter(Predicate.not(isParent)).toList();
  }

  private Predicate<BatchRequest> buildIsParent() {
    var parentRequest = batch.getParentRequest();
    return new IsParent(parentRequest.getId());
  }

  @RequiredArgsConstructor
  private static class IsParent implements Predicate<BatchRequest> {

    private final Integer parentRequestId;

    @Override
    public boolean test(BatchRequest batchRequest) {
      return Objects.equals(batchRequest.getRequest().getId(), parentRequestId);
    }
  }
}

package org.goldstandard.assurance.api.fixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.entity.lookup.RequestType.RequestTypeBuilder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestTypeMother {

  public static RequestType withCode(String code) {
    return builder().code(code).build();
  }

  public static RequestType withId(Integer id) {
    return builder().id(id).build();
  }

  public static RequestType preliminaryReview() {
    return builder().build();
  }

  public static RequestTypeBuilder<?, ?> builder() {
    return RequestType.builder()
        .id(RequestType.PRELIMINARY_REVIEW_ID)
        .code("PRELIMINARY_REVIEW")
        .description("Preliminary Review")
        .isActive(true);
  }
}

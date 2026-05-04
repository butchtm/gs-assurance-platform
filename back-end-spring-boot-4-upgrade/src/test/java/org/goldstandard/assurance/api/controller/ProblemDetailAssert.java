package org.goldstandard.assurance.api.controller;

import java.util.Objects;
import org.assertj.core.api.AbstractAssert;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class ProblemDetailAssert extends AbstractAssert<ProblemDetailAssert, ProblemDetail> {

  protected ProblemDetailAssert(ProblemDetail actual) {
    super(actual, ProblemDetailAssert.class);
  }

  public ProblemDetailAssert hasStatus(HttpStatus status) {
    if (actual.getStatus() != status.value()) {
      failWithMessage("Expected status %s but was %s", status.value(), actual.getStatus());
    }
    return this;
  }

  public void hasDetail(String expectedDetail) {
    if (!Objects.equals(actual.getDetail(), expectedDetail)) {
      failWithMessage("Expected detail %s but was %s", expectedDetail, actual.getDetail());
    }
  }

  public static ProblemDetailAssert assertThat(ProblemDetail actual) {
    return new ProblemDetailAssert(actual);
  }
}

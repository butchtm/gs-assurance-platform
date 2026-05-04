package org.goldstandard.assurance.api.filter;

import jakarta.servlet.http.HttpServletRequest;
import java.util.function.Predicate;

public class ShouldNotFilterRequestPredicate implements Predicate<HttpServletRequest> {

  @Override
  public boolean test(HttpServletRequest request) {
    return request.getRequestURI().startsWith("/api/actuator");
  }
}

package org.goldstandard.assurance.api.service.activity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestRound;
import org.goldstandard.assurance.api.entity.activity.Activity;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RequestActivityDisplayNameFactory {

  private static final Collection<String> DEFAULT_PLACEHOLDERS = List.of("1", "2+");

  private final Collection<String> placeholders;

  public RequestActivityDisplayNameFactory() {
    this(DEFAULT_PLACEHOLDERS);
  }

  public String toDisplayName(Request request, Activity activity) {
    var displayName = activity.getDisplayName();
    if (activity.isOnReviewPathway()) {
      return populateRoundOfReviewNumber(request.getRequestRounds(), displayName);
    }
    return displayName;
  }

  private String populateRoundOfReviewNumber(List<RequestRound> rounds, String displayName) {
    int latestRound = rounds.stream().mapToInt(RequestRound::getRoundIndex).max().orElse(1);
    return findPlaceholderIfPresent(displayName)
        .map(placeholder -> displayName.replace(placeholder, Integer.toString(latestRound)))
        .orElse(displayName);
  }

  private Optional<String> findPlaceholderIfPresent(String displayName) {
    return placeholders.stream().filter(displayName::contains).findFirst();
  }
}

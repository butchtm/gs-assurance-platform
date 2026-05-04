package org.goldstandard.assurance.api.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Getter;
import lombok.With;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;

@Builder
@Getter
public class RequestUpdateParams {

  @With private final Request updatedRequest;
  @With private final Request originalRequest;
  @With private final Collection<Request> originalChildRequests;
  @With private final Collection<Request> updatedChildRequests;
  @With private final Collection<StakeholderConsultation> consultations;
  @With private final Collection<Project> originalProjects;
  @With private final Collection<Project> updatedProjects;
  @With private final Collection<RequestRound> rounds;
  @With private final Collection<RequestActivity> activities;
  @With private final Conversation conversation;

  public Collection<RequestMethodology> getOriginalMethodologies() {
    return toMethodologies(originalRequest);
  }

  public Collection<RequestMethodology> getUpdatedMethodologies() {
    return toMethodologies(updatedRequest);
  }

  public Optional<Conversation> getConversation() {
    return Optional.ofNullable(conversation);
  }

  public Map<Request, Request> toOriginalAndUpdatedRequests() {
    Map<Request, Request> map = new HashMap<>();
    map.put(originalRequest, updatedRequest);
    updatedChildRequests.forEach(r -> map.put(forceFindOriginalChildRequestById(r.getId()), r));
    return Collections.unmodifiableMap(map);
  }

  private Request forceFindOriginalChildRequestById(Integer requestId) {
    return originalChildRequests.stream()
        .filter(r -> Objects.equals(r.getId(), requestId))
        .findFirst()
        .orElseThrow();
  }

  private static Collection<RequestMethodology> toMethodologies(Request parent) {
    return toStream(parent)
        .map(Request::getRequestMethodologies)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .toList();
  }

  private static Stream<Request> toStream(Request request) {
    return Optional.ofNullable(request).stream();
  }
}

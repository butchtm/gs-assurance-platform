package org.goldstandard.assurance.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO;
import org.goldstandard.assurance.api.entity.Conversation;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestUpdateParams;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.goldstandard.assurance.api.entity.activity.RequestActivities;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.service.activity.ActivityPathwayPolicy;
import org.goldstandard.assurance.api.service.conversation.ConversationFinder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestUpdateParamsCalculator {

  private final RequestUpdateApplier applier;
  private final StakeholderConsultationFactory consultationFactory;
  private final BatchRequestFinder batchRequestFinder;
  private final ProjectStatusUpdatePolicy projectStatusUpdatePolicy;
  private final RoundOfReviewFactory roundOfReviewFactory;
  private final ActivityPathwayPolicy activityPathwayPolicy;
  private final ProjectCreditingPeriodUpdatePolicy projectCreditingPeriodUpdatePolicy;
  private final ProjectUpdateFinder projectUpdateFinder;
  private final ConversationFinder conversationFinder;

  public RequestUpdateParams toUpdateParams(
      Request originalRequest, RequestCreateUpdateDTO update) {
    var updatedRequest = applier.applyUpdates(originalRequest, update);
    var originalChildRequests = batchRequestFinder.getBatchChildRequests(originalRequest);
    var updatedChildRequests = toUpdatedChildRequests(originalChildRequests, update);
    var consultations = buildConsultationsIfRequired(updatedRequest, update);

    var originalProjects =
        projectUpdateFinder.toProjectsAppendingLegacyBatchProjectsIfPresent(
            updatedRequest, updatedChildRequests);
    var updatedProjects =
        toUpdatedProjects(
            originalRequest,
            updatedRequest,
            originalChildRequests,
            updatedChildRequests,
            originalProjects);

    var rounds = roundOfReviewFactory.toRoundsOfReview(originalRequest, updatedRequest);
    var activities = toRequestActivities(originalRequest, updatedRequest);
    roundOfReviewFactory.setRoundOfReviewEndDate(originalRequest, updatedRequest);
    var conversation = toUpdatedConversation(originalRequest, updatedRequest);
    var builder =
        RequestUpdateParams.builder()
            .originalRequest(originalRequest)
            .updatedRequest(updatedRequest)
            .originalChildRequests(originalChildRequests)
            .updatedChildRequests(updatedChildRequests)
            .consultations(consultations)
            .originalProjects(originalProjects)
            .updatedProjects(updatedProjects)
            .rounds(rounds)
            .activities(activities);
    conversation.ifPresent(builder::conversation);
    return builder.build();
  }

  private Collection<Request> toUpdatedChildRequests(
      Collection<Request> childRequests, RequestCreateUpdateDTO update) {
    return childRequests.stream()
        .map(childRequest -> applier.applyParentUpdates(childRequest, update))
        .toList();
  }

  private Collection<StakeholderConsultation> buildConsultationsIfRequired(
      Request request, RequestCreateUpdateDTO update) {
    if (update.shouldStartConsultation() && !request.isDeltaExcluded()) {
      return consultationFactory.buildConsultations(request);
    }
    return Collections.emptyList();
  }

  private Collection<Project> toUpdatedProjects(
      Request originalRequest,
      Request updatedRequest,
      Collection<Request> originalChildRequests,
      Collection<Request> updatedChildRequests,
      Collection<Project> originalProjects) {
    var updatedProjects =
        projectStatusUpdatePolicy.calculateProjectStatusUpdates(originalProjects, updatedRequest);
    var originalRequests =
        Stream.concat(Stream.of(originalRequest), originalChildRequests.stream()).toList();
    var updatedRequests =
        Stream.concat(Stream.of(updatedRequest), updatedChildRequests.stream()).toList();
    return projectCreditingPeriodUpdatePolicy.calculateProjectCreditingPeriodUpdates(
        originalRequests, updatedRequests, updatedProjects);
  }

  private Collection<RequestActivity> toRequestActivities(
      Request originalRequest, Request updatedRequest) {
    Collection<RequestActivity> activities = new ArrayList<>();
    updatedRequest.getCurrentActivity().ifPresent(activities::add);
    RequestActivities nextActivities =
        activityPathwayPolicy.createNextActivitiesIfRequired(originalRequest, updatedRequest);
    if (!nextActivities.isEmpty()) {
      activities.addAll(nextActivities.asCollection());
      updatedRequest.setCurrentActivity(nextActivities.getNext());
    }
    return activities;
  }

  private Optional<Conversation> toUpdatedConversation(
      Request originalRequest, Request updatedRequest) {
    var conversation = conversationFinder.findByRequestId(updatedRequest.getId());
    return conversation.flatMap(
        value -> closeConversationIfRequired(originalRequest, updatedRequest, value));
  }

  private Optional<Conversation> closeConversationIfRequired(
      Request originalRequest, Request updatedRequest, Conversation conversation) {
    if (shouldCloseConversation(originalRequest, updatedRequest)) {
      return Optional.of(conversation.close());
    }
    return Optional.empty();
  }

  private boolean shouldCloseConversation(Request originalRequest, Request updatedRequest) {
    return originalRequest.getCurrentRequestStatus().isActive()
        && updatedRequest.getCurrentRequestStatus().isInactive();
  }
}

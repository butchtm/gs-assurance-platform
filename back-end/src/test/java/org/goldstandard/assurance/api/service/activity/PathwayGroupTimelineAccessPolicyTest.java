package org.goldstandard.assurance.api.service.activity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.goldstandard.assurance.api.dto.PathwayGroupTimelineActivityDTO;
import org.goldstandard.assurance.api.dto.PathwayGroupTimelineActivityDTO.PathwayGroupTimelineActivityDTOBuilder;
import org.goldstandard.assurance.api.entity.activity.PathwayGroupTimeline;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PathwayGroupTimelineAccessPolicyTest {

  @InjectMocks PathwayGroupTimelineAccessPolicy policy;

  @Mock PermissionsService permissionsService;

  @Test
  void shouldThrowExceptionIfTimelineForCompletenessCheckPathwayAndUserIsProjectDeveloperOrVvb() {
    var timeline = mock(PathwayGroupTimeline.class);
    when(timeline.isForCompletenessCheckPathway()).thenReturn(true);
    when(permissionsService.isProjectDeveloperOrVVB()).thenReturn(true);

    var error = catchThrowable(() -> policy.validateAccess(timeline));

    assertThat(error)
        .isInstanceOf(PermissionException.class)
        .hasMessage(
            "project developers and vvbs are not permitted to view completeness check pathways");
  }

  @Test
  void
      shouldNotThrowExceptionIfTimelineNotForCompletenessCheckPathwayAndUserIsProjectDeveloperOrVvb() {
    var timeline = mock(PathwayGroupTimeline.class);
    when(timeline.isForCompletenessCheckPathway()).thenReturn(false);

    ThrowingCallable call = () -> policy.validateAccess(timeline);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void
      shouldNotThrowExceptionIfTimelineForCompletenessCheckPathwayAndUserIsNotProjectDeveloperOrVvb() {
    var timeline = mock(PathwayGroupTimeline.class);
    when(timeline.isForCompletenessCheckPathway()).thenReturn(true);
    when(permissionsService.isProjectDeveloperOrVVB()).thenReturn(false);

    ThrowingCallable call = () -> policy.validateAccess(timeline);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldNotRedactAnyDataIfUserIsNotProjectDeveloperOrVvb() {
    var activity = activityBuilder().build();
    when(permissionsService.isProjectDeveloperOrVVB()).thenReturn(false);

    var redacted = policy.redactIfRequired(List.of(activity));

    assertThat(redacted).usingRecursiveFieldByFieldElementComparator().containsExactly(activity);
  }

  @Test
  void
      shouldRedactAllDatesIfActivityIsNotHarmonisationOfFeedbackAndUserIsNotProjectDeveloperOrVvb() {
    var activity = activityBuilder().harmonisationOfFeedback(false).build();
    when(permissionsService.isProjectDeveloperOrVVB()).thenReturn(true);

    var redacted = policy.redactIfRequired(List.of(activity));

    assertThat(redacted)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields(
            "startDate", "dueDate", "endDate")
        .containsExactly(activity);
    var firstRedacted = IterableUtils.get(redacted, 0);
    assertThat(firstRedacted.getStartDate()).isNull();
    assertThat(firstRedacted.getDueDate()).isNull();
    assertThat(firstRedacted.getEndDate()).isNull();
  }

  @Test
  void
      shouldOnlyRedactStartDateIfActivityIsHarmonisationOfFeedbackAndUserIsNotProjectDeveloperOrVvb() {
    var activity = activityBuilder().harmonisationOfFeedback(true).build();
    when(permissionsService.isProjectDeveloperOrVVB()).thenReturn(true);

    var redacted = policy.redactIfRequired(List.of(activity));

    assertThat(redacted)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("startDate")
        .containsExactly(activity);
    var firstRedacted = IterableUtils.get(redacted, 0);
    assertThat(firstRedacted.getStartDate()).isNull();
  }

  private static PathwayGroupTimelineActivityDTOBuilder activityBuilder() {
    var now = ZonedDateTime.now();
    return PathwayGroupTimelineActivityDTO.builder()
        .index(1)
        .name("my activity")
        .startDate(now)
        .dueDate(now)
        .endDate(now)
        .externalResponsibility(true);
  }
}

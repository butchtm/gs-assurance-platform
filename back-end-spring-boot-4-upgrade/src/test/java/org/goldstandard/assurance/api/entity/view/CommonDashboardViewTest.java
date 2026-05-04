package org.goldstandard.assurance.api.entity.view;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.UnaryOperator;
import org.goldstandard.assurance.api.service.DummyNullDateFilter;
import org.junit.jupiter.api.Test;

class CommonDashboardViewTest {

  private static final ZonedDateTime DUMMY_DATE = ZonedDateTime.parse("1970-01-01T00:00Z");
  private static final ZonedDateTime ANY_OTHER_DATE = DUMMY_DATE.plus(1, ChronoUnit.MILLIS);

  private static final UnaryOperator<ZonedDateTime> DUMMY_DATE_FILTER =
      new DummyNullDateFilter(DUMMY_DATE);

  @Test
  void shouldReplaceRequestStartDateWithNullIfMatchesDummyDate() {
    var original = CommonDashboardView.builder().requestStartDate(DUMMY_DATE).build();

    var updated = original.removeDummyNullDates(DUMMY_DATE_FILTER);

    assertThat(updated.getRequestStartDate()).isNull();
  }

  @Test
  void shouldReplaceRequestEndDateWithNullIfMatchesDummyDate() {
    var original = CommonDashboardView.builder().requestEndDate(DUMMY_DATE).build();

    var updated = original.removeDummyNullDates(DUMMY_DATE_FILTER);

    assertThat(updated.getRequestEndDate()).isNull();
  }

  @Test
  void shouldReplaceFirstRoundStartDateWithNullIfMatchesDummyDate() {
    var original = CommonDashboardView.builder().firstRoundStartDate(DUMMY_DATE).build();

    var updated = original.removeDummyNullDates(DUMMY_DATE_FILTER);

    assertThat(updated.getFirstRoundStartDate()).isNull();
  }

  @Test
  void shouldReplaceCurrentRoundEndDateWithNullIfMatchesDummyDate() {
    var original = CommonDashboardView.builder().currentRoundEndDate(DUMMY_DATE).build();

    var updated = original.removeDummyNullDates(DUMMY_DATE_FILTER);

    assertThat(updated.getCurrentRoundEndDate()).isNull();
  }

  @Test
  void shouldNotChangeRequestStartDateIfDoesNotMatchDummyDate() {
    var original = CommonDashboardView.builder().requestStartDate(ANY_OTHER_DATE).build();

    var updated = original.removeDummyNullDates(DUMMY_DATE_FILTER);

    assertThat(updated.getRequestStartDate()).isEqualTo(ANY_OTHER_DATE);
  }

  @Test
  void shouldNotChangeRequestEndDateIfDoesNotMatchDummyDate() {
    var original = CommonDashboardView.builder().requestEndDate(ANY_OTHER_DATE).build();

    var updated = original.removeDummyNullDates(DUMMY_DATE_FILTER);

    assertThat(updated.getRequestEndDate()).isEqualTo(ANY_OTHER_DATE);
  }

  @Test
  void shouldNotChangeFirstRoundStartDateIfDoesNotMatchDummyDate() {
    var original = CommonDashboardView.builder().firstRoundStartDate(ANY_OTHER_DATE).build();

    var updated = original.removeDummyNullDates(DUMMY_DATE_FILTER);

    assertThat(updated.getFirstRoundStartDate()).isEqualTo(ANY_OTHER_DATE);
  }

  @Test
  void shouldNotChangeCurrentRoundEndDateIfDoesNotMatchDummyDate() {
    var original = CommonDashboardView.builder().currentRoundEndDate(ANY_OTHER_DATE).build();

    var updated = original.removeDummyNullDates(DUMMY_DATE_FILTER);

    assertThat(updated.getCurrentRoundEndDate()).isEqualTo(ANY_OTHER_DATE);
  }
}

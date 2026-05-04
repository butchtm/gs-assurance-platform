package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import org.goldstandard.assurance.api.entity.lookup.AssignmentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DueDateCalculatorTest {

  private final WeekdayCalculator weekdayCalculator = mock(WeekdayCalculator.class);

  private final DueDateCalculator dueDateCalculator = new DueDateCalculator(weekdayCalculator);

  @Test
  void shouldAddSevenWeekdaysForCompletenessCheckAssignmentType() {
    var now = ZonedDateTime.now();
    int numberOfWeekdays = 7;
    var expectedDueDate = now.plusDays(numberOfWeekdays);
    when(weekdayCalculator.addWeekdays(now, numberOfWeekdays)).thenReturn(expectedDueDate);

    var dueDate =
        dueDateCalculator.calculateDueDate(now, AssignmentType.COMPLETENESS_ASSIGNMENT_TYPE_ID);

    assertThat(dueDate).contains(expectedDueDate);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 5})
  void shouldNotReturnDueDateForAnyOtherAssignmentType(int assignmentTypeId) {
    var now = ZonedDateTime.now();

    var dueDate = dueDateCalculator.calculateDueDate(now, assignmentTypeId);

    assertThat(dueDate).isEmpty();
  }
}

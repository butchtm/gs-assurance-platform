package org.goldstandard.assurance.api.service;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.AssignmentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DueDateCalculator {

  private final WeekdayCalculator weekdayCalculator;
  private final Map<Integer, Integer> assignmentDaysByType;

  @Autowired
  public DueDateCalculator(WeekdayCalculator weekdayCalculator) {
    this(weekdayCalculator, buildAssignmentDaysByType());
  }

  public Optional<ZonedDateTime> calculateDueDate(ZonedDateTime now, int assignmentTypeId) {
    return getAssignmentDurationInDays(assignmentTypeId)
        .map(days -> weekdayCalculator.addWeekdays(now, days));
  }

  private Optional<Integer> getAssignmentDurationInDays(int typeId) {
    return Optional.ofNullable(assignmentDaysByType.get(typeId));
  }

  private static Map<Integer, Integer> buildAssignmentDaysByType() {
    return Map.of(AssignmentType.COMPLETENESS_ASSIGNMENT_TYPE_ID, 7);
  }
}

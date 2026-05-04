package org.goldstandard.assurance.api.service.report;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.AssignmentSummaryFiltersDTO;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.service.ReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssignmentSummaryFilenameGenerator {

  private final Clock clock;
  private final ReferenceDataService refDataService;
  private final DateTimeFormatter dateTimeFormatter;

  @Autowired
  public AssignmentSummaryFilenameGenerator(Clock clock, ReferenceDataService refDataService) {
    this(clock, refDataService, DateTimeFormatter.ofPattern("ddMMyyyy-HHmmssSSS"));
  }

  public String generateFilename(AssignmentSummaryFiltersDTO filters) {
    return String.format(
        "%s-assignment-summary-report-%s.xlsx",
        toAssignmentTypeDescription(filters), formattedDate());
  }

  private String formattedDate() {
    var now = ZonedDateTime.now(clock).withZoneSameInstant(ZoneId.of("UTC"));
    return now.format(dateTimeFormatter);
  }

  private String toAssignmentTypeDescription(AssignmentSummaryFiltersDTO filters) {
    var type = refDataService.getAssignmentType(filters.getAssignmentTypeId());
    return toFormattedCode(type);
  }

  private static String toFormattedCode(ReferenceDataType type) {
    return type.getCode().replace('/', '-').replace('_', '-').toLowerCase();
  }
}

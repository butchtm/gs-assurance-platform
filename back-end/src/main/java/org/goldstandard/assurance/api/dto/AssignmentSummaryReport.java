package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AssignmentSummaryReport {
  byte[] data;
  String filename;
}

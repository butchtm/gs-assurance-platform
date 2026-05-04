package org.goldstandard.assurance.api.dto.finance.salesorder;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SalesOrderHeader {
  @JsonProperty("GSFCustAccount")
  private final Integer accountId;

  @JsonProperty("ProjectID")
  private final String gsid;

  @JsonProperty("GSFREQUESTID")
  private final int requestId;

  @JsonProperty("SALESORDERLINEV2ENTITY")
  private final SalesOrderLine line;
}

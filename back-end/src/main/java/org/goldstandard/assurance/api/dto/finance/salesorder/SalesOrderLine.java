package org.goldstandard.assurance.api.dto.finance.salesorder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.finance.BigDecimalSerializer;

@RequiredArgsConstructor
@Getter
public class SalesOrderLine {

  @JsonProperty("GSFINVOICETYPE")
  private final String requestTypeCode;

  @JsonProperty("ORDEREDSALESQUANTITY")
  @JsonSerialize(using = BigDecimalSerializer.class)
  private final BigDecimal quantity;

  public SalesOrderLine(String requestTypeCode) {
    this(requestTypeCode, BigDecimal.ONE);
  }
}

package org.goldstandard.assurance.api.dto.finance.salesorder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@JacksonXmlRootElement(localName = "Document")
public class SalesOrder {

  @JsonProperty("SALESORDERHEADERV3ENTITY")
  private final SalesOrderHeader header;

  @JsonIgnore
  public Integer getRequestId() {
    return header.getRequestId();
  }
}

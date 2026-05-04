package org.goldstandard.assurance.api.dto.finance.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@JacksonXmlRootElement(localName = "Document")
public class ProjectContractDocument {

  @JsonProperty("PROJCONTRACTENTITY")
  private final ProjectContract contract;

  @JsonIgnore
  public String getId() {
    return contract.getId();
  }
}

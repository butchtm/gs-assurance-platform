package org.goldstandard.assurance.api.dto.finance.project;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProjectFundingRuleEntity {
  @JacksonXmlProperty(localName = "PROJECTCONTRACTID", isAttribute = true)
  private final String projectContractId;

  @JacksonXmlProperty(localName = "PRIORITY", isAttribute = true)
  private final String priority;

  @JacksonXmlProperty(localName = "PROJECTID", isAttribute = true)
  private final String projectId;

  @JacksonXmlProperty(localName = "ACTIVITYNUMBER", isAttribute = true)
  private final String activityNumber;

  @JacksonXmlProperty(localName = "TRANSACTIONTYPE", isAttribute = true)
  private final String transactionType;

  @JacksonXmlProperty(localName = "CATEGORYID", isAttribute = true)
  private final String categoryId;

  @JacksonXmlProperty(localName = "CATEGORYGROUPID", isAttribute = true)
  private final String categoryGroupId;

  @JacksonXmlProperty(localName = "ITEMID", isAttribute = true)
  private final String itemId;

  @JacksonXmlProperty(localName = "ENDDATE", isAttribute = true)
  private final String endDate;

  @JacksonXmlProperty(localName = "STARTDATE", isAttribute = true)
  private final String startDate;

  @JacksonXmlProperty(localName = "RESOURCEID", isAttribute = true)
  private final String resourceId;

  @JacksonXmlProperty(localName = "FUNDINGSOURCEID", isAttribute = true)
  private final String fundingSourceId;

  @JacksonXmlProperty(localName = "ALLOCATEROUNDING", isAttribute = true)
  private final String allocateRounding;

  @JacksonXmlProperty(localName = "ALLOCATIONPERCENTAGE", isAttribute = true)
  private final String allocationPercentage;

  public static ProjectFundingRuleEntity.ProjectFundingRuleEntityBuilder defaultBuilder() {
    return ProjectFundingRuleEntity.builder()
        .projectContractId("")
        .priority("")
        .projectId("")
        .activityNumber("")
        .transactionType("")
        .categoryId("")
        .categoryGroupId("")
        .itemId("")
        .endDate("")
        .startDate("")
        .resourceId("")
        .fundingSourceId("")
        .allocateRounding("")
        .allocationPercentage("100.000000");
  }
}

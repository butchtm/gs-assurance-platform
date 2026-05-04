package org.goldstandard.assurance.api.dto.finance.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@Builder
@Getter
public class ProjectContract {
  @JacksonXmlProperty(localName = "PROJECTCONTRACTID", isAttribute = true)
  private final String id;

  @JacksonXmlProperty(localName = "CONTRACTDATE", isAttribute = true)
  private final String date;

  @JacksonXmlProperty(localName = "CONTRACTLINES", isAttribute = true)
  private final String lines;

  @JacksonXmlProperty(localName = "CUSTOMERRETENTIONTERMID", isAttribute = true)
  private final String customerRetentionTermId;

  @JacksonXmlProperty(localName = "DEFAULTPOSTINGLEVEL", isAttribute = true)
  private final String defaultPostingLevel;

  @JacksonXmlProperty(localName = "INVOICEFREQUENCY", isAttribute = true)
  private final String invoiceFrequency;

  @JacksonXmlProperty(localName = "INVOICINGNAME", isAttribute = true)
  private final String invoicingName;

  @JacksonXmlProperty(localName = "LISTCODEID", isAttribute = true)
  private final String listCodeId;

  @JacksonXmlProperty(localName = "LOCKCONTRACTSALESCURRENCY", isAttribute = true)
  private final String lockContractSalesCurrency;

  @JacksonXmlProperty(localName = "MINIMUMTIMEINCREMENT", isAttribute = true)
  private final String minimumTimeIncrement;

  @JacksonXmlProperty(localName = "NAME", isAttribute = true)
  private final String name;

  @JacksonXmlProperty(localName = "PRICEGROUP", isAttribute = true)
  private final String priceGroup;

  @JacksonXmlProperty(localName = "PROGRESSINVOICING", isAttribute = true)
  private final String progressInvoicing;

  @JacksonXmlProperty(localName = "RETAINAGEPERCENT", isAttribute = true)
  private final String retainagePercent;

  @JacksonXmlProperty(localName = "SALESCURRENCY", isAttribute = true)
  private final String salesCurrency;

  @JacksonXmlProperty(localName = "SALESRESPONSIBLEPERSONNELNUMBER", isAttribute = true)
  private final String salesResponsiblePersonnelNumber;

  @JacksonXmlProperty(localName = "SALESTAXGROUP", isAttribute = true)
  private final String salesTaxGroup;

  @JacksonXmlProperty(localName = "TRANSACTIONCODE", isAttribute = true)
  private final String transactionCode;

  @JsonProperty("PROJFUNDINGSOURCEENTITY")
  private final FundingSource fundingSource;

  @JsonProperty("PROJECTENTITY")
  private final ProjectEntity project;

  public static ProjectContract.ProjectContractBuilder defaultBuilder() {
    return ProjectContract.builder()
        .id("")
        .date("")
        .lines("")
        .customerRetentionTermId("")
        .defaultPostingLevel("")
        .invoiceFrequency("")
        .invoicingName("")
        .listCodeId("")
        .lockContractSalesCurrency("")
        .minimumTimeIncrement("")
        .name("")
        .priceGroup("")
        .progressInvoicing("")
        .retainagePercent("")
        .salesCurrency("USD")
        .salesResponsiblePersonnelNumber("")
        .salesTaxGroup("")
        .transactionCode("");
  }
}

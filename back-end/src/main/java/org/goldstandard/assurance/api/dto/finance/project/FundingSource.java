package org.goldstandard.assurance.api.dto.finance.project;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FundingSource {
  @JacksonXmlProperty(localName = "PROJECTCONTRACTID", isAttribute = true)
  private final String projectContractId;

  @JacksonXmlProperty(localName = "FUNDINGSOURCEID", isAttribute = true)
  private final String id;

  @JacksonXmlProperty(localName = "ADDRESSCITY", isAttribute = true)
  private final String city;

  @JacksonXmlProperty(localName = "ADDRESSCOUNTRY", isAttribute = true)
  private final String country;

  @JacksonXmlProperty(localName = "ADDRESSCOUNTRYISOCODE", isAttribute = true)
  private final String countryIsoCode;

  @JacksonXmlProperty(localName = "ADDRESSCOUNTY", isAttribute = true)
  private final String county;

  @JacksonXmlProperty(localName = "ADDRESSDESCRIPTION", isAttribute = true)
  private final String addressDescription;

  @JacksonXmlProperty(localName = "ADDRESSDISTRICTNAME", isAttribute = true)
  private final String districtName;

  @JacksonXmlProperty(localName = "ADDRESSLATITUDE", isAttribute = true)
  private final String latitude;

  @JacksonXmlProperty(localName = "ADDRESSLONGITUDE", isAttribute = true)
  private final String longitude;

  @JacksonXmlProperty(localName = "ADDRESSSTATE", isAttribute = true)
  private final String state;

  @JacksonXmlProperty(localName = "ADDRESSSTREET", isAttribute = true)
  private final String street;

  @JacksonXmlProperty(localName = "ADDRESSTIMEZONE", isAttribute = true)
  private final String addressTimezone;

  @JacksonXmlProperty(localName = "ADDRESSVALIDFROM", isAttribute = true)
  private final String addressValidFrom;

  @JacksonXmlProperty(localName = "ADDRESSVALIDTO", isAttribute = true)
  private final String addressValidTo;

  @JacksonXmlProperty(localName = "ADDRESSZIPCODE", isAttribute = true)
  private final String zipCode;

  @JacksonXmlProperty(localName = "CASHDISCOUNT", isAttribute = true)
  private final String cashDiscount;

  @JacksonXmlProperty(localName = "CONTACTID", isAttribute = true)
  private final String contactId;

  @JacksonXmlProperty(localName = "CREATEDEFAULTFUNDINGRULE", isAttribute = true)
  private final String createdDefaultFundingRule;

  @JacksonXmlProperty(localName = "CUSTOMERORORGANIZATION", isAttribute = true)
  private final String customerOrOrganization;

  @JacksonXmlProperty(localName = "CUSTOMERREFERENCE", isAttribute = true)
  private final String customerReference;

  @JacksonXmlProperty(localName = "CUSTOMERREQUISITION", isAttribute = true)
  private final String customerRequisition;

  @JacksonXmlProperty(localName = "DIMENSIONACCOUNT", isAttribute = true)
  private final String dimensionAccount;

  @JacksonXmlProperty(localName = "DIMENSIONDISPLAYVALUE", isAttribute = true)
  private final String dimensionDisplayValue;

  @JacksonXmlProperty(localName = "EINVOICELINESPEC", isAttribute = true)
  private final String electronicInvoiceLineSpec;

  @JacksonXmlProperty(localName = "FUNDINGTYPE", isAttribute = true)
  private final String fundingType;

  @JacksonXmlProperty(localName = "GRANTID", isAttribute = true)
  private final String grantId;

  @JacksonXmlProperty(localName = "INDIVIDUALBUFFERDAYS", isAttribute = true)
  private final String individualBufferDays;

  @JacksonXmlProperty(localName = "INVOICEFORMAT", isAttribute = true)
  private final String invoiceFormat;

  @JacksonXmlProperty(localName = "INVOICELOCATIONID", isAttribute = true)
  private final String invoiceLocationId;

  @JacksonXmlProperty(localName = "INVOICENAME", isAttribute = true)
  private final String invoiceName;

  @JacksonXmlProperty(localName = "ISPRIMARYFUNDINGSOURCE", isAttribute = true)
  private final String isPrimaryFundingSource;

  @JacksonXmlProperty(localName = "LANGUAGE", isAttribute = true)
  private final String language;

  @JacksonXmlProperty(localName = "NUMBERSEQUENCEGROUP", isAttribute = true)
  private final String numberSequenceGroup;

  @JacksonXmlProperty(localName = "PARTYNUMBER", isAttribute = true)
  private final String partyNumber;

  @JacksonXmlProperty(localName = "PAYMENTSCHEDULE", isAttribute = true)
  private final String paymentSchedule;

  @JacksonXmlProperty(localName = "POSTINGPROFILE", isAttribute = true)
  private final String postingProfile;

  @JacksonXmlProperty(localName = "RETENTIONTERMID", isAttribute = true)
  private final String retentionTermId;

  @JacksonXmlProperty(localName = "TERMSOFPAYMENT", isAttribute = true)
  private final String termsOfPayment;

  public static FundingSource.FundingSourceBuilder defaultBuilder() {
    return FundingSource.builder()
        .projectContractId("")
        .id("")
        .city("")
        .country("")
        .countryIsoCode("")
        .county("")
        .addressDescription("")
        .districtName("")
        .latitude("")
        .longitude("")
        .state("")
        .street("")
        .addressTimezone("")
        .addressValidFrom("")
        .addressValidTo("")
        .zipCode("")
        .cashDiscount("")
        .contactId("")
        .createdDefaultFundingRule("")
        .customerOrOrganization("")
        .customerReference("")
        .customerRequisition("")
        .dimensionAccount("")
        .dimensionDisplayValue("")
        .electronicInvoiceLineSpec("")
        .fundingType("")
        .grantId("")
        .individualBufferDays("")
        .invoiceFormat("")
        .invoiceLocationId("")
        .invoiceName("")
        .isPrimaryFundingSource("")
        .language("")
        .numberSequenceGroup("")
        .partyNumber("")
        .paymentSchedule("")
        .postingProfile("")
        .retentionTermId("")
        .termsOfPayment("");
  }
}

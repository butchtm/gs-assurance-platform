package org.goldstandard.assurance.api.dto.finance.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProjectEntity {
  @JacksonXmlProperty(localName = "PROJECTID", isAttribute = true)
  private final String id;

  @JacksonXmlProperty(localName = "ACTIVEREVISION", isAttribute = true)
  private final String activeRevision;

  @JacksonXmlProperty(localName = "ACTUALENDDATE", isAttribute = true)
  private final String actualEndDate;

  @JacksonXmlProperty(localName = "ACTUALSTARTDATE", isAttribute = true)
  private final String actualStartDate;

  @JacksonXmlProperty(localName = "ALERTTIMEFRAMEWEEKS", isAttribute = true)
  private final String alertTimeFrameWeeks;

  @JacksonXmlProperty(localName = "ALLOWNEGATIVEBUDGETSTOBECARRIEDFORWARD", isAttribute = true)
  private final String allowNegativeBudgetsToBeCarriedForward;

  @JacksonXmlProperty(localName = "ALTERNATEPROJECT", isAttribute = true)
  private final String alternateProject;

  @JacksonXmlProperty(localName = "BANKDOCUMENTTYPE", isAttribute = true)
  private final String bankDocumentType;

  @JacksonXmlProperty(localName = "BUDGETCONTROLINTERVAL", isAttribute = true)
  private final String budgetControlInterval;

  @JacksonXmlProperty(localName = "BUDGETOVERRUNDEFAULT", isAttribute = true)
  private final String budgetOverrunDefault;

  @JacksonXmlProperty(localName = "CALENDAR", isAttribute = true)
  private final String calendar;

  @JacksonXmlProperty(localName = "CANCARRYFORWARDREMAININGBUDGETS", isAttribute = true)
  private final String canCarryForwardRemainingBudgets;

  @JacksonXmlProperty(localName = "CANUSEALTERNATEPROJECTBUDGET", isAttribute = true)
  private final String canUseAlternateProjectBudget;

  @JacksonXmlProperty(localName = "CANUSEBUDGETCONTROL", isAttribute = true)
  private final String canUseBudgetControl;

  @JacksonXmlProperty(localName = "CANVERIFYCOSTAGAINSTREMAININGFORECAST", isAttribute = true)
  private final String canVerifyCostAgainstRemainingForecast;

  @JacksonXmlProperty(localName = "CARBONCREDITS", isAttribute = true)
  private final String carbonCredits;

  @JacksonXmlProperty(localName = "CATEGORY", isAttribute = true)
  private final String category;

  @JacksonXmlProperty(localName = "CERTIFICATIONSTAGE", isAttribute = true)
  private final String certificationStage;

  @JacksonXmlProperty(localName = "CERTIFIEDPAYROLL", isAttribute = true)
  private final String certifiedPayroll;

  @JacksonXmlProperty(localName = "CONTACTINFO", isAttribute = true)
  private final String contactInfo;

  @JacksonXmlProperty(localName = "CREDITID", isAttribute = true)
  private final String creditId;

  @JacksonXmlProperty(localName = "CUSTOMERACCOUNT", isAttribute = true)
  private final String customerAccount;

  @JacksonXmlProperty(localName = "DATEOFCREATION", isAttribute = true)
  private final String dateOfCreation;

  @JacksonXmlProperty(localName = "DEFAULTINVOICEACCOUNT", isAttribute = true)
  private final String defaultInvoiceAccount;

  @JacksonXmlProperty(localName = "DELIVERYNAME", isAttribute = true)
  private final String deliveryName;

  @JacksonXmlProperty(localName = "DESCRIPTION", isAttribute = true)
  private final String description;

  @JacksonXmlProperty(localName = "DIMENSIONDISPLAYVALUE", isAttribute = true)
  private final String dimensionDisplayValue;

  @JacksonXmlProperty(localName = "DURATIONDETERMINESENDDATE", isAttribute = true)
  private final String durationDetermineSendDate;

  @JacksonXmlProperty(localName = "DURATIONINDAYS", isAttribute = true)
  private final String durationInDays;

  @JacksonXmlProperty(localName = "EMAIL", isAttribute = true)
  private final String email;

  @JacksonXmlProperty(localName = "ENDDATE1", isAttribute = true)
  private final String endDate1;

  @JacksonXmlProperty(localName = "ENDTIME", isAttribute = true)
  private final String endTime;

  @JacksonXmlProperty(localName = "ESTIMATEPROJECTID", isAttribute = true)
  private final String estimateProjectId;

  @JacksonXmlProperty(localName = "EXTENSIONDATE", isAttribute = true)
  private final String extensionDate;

  @JacksonXmlProperty(localName = "EXTERNALREVISION", isAttribute = true)
  private final String externalRevision;

  @JacksonXmlProperty(localName = "FIXEDASSETNUMBER", isAttribute = true)
  private final String fixedAssetNumber;

  @JacksonXmlProperty(localName = "GEOGRAPHICALLOCATION", isAttribute = true)
  private final String geographicalLocation;

  @JacksonXmlProperty(localName = "GSFCERTIFICATIONBODY", isAttribute = true)
  private final String gsfCertificationBody;

  @JacksonXmlProperty(localName = "GSFEXPIRYDATE", isAttribute = true)
  private final String gsfExpiryDate;

  @JacksonXmlProperty(localName = "GSFISSUANCEDATE", isAttribute = true)
  private final String gsfIssuanceDate;

  @JacksonXmlProperty(localName = "GSFPROJECTDETAILS", isAttribute = true)
  private final String gsfProjectDetails;

  @JacksonXmlProperty(localName = "GSFPROJECTID", isAttribute = true)
  private final String gsfProjectId;

  @JacksonXmlProperty(localName = "GSFPROJECTTYPE", isAttribute = true)
  private final String gsfProjectType;

  @JacksonXmlProperty(localName = "GSFQTY", isAttribute = true)
  private final String gsfQty;

  @JacksonXmlProperty(localName = "GSFSTATUS", isAttribute = true)
  private final String gsfStatus;

  @JacksonXmlProperty(localName = "GSFVALUE", isAttribute = true)
  private final String gsfValue;

  @JacksonXmlProperty(localName = "GSFWORKFLOWSTATUS", isAttribute = true)
  private final String gsfWorkflowStatus;

  @JacksonXmlProperty(localName = "HEADER", isAttribute = true)
  private final String header;

  @JacksonXmlProperty(localName = "INDIVIDUALGROUP", isAttribute = true)
  private final String individualGroup;

  @JacksonXmlProperty(localName = "INVOICECOST", isAttribute = true)
  private final String invoiceCost;

  @JacksonXmlProperty(localName = "INVOICINGMETHOD", isAttribute = true)
  private final String invoicingMethod;

  @JacksonXmlProperty(localName = "ISACTIVITYREQUIREDFOREXPENSEFORECAST", isAttribute = true)
  private final String isActivityRequiredForExpenseForecast;

  @JacksonXmlProperty(localName = "ISACTIVITYREQUIREDFOREXPENSETRANSACTION", isAttribute = true)
  private final String isActivityRequiredForExpenseTransaction;

  @JacksonXmlProperty(localName = "ISACTIVITYREQUIREDFORHOURFORECAST", isAttribute = true)
  private final String isActivityRequiredForHourForecast;

  @JacksonXmlProperty(localName = "ISACTIVITYREQUIREDFORHOURTRANSACTION", isAttribute = true)
  private final String isActivityRequiredForHourTransaction;

  @JacksonXmlProperty(localName = "ISACTIVITYREQUIREDFORITEMFORECAST", isAttribute = true)
  private final String isActivityRequiredForItemForecast;

  @JacksonXmlProperty(localName = "ISACTIVITYREQUIREDFORITEMTRANSACTION", isAttribute = true)
  private final String isActivityRequiredForItemTransaction;

  @JacksonXmlProperty(localName = "ISSUEDATE", isAttribute = true)
  private final String issueDate;

  @JacksonXmlProperty(localName = "JOBIDENTIFICATION", isAttribute = true)
  private final String jobIdentification;

  @JacksonXmlProperty(localName = "JOBPAYTYPE", isAttribute = true)
  private final String jobPayType;

  @JacksonXmlProperty(localName = "KYCSTATUS", isAttribute = true)
  private final String kycStatus;

  @JacksonXmlProperty(localName = "LEDGERPOSTINGSORTPRIORITY", isAttribute = true)
  private final String ledgerPostingSortPriority;

  @JacksonXmlProperty(localName = "LOCATIONID", isAttribute = true)
  private final String locationId;

  @JacksonXmlProperty(localName = "MILESTONE", isAttribute = true)
  private final String milestone;

  @JacksonXmlProperty(localName = "MINIMUMTIMEINCREMENT", isAttribute = true)
  private final String minimumTimeIncrement;

  @JacksonXmlProperty(localName = "PARENTPROJECT", isAttribute = true)
  private final String parentProject;

  @JacksonXmlProperty(localName = "POSTINGLEVEL", isAttribute = true)
  private final String postingLevel;

  @JacksonXmlProperty(localName = "PROJECTACTIVITYTYPE", isAttribute = true)
  private final String projectActivityType;

  @JacksonXmlProperty(localName = "PROJECTBUDGETMANAGEMENT", isAttribute = true)
  private final String projectBudgetManagement;

  @JacksonXmlProperty(localName = "PROJECTCLASSIFICATION", isAttribute = true)
  private final String projectClassification;

  @JacksonXmlProperty(localName = "PROJECTCONTRACTID", isAttribute = true)
  private final String contractId;

  @JacksonXmlProperty(localName = "PROJECTDESCRIPTION", isAttribute = true)
  private final String projectDescription;

  @JacksonXmlProperty(localName = "PROJECTEDENDDATE", isAttribute = true)
  private final String projectedEndDate;

  @JacksonXmlProperty(localName = "PROJECTEDSTARTDATE", isAttribute = true)
  private final String projectedStartDate;

  @JacksonXmlProperty(localName = "PROJECTGROUP", isAttribute = true)
  private final String projectGroup;

  @JacksonXmlProperty(localName = "PROJECTNAME", isAttribute = true)
  private final String projectName;

  @JacksonXmlProperty(localName = "PROJECTSTAGE", isAttribute = true)
  private final String projectStage;

  @JacksonXmlProperty(localName = "PROJECTSTATE", isAttribute = true)
  private final String projectState;

  @JacksonXmlProperty(localName = "PROJECTSTATUS", isAttribute = true)
  private final String projectStatus;

  @JacksonXmlProperty(localName = "PROJECTTEMPLATE", isAttribute = true)
  private final String projectTemplate;

  @JacksonXmlProperty(localName = "PROJECTTYPE", isAttribute = true)
  private final String projectType;

  @JacksonXmlProperty(localName = "PSASCHEDIGNORECALENDAR", isAttribute = true)
  private final String psaSchedIgnoreCalendar;

  @JacksonXmlProperty(localName = "REMAININGTONSCO2E", isAttribute = true)
  private final String remainingTonsCo2E;

  @JacksonXmlProperty(localName = "RENEWALDATE", isAttribute = true)
  private final String renewalDate;

  @JacksonXmlProperty(localName = "REQUESTID", isAttribute = true)
  private final String requestId;

  @JacksonXmlProperty(localName = "SALEPERCENTAGE", isAttribute = true)
  private final String salePercentage;

  @JacksonXmlProperty(localName = "SALESPRICEGROUP", isAttribute = true)
  private final String salesPriceGroup;

  @JacksonXmlProperty(localName = "SALESTAXGROUP", isAttribute = true)
  private final String salesTaxGroup;

  @JacksonXmlProperty(localName = "SCALE", isAttribute = true)
  private final String scale;

  @JacksonXmlProperty(localName = "SCHEDULESTATUS", isAttribute = true)
  private final String scheduleStatus;

  @JacksonXmlProperty(localName = "SDG1", isAttribute = true)
  private final String sdg1;

  @JacksonXmlProperty(localName = "SDG2", isAttribute = true)
  private final String sdg2;

  @JacksonXmlProperty(localName = "SDG3", isAttribute = true)
  private final String sdg3;

  @JacksonXmlProperty(localName = "SDG4", isAttribute = true)
  private final String sdg4;

  @JacksonXmlProperty(localName = "SDG5", isAttribute = true)
  private final String sdg5;

  @JacksonXmlProperty(localName = "SDG6", isAttribute = true)
  private final String sdg6;

  @JacksonXmlProperty(localName = "SDG7", isAttribute = true)
  private final String sdg7;

  @JacksonXmlProperty(localName = "SDG8", isAttribute = true)
  private final String sdg8;

  @JacksonXmlProperty(localName = "SDG9", isAttribute = true)
  private final String sdg9;

  @JacksonXmlProperty(localName = "SDG10", isAttribute = true)
  private final String sdg10;

  @JacksonXmlProperty(localName = "SDG11", isAttribute = true)
  private final String sdg11;

  @JacksonXmlProperty(localName = "SDG12", isAttribute = true)
  private final String sdg12;

  @JacksonXmlProperty(localName = "SDG13", isAttribute = true)
  private final String sdg13;

  @JacksonXmlProperty(localName = "SDG14", isAttribute = true)
  private final String sdg14;

  @JacksonXmlProperty(localName = "SDG15", isAttribute = true)
  private final String sdg15;

  @JacksonXmlProperty(localName = "SDG16", isAttribute = true)
  private final String sdg16;

  @JacksonXmlProperty(localName = "SDG17", isAttribute = true)
  private final String sdg17;

  @JacksonXmlProperty(localName = "SEARCHPRIORITY", isAttribute = true)
  private final String searchPriority;

  @JacksonXmlProperty(localName = "SECTORAL", isAttribute = true)
  private final String sectoral;

  @JacksonXmlProperty(localName = "SORTINGID1", isAttribute = true)
  private final String sortingId1;

  @JacksonXmlProperty(localName = "SORTINGID2", isAttribute = true)
  private final String sortingId2;

  @JacksonXmlProperty(localName = "SORTINGID3", isAttribute = true)
  private final String sortingId3;

  @JacksonXmlProperty(localName = "STANDARD", isAttribute = true)
  private final String standard;

  @JacksonXmlProperty(localName = "STARTDATE1", isAttribute = true)
  private final String startDate1;

  @JacksonXmlProperty(localName = "STARTTIME", isAttribute = true)
  private final String startTime;

  @JacksonXmlProperty(localName = "STATUS", isAttribute = true)
  private final String status;

  @JacksonXmlProperty(localName = "SUBPROJECTIDFORMAT", isAttribute = true)
  private final String subProjectIdFormat;

  @JacksonXmlProperty(localName = "TASKCOMPLETELYSCHEDULED", isAttribute = true)
  private final String taskCompletelyScheduled;

  @JacksonXmlProperty(localName = "TEMPLATEAPPLIED", isAttribute = true)
  private final String templateApplied;

  @JacksonXmlProperty(localName = "TONSCO2E", isAttribute = true)
  private final String tonsCo2E;

  @JacksonXmlProperty(localName = "TOTALEFFORTINHOURS", isAttribute = true)
  private final String totalEffortInHours;

  @JacksonXmlProperty(localName = "TRANSACTIONDATE", isAttribute = true)
  private final String transactionDate;

  @JacksonXmlProperty(localName = "TRANSACTIONTYPESCONTROLLED", isAttribute = true)
  private final String transactionTypesControlled;

  @JacksonXmlProperty(localName = "USINGENTITY", isAttribute = true)
  private final String usingEntity;

  @JacksonXmlProperty(localName = "VALUE", isAttribute = true)
  private final String value;

  @JacksonXmlProperty(localName = "WBSMILESTONES", isAttribute = true)
  private final String wbsMilestones;

  @JacksonXmlProperty(localName = "WORKERARCHITECTPERSONNELNUMBER", isAttribute = true)
  private final String workerArchitectPersonnelNumber;

  @JacksonXmlProperty(localName = "WORKERRESPFINANCIALPERSONNELNUMBER", isAttribute = true)
  private final String workerRespFinancialPersonnelNumber;

  @JacksonXmlProperty(localName = "WORKERRESPONSIBLEPERSONNELNUMBER", isAttribute = true)
  private final String workerResponsiblePersonnelNumber;

  @JacksonXmlProperty(localName = "WORKERRESPSALESPERSONNELNUMBER", isAttribute = true)
  private final String workerRespSalesPersonnelNumber;

  @JsonProperty("PROJFUNDINGRULEENTITY ")
  private final ProjectFundingRuleEntity fundingRuleEntity;

  public static ProjectEntity.ProjectEntityBuilder defaultBuilder() {
    return ProjectEntity.builder()
        .id("")
        .activeRevision("")
        .actualEndDate("")
        .actualStartDate("")
        .alertTimeFrameWeeks("")
        .allowNegativeBudgetsToBeCarriedForward("")
        .alternateProject("")
        .bankDocumentType("")
        .budgetControlInterval("")
        .budgetOverrunDefault("")
        .calendar("")
        .canCarryForwardRemainingBudgets("")
        .canUseAlternateProjectBudget("")
        .canUseBudgetControl("")
        .canVerifyCostAgainstRemainingForecast("")
        .carbonCredits("")
        .category("")
        .certificationStage("")
        .certifiedPayroll("")
        .contactInfo("")
        .creditId("")
        .customerAccount("")
        .dateOfCreation("")
        .defaultInvoiceAccount("")
        .deliveryName("")
        .description("")
        .dimensionDisplayValue("")
        .durationDetermineSendDate("")
        .durationInDays("")
        .email("")
        .endDate1("")
        .endTime("")
        .estimateProjectId("")
        .extensionDate("")
        .externalRevision("")
        .fixedAssetNumber("")
        .geographicalLocation("")
        .gsfCertificationBody("")
        .gsfExpiryDate("")
        .gsfIssuanceDate("")
        .gsfProjectDetails("")
        .gsfProjectId("")
        .gsfProjectType("")
        .gsfQty("")
        .gsfStatus("")
        .gsfValue("")
        .gsfWorkflowStatus("")
        .header("")
        .individualGroup("")
        .invoiceCost("")
        .invoicingMethod("")
        .isActivityRequiredForExpenseForecast("")
        .isActivityRequiredForExpenseTransaction("")
        .isActivityRequiredForHourForecast("")
        .isActivityRequiredForHourTransaction("")
        .isActivityRequiredForItemForecast("")
        .isActivityRequiredForItemTransaction("")
        .issueDate("")
        .jobIdentification("")
        .jobPayType("")
        .kycStatus("")
        .ledgerPostingSortPriority("")
        .locationId("")
        .milestone("")
        .minimumTimeIncrement("")
        .parentProject("")
        .postingLevel("")
        .projectActivityType("")
        .projectBudgetManagement("")
        .projectClassification("")
        .contractId("")
        .projectDescription("")
        .projectedEndDate("")
        .projectedStartDate("")
        .projectGroup("")
        .projectName("")
        .projectStage("")
        .projectState("")
        .projectStatus("")
        .projectTemplate("")
        .projectType("")
        .psaSchedIgnoreCalendar("")
        .remainingTonsCo2E("")
        .renewalDate("")
        .requestId("")
        .salePercentage("")
        .salesPriceGroup("")
        .salesTaxGroup("")
        .scale("")
        .scheduleStatus("")
        .sdg1("")
        .sdg2("")
        .sdg3("")
        .sdg4("")
        .sdg5("")
        .sdg6("")
        .sdg7("")
        .sdg8("")
        .sdg9("")
        .sdg10("")
        .sdg11("")
        .sdg12("")
        .sdg13("")
        .sdg14("")
        .sdg15("")
        .sdg16("")
        .sdg17("")
        .searchPriority("")
        .sectoral("")
        .sortingId1("")
        .sortingId2("")
        .sortingId3("")
        .standard("")
        .startDate1("")
        .startTime("")
        .status("")
        .subProjectIdFormat("")
        .taskCompletelyScheduled("")
        .templateApplied("")
        .tonsCo2E("")
        .totalEffortInHours("")
        .transactionDate("")
        .transactionTypesControlled("")
        .usingEntity("")
        .value("")
        .wbsMilestones("")
        .workerArchitectPersonnelNumber("")
        .workerRespFinancialPersonnelNumber("")
        .workerResponsiblePersonnelNumber("")
        .workerRespSalesPersonnelNumber("");
  }
}

class _RequestCreationPage {
  //For buttons and fields to trigger and populate methodologies modal within request creation page
  highRiskBanner = {
    highRiskBannerHeader: () => cy.get("p").contains(" - High Risk"),
    highRiskBannerSummary: (x) => cy.get("p").contains(x)
  }

  methodologies = {
    addMethodology: () => cy.get("button").contains("Add Methodology"), //trigger model
    projectMethodology: () => cy.get("label").contains("Project Methodology"), //use within model
    projectMethodologyNth: (x) => cy.get(`[data-testid="methodology-input-${x}"]`),
    methodologyVersion: () => cy.get("label").contains("Version").parent().find("input"), //use within model
    methodologyVersionNth: (x) => cy.get(`[data-testid="version-input-${x}"]`),
    addButton: () => cy.get('[type="submit"]').contains("Add") //confirm the selected methodology
  }

  monitoringPeriods = {
    newMonitoringPeriod: () => cy.get("button").contains("Add New Monitoring Period"),
    periodStartDate: () => cy.get("label").contains("Monitoring Period Start Date"),
    periodEndDate: () => cy.get("label").contains("Monitoring Period End Date")
  }

  fields = {
    requestType: () => cy.get("label").contains("Request Type"),
    createBatchRequest: () => cy.get(`[data-testid="createBatchRequest-select"]`).parent(),
    creditPeriodID: () => cy.get(`[id="creditingPeriodId-input"]`).parent(),
    batchCreditPeriodID: () => cy.get(`[data-testid="crediting-period-select"]`),
    estimatedEmission: () => cy.get('[data-testid="emission-reduction-input"]').find("input"),
    batchRequestType: () => cy.get(`[id="batchRequestTypeId-input"]`).parent(),
    chooseProjectToBatch: () => cy.get("label").contains("Choose Projects to batch this request with"),
    feeSchedule: () => cy.get('[data-testid="feeSchedule-select"]'),
    paaStatus: () => cy.get('[data-testid="paa-select"]'),
    paaStatusYes: () => cy.get('[data-testid="paa-option-true"]'),
    paaStatusNo: () => cy.get('[data-testid="paa-option-false"]')

  }

  dropdowns = {
    annualReport: () => cy.get("li").contains("Annual Report"), //From request type
    designChange: () => cy.get("li").contains("Design Change | With VVB"), //From request type
    prelimReview: () => cy.get("li").contains("Preliminary Review"), //From request type
    generic: () => cy.get("li").contains("Generic"), //From request type
    designCertification: () => cy.get("li").contains("Design Certification | Project"), //From request type

    combinedDesignPerformanceCertificationProject: () =>
      cy.get("li").contains("Combined Design and Performance Certification | Project"), //From request type
    combinedDesignPerformanceCertificationPoaWithReal: () =>
      cy.get("li").contains("Combined Design and Performance Certification | PoA with Real Case VPA"), //From request type
    combinedDesignPerformanceCertificationRealWithRegular: () =>
      cy.get("li").contains("Combined Design and Performance Certification | Real Case or Regular VPA"), //From request type

    inclusion: () => cy.get("li").contains("Inclusion | Regular Case VPA"), //From request type
    manageInformation: () => cy.get("li").contains("Manage Information | Project or PoA or VPA"), //From request type

    performanceCertificationProject: () => cy.get("li").contains("Performance Certification | Project"), //From request type
    performanceCertificationRealOrRegular: () => cy.get("li").contains("Performance Certification | Real or Regular VPAs"), //From request type
    issuance: () => cy.get("li").contains("Issuance"), //From request type

    decertificationProject: () => cy.get("li").contains("Decertification | Project"), //From request type
    commonMethodologyOne: () => cy.get("li").contains("ACM0001"), //From project methodologies
    commonMethodologyTwo: () => cy.get("li").contains("ACM0002"), //From project methodologies
    no: () => cy.get("li").contains("No"), //batching option 1
    yes: () => cy.get("li").contains("Yes"), //batching option 2
    poaPlusOneRealCase: () => cy.get("li").contains("PoA + 1 Real Case"), //batching type 1 for POA
    poaPlusOneOrMoreRealCase: () => cy.get("li").contains("PoA + 1 or more Real Case"), //batching type 1 for POA
    realOneOrMoreRegularCase: () => cy.get("li").contains("Real Case + 1 or more Regular Case"), //batching type 1 for REALCASE project
    regularTwoOrMoreRegularCase: () => cy.get("li").contains("2 or more Regular Case"), //batching type 1 for REGULARCASE project
    periodOne: () => cy.get("li").contains("Period"), //from credit period id
    cashPerCredit: () => cy.get("li").contains("Cash per Credit") //from fee schedule
  }

  navigation = {
    nextButton: () => cy.get('[type="submit"]').contains("Next"),
    finishButton: () => cy.get('[type="submit"]').contains("Finish")
  }

  checkPage(page) {
    if (parseInt(page) >= 4) {
      cy.get('#stepper').contains(page)
    } else {
      throw new Error(`Page ${page} is not valid for batch requests`)
    }
  }
}

const requestCreationPage = new _RequestCreationPage()
export default requestCreationPage

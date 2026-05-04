import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import requestCreationPage from "../../pages/requestCreationPage.cy.js"
import projectsPage from "../../pages/projectsPage.cy.js"

When("I click {string} from dropdown during request creation", (field) => {
  const actions = {
    "Annual Report": requestCreationPage.dropdowns.annualReport,
    "Design Change | With VVB": requestCreationPage.dropdowns.designChange,
    "Preliminary Review": requestCreationPage.dropdowns.prelimReview,
    "Generic": requestCreationPage.dropdowns.generic,
    "Design Certification | Project": requestCreationPage.dropdowns.designCertification,
    "Combined Design and Performance Certification | Project":
      requestCreationPage.dropdowns.combinedDesignPerformanceCertificationProject,
    "Combined Design and Performance Certification | Real Case or Regular VPA":
      requestCreationPage.dropdowns.combinedDesignPerformanceCertificationRealWithRegular,
    "Combined Design and Performance Certification | PoA with Real Case VPA":
      requestCreationPage.dropdowns.combinedDesignPerformanceCertificationPoaWithReal,
    "Inclusion | Regular Case VPA": requestCreationPage.dropdowns.inclusion,
    "Manage Information | Project or PoA or VPA": requestCreationPage.dropdowns.manageInformation,
    "Performance Certification | Project": requestCreationPage.dropdowns.performanceCertificationProject,
    "Performance Certification | Real or Regular VPAs": requestCreationPage.dropdowns.performanceCertificationRealOrRegular,
    "Issuance": requestCreationPage.dropdowns.issuance,
    "Decertification | Project": requestCreationPage.dropdowns.decertificationProject,
    "ACM0001": requestCreationPage.dropdowns.commonMethodologyOne,
    "ACM0002": requestCreationPage.dropdowns.commonMethodologyTwo,
    "No": requestCreationPage.dropdowns.no,
    "Yes": requestCreationPage.dropdowns.yes,
    "PoA + 1 Real Case": requestCreationPage.dropdowns.poaPlusOneRealCase,
    "period": requestCreationPage.dropdowns.periodOne,
    "PoA + 1 or more Real Case": requestCreationPage.dropdowns.poaPlusOneOrMoreRealCase,
    "Real Case + 1 or more Regular Case": requestCreationPage.dropdowns.realOneOrMoreRegularCase,
    "2 or more Regular Case": requestCreationPage.dropdowns.regularTwoOrMoreRegularCase,
    "cash per credit": requestCreationPage.dropdowns.cashPerCredit
  }

  const action = actions[field]

  if (action) {
    action().click()
  } else {
    throw new Error(`field ${field} not found`)
  }
})

//Master list of fields and clicking said fields from request creation
When("I click {string} from request creation page", (field) => {
  switch (field) {
    case "request type":
      requestCreationPage.fields.requestType().parent().find("button").click({ scrollBehavior: "center" })
      break
    case "estimated emission reduction/removal":
      requestCreationPage.fields.estimatedEmission().click({ force: true })
      break
    case "add methodology button":
      requestCreationPage.methodologies.addMethodology().click()
      break
    case "project methodology":
      requestCreationPage.methodologies.projectMethodology().parent().find("button").click({ scrollBehavior: "center" })
      break
    case "2nd project methodology field":
      requestCreationPage.methodologies
        .projectMethodologyNth(1) //done by index so 1=2nd field displayed
        .parent()
        .find("button")
        .click({ scrollBehavior: "center" })
      break
    case "version":
      requestCreationPage.methodologies.methodologyVersion().click({ force: true })
      break
    case "2nd methodology version":
      requestCreationPage.methodologies.methodologyVersionNth(1).click({ force: true })
      break
    case "add":
      requestCreationPage.methodologies.addButton().click()
      break
    case "finish":
      requestCreationPage.navigation.finishButton().click()
      break
    case "create batch request?":
      cy.wait(1000)
      requestCreationPage.fields.createBatchRequest().click({ force: true }).type("{downarrow}")
      break
    case "batch request type":
      cy.wait(1000)
      requestCreationPage.fields.batchRequestType().click({ force: true }).type("{downarrow}")
      break
    case "choose project to batch":
      cy.wait(1000)
      requestCreationPage.fields.chooseProjectToBatch().parent().find('button[title="Open"]').click()
      break
    case "credit period id":
      requestCreationPage.fields.creditPeriodID().click({ force: true }).type("{downarrow}")
      break
    case "credit period id (batch)":
      requestCreationPage.fields.batchCreditPeriodID().click({ force: true }).type("{downarrow}")
      break
    case "add new monitoring period":
      requestCreationPage.monitoringPeriods.newMonitoringPeriod().click()
      break
    case "fee schedule":
      requestCreationPage.fields.feeSchedule().click()
      break
    default:
      throw new Error(`field ${field} not found`)
  }
})

And("I add {string} {string} to field", (number, project) => {
  cy.wait(1000)
  if (number > 1) {
    for (let i = 0; i < parseInt(number); i++) {
      cy.get("li[aria-selected='false']").contains(project).first().click()
      requestCreationPage.fields.chooseProjectToBatch().parent().find('button[title="Open"]').click()
    }
  } else {
    cy.get("li").contains(project).click()
  }
})

//Repeat the core actions of steps a number of times for each additional project in a batched request
Given("I repeat steps for {string} additional project in batch", (numberInBatch) => {
  const totalSteps = parseInt(numberInBatch) //parseInt string->int. String from feature file steps
  for (let i = 0; i < totalSteps; i++) {
    requestCreationPage.checkPage((i + 4).toString()) //Occurs from 4th step on
    requestCreationPage.fields.batchCreditPeriodID().click({ force: true }).type("{downarrow}")
    requestCreationPage.dropdowns.periodOne().click()
    requestCreationPage.fields.estimatedEmission().click({ force: true }).type("1,000")
    requestCreationPage.navigation.nextButton().click()
  }
  requestCreationPage.checkPage((totalSteps + 4).toString())
})

//Repeat the core actions of steps a number of times for each additional project in a batched request
Given("I repeat on design change {string} additional times for each project in {string}", (numberInBatch, batchType) => {
  const totalSteps = parseInt(numberInBatch) //parseInt string->int. String from feature file steps
  for (let i = 0; i < totalSteps; i++) {
    cy.log("repeat:" + i)

    requestCreationPage.checkPage((i + 4).toString())
    requestCreationPage.fields.batchCreditPeriodID().click({ force: true }).type("{downarrow}")
    requestCreationPage.dropdowns.periodOne().click()

    //Effective for POA and VPAs batching
    if (i < 1 && (batchType == "PoA + 1 Real Case" || batchType == "PoA + 1 or more Real Case")) {
      cy.log("check: " + i)
      cy.log("check: " + batchType)
      requestCreationPage.methodologies.addMethodology().click()
      requestCreationPage.methodologies
        .projectMethodologyNth(1) //done by index so 1=2nd field displayed
        .parent()
        .find("button")
        .click({ scrollBehavior: "center" })
      requestCreationPage.dropdowns.commonMethodologyTwo().click()

      requestCreationPage.methodologies.methodologyVersionNth(1).click({ force: true })
      requestCreationPage.methodologies.methodologyVersionNth(1).type(2)
    }

    requestCreationPage.fields.estimatedEmission().click({ force: true }).type("1,000")
    requestCreationPage.navigation.nextButton().click()
  }
  requestCreationPage.checkPage((totalSteps + 4).toString())
})

And("I click next to see the request summary page", () => {
  requestCreationPage.navigation.nextButton().click()
})

Then("monitoring period start and end date appear", () => {
  requestCreationPage.monitoringPeriods.periodEndDate()
  requestCreationPage.monitoringPeriods.periodStartDate()
})

When("I supply fee schedule as required by request type {string}", (requestType) => {
  if (requestType == "Issuance") {
    cy.log("Issuance does not require this field, skip")
  } else {
    requestCreationPage.fields.feeSchedule().parent().click({ force: true }).type("{downarrow}")
    requestCreationPage.dropdowns.cashPerCredit().click({ force: true })
  }
})

Then("Field contains {string} for request type {string}", (choice, requestType) => {
  if (requestType == "Issuance") {
    cy.log("Issuance does not require this field, skip")
  } else {
    cy.get("div").contains(`${choice}`)
  }
})

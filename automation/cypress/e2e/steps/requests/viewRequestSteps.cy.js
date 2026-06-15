import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import selectedProjectsPage from "../../pages/selectedProjectPage.cy.js"
import requestCreationPage from "../../pages/requestCreationPage.cy.js"
import viewRequestPage from "../../pages/viewRequestPage.cy.js"

When("I view a {string} {string} request with PAA status {string} on a {string} {string} project as a {string}", (requestStatus, requestType, paaStatus, projectStatus, projectName,username) => {
    cy.readFile("cypress/fixtures/platformInfo.json").then((data) => {
        const gsids = {
        Listed: {
            "automation test (POA)": data["Listed POA GSID"],
            "automation test (Standalone)": data["Listed Standalone GSID"],
            "automation test (REGULARCASE)": data["Listed REGULARCASE GSID"],
            "automation test (REALCASE)": data["Listed REALCASE GSID"]
        },
        New: {
            "automation test (POA)": data["New POA GSID"],
            "automation test (REGULARCASE)": data["New REGULARCASE GSID"],
            "automation test (REALCASE)": data["New REALCASE GSID"],
            "automation test (Standalone)": data["New Standalone GSID"]
        }
        }

        const gsid = gsids[projectStatus][projectName]

        
        cy.waitForNetworkIdle(500)
        cy.getRequestApi(gsid, requestType,requestStatus, paaStatus).then((request) => {
            cy.clearAllCookies()
            cy.loginAsUser(username)
            cy.visit(`/projects/${gsid}/requests?expand=${request.id}`)
            cy.contains(request.id)
        })
    })
})

Then("The request type {string} is displayed", (requestType) => {
    cy.contains(requestType).should('exist')
})

When("I click Update Request", () => {
    viewRequestPage.buttons.updateRequestButton().click()
})

Then("PAA aligned is currently {string}", (paaStatus) => {
    switch(paaStatus) {
    case "na": 
      requestCreationPage.fields.paaStatus().should('not.exist')
      break
    case "true":
      requestCreationPage.fields.paaStatus().should('have.value', 'true')
      break
    case "false": 
      requestCreationPage.fields.paaStatus().should('have.value', 'false')
      break
  }
})

And("I click Next", () => {
    viewRequestPage.buttons.nextButton().click()
})

And("I click Back", () => {
    viewRequestPage.buttons.backButton().click()
})

And("I click Finish", () => {
    viewRequestPage.buttons.finishButton().click()
})

And("I cannot change PAA status", () => {
    requestCreationPage.fields.paaStatus().should('be.disabled')
})

Then("There are no actions available", () => {
    viewRequestPage.buttons.updateRequestButton().should('not.exist')
})
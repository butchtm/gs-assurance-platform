import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import consultationPage from "../../pages/consultationsPage.cy"

When("I collapse or expand all the rows", () => {
  consultationPage.filters.collapseAllConsultations().click()
})

When("I click a project in consultation", () => {
  consultationPage.tableDisplay.methodologyVersion().click()
})

Then("Important information headers can be seen", () => {
  consultationPage.openConsultation.commentsHeader()
  consultationPage.openConsultation.documentsHeader()
  consultationPage.openConsultation.projectHeader()
  consultationPage.openConsultation.requestHeader()
})

And("I select add comment", () => {
  cy.wait(500)
  consultationPage.openConsultation.commentsHeader().click({ force: true })
  consultationPage.openConsultation.addComment().click({ force: true })
})

Then("I can see comment modal with fields", () => {
  consultationPage.addCommentModal.addCommentHeader()
})

When("I fill out comment modal details", () => {
  consultationPage.addCommentModal.name().type("Auto Tester")
  consultationPage.addCommentModal.email().type("auto@testedbycaci.com")
  consultationPage.addCommentModal.comment().type("I am consulting")
})

And("I click submit comment from modal", () => {
  consultationPage.addCommentModal.submitComment().click()
})

Then("I can see a success popup for comment", () => {
  cy.wait(500)
  consultationPage.popupMessages.successComment()
})

And("I check my comment for PII", () => {
  consultationPage.addCommentModal.checkPii().click()
  consultationPage.addCommentModal.noPiiFound()
})
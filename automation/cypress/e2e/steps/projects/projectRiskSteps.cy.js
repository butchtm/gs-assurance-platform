import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import selectedProjectsPage from "../../pages/selectedProjectPage.cy"
import requestCreationPage from "../../pages/requestCreationPage.cy"

And("I select the update risk button", () => {
  selectedProjectsPage.projectRiskView.updateRisk().click()
})

Then("I can see my update project risk modal", () => {
  cy.get("h2").contains("Update Project Risk")
})

And("I type my risk information into the summary field", () => {
  selectedProjectsPage.projectRiskView.riskModal.summaryTextBox().click().clear().type("this project is high risk")
})

Then("I save my risk update by clicking the update button", () => {
  selectedProjectsPage.projectRiskView.riskModal.updateButton().click()
})

And("I can see my description in the risk history", () => {
  cy.get('td[data-index="3"]').contains("this project is high risk")
})

And("I can see my details {string} {string} {string} in the risk table", (risk, name, summary) => {
  const today = new Date()
  const formattedDate = today.toLocaleDateString("en-GB")
  selectedProjectsPage.projectRiskView.verifyTableContent(formattedDate, risk, name, summary)
})

And("I select the risk toggle button", () => {
  selectedProjectsPage.projectRiskView.riskModal.riskToggle().click()
})

And("I can see high risk flag at top of page", () => {
  selectedProjectsPage.highRiskBanner.highRiskBannerHeader()
})

When("I select the high risk warning from top of my project", () => {
  selectedProjectsPage.highRiskBanner.highRiskBannerHeader().click()
})

Then("I can see my risk summary {string} in the risk banner", (content) => {
  selectedProjectsPage.highRiskBanner.highRiskBannerSummary(content).should("exist")
})

When("I select the update risk icon from high risk banner", () => {
  selectedProjectsPage.highRiskBanner.highRiskBannerEdit().click()
})

And("I update the risk summary to include {string}", (content) => {
  selectedProjectsPage.projectRiskView.riskModal.summaryTextBox().click().clear().type(content)
})

And("I select the high risk warning from top of request creation wizard", () => {
  requestCreationPage.highRiskBanner.highRiskBannerHeader().should("exist").click()
})

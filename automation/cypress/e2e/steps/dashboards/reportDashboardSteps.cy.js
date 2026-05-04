import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import commonDashboard from "../../pages/reportDashboardPage.cy"

When("I select {string} filter from common dashboard page", (choice) => {
  switch (choice) {
    case "Name or Exact GSID":
      commonDashboard.filters.projectNameOrGSID().click({ force: true })
      break
    case "Request Status":
      commonDashboard.filters.requestStatus().click()
      break
    case "Request Type":
      commonDashboard.filters.requestType().click()
      break
    default:
      commonDashboard.filters.otherFilterButtons(choice).click()
  }
})

Then("I clear the common dashboard filters of default value", () => {
  commonDashboard.firstReviewStartDateFilter.openFilterButton().click()
  commonDashboard.firstReviewStartDateFilter.clearMinValueButton().click({ force: true })
  commonDashboard.firstReviewStartDateFilter.clearMaxValueButton().click({ force: true })
  /*commonDashboard.firstReviewStartDateFilter
    .clearMinValueButton()
    .click({ force: true })
    .type("{ctrl}a{backspace}", { force: true })
  commonDashboard.firstReviewStartDateFilter
    .clearMaxValueButton()
    .click({ force: true })
    .type("{ctrl}a{backspace}{esc}", { force: true })
  */

  commonDashboard.firstReviewStartDateFilter.escapeFilter().click().type("{esc}", { force: true })
  commonDashboard.firstReviewStartDateFilter
    .openFilterButton()
    .parent()
    .within(() => {
      commonDashboard.firstReviewStartDateFilter.filterCount(0)
    })
})

And("I select {string} from common dashboard filter drop down", (item) => {
  commonDashboard.selectFilterListItems(item)
})

Then("{string} is displayed in column {string} on common dashboard", (text, column) => {
  cy.wait(1000)
  let found = false
  let indexValue = commonDashboard.associateIndex(column)

  cy.get("tbody")
    .within(() => {
      cy.get("tr").each(($row) => {
        cy.wrap($row)
          .find("td")
          .eq(indexValue)
          .then(($cell) => {
            if ($cell.text().includes(`${text}`)) {
              found = true
            }
          })
      })
    })
    .then(() => {
      expect(found).to.be.true
    })
})

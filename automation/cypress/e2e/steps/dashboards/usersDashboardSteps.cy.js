import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import usersDashboardPage from "../../pages/usersDashboardPage.cy"

When("I select {string} from users dashboard page", (selection) => {
  if (selection === "active filter") {
    usersDashboardPage.filters.active().click()
  } else if (selection === "name or email") {
    usersDashboardPage.filters.usernameOrEmail().click()
  } else {
    throw new Error(`${selection} not found on users dashboard page`)
  }
})

And("I set filter to see {string} users", (choice) => {
  if (choice == "Inactive") {
    cy.get('input[type="checkbox"]').click().click()
  } else {
    cy.get('input[type="checkbox"]').click()
  }
})

Then("{string} {string} is displayed in the list", (choice, index) => {
  cy.wait(2500)
  let found = false
  let indexValue

  if (index == "active") {
    indexValue = 5
  } else if (index == "account") {
    indexValue = 6
  } else {
    indexValue = 1
  }

  cy.get("tbody")
    .within(() => {
      cy.get("tr").each(($row) => {
        cy.wrap($row)
          .find("td")
          .eq(indexValue)
          .then(($cell) => {
            if ($cell.text().includes(`${choice}`)) {
              found = true
            }
          })
      })
    })
    .then(() => {
      expect(found).to.be.true
    })
})

And("I click the {string} link to accounts", (choice) => {
  usersDashboardPage.link.linkToAccount(choice).click()
})

When("I select {string} from list navigation buttons", (choice) => {
  if (choice == "last page") {
    usersDashboardPage.filters.lastPageActive().click()
  } else {
    throw new Error(`${choice} not found on users dashboard page`)
  }
})

Then("{string} is displayed and related button disabled", (choice) => {
  if (choice == "last page") {
    usersDashboardPage.filters.lastPageDisabled()
  } else {
    throw new Error(`${choice} not found on users dashboard page`)
  }
})

import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import navigationBar from "../../pages/navigationBars.cy.js"
import adminPage from "../../pages/adminPage.cy.js"

const associateURL = {
  "projects": {
    "PD": "project-trees",
    "VVB": "projects",
    "FINANCE": "projects",
    "REVIEWER": "projects",
    "ARM TEAM": "projects",
    "ADMIN": "projects",
    "TAC": "projects"
  },
  "all requests": {
    "ADMIN": "all-requests",
    "ARM TEAM": "all-requests"
  },
  "my requests": {
    "PD": "my-requests",
    "VVB": "my-requests",
    "ARM TEAM": "my-requests",
    "TAC": "requests",
    "FINANCE": "my-requests",
    "REVIEWER": "my-requests"
  },
  "platform accounts": {
    "ADMIN": "accounts",
    "ARM TEAM": "accounts"
  },
  "users": {
    "ADMIN": "users",
    "ARM TEAM": "users"
  },
  "consultations": {
    "PD": "consultations",
    "VVB": "consultations",
    "FINANCE": "consultations",
    "REVIEWER": "consultations",
    "ARM TEAM": "consultations",
    "ADMIN": "consultations",
    "TAC": "consultations"
  },
  "admin": {
    "ADMIN": "admin",
    "ARM TEAM": "admin"
  },
  "common-dashboard": {
    "ADMIN": "common-dashboard",
    "ARM TEAM": "common-dashboard"
  }
}

Then("{string} click {string} from the navbar successfully", (username, page) => {
  const expectedUrl = associateURL[page][username]
  cy.log(`bingo: ${page} and ${username} and ${expectedUrl}`)

  if (expectedUrl) {

    navigationBar.primaryBarSelector(page, username)

    cy.waitForNetworkIdle(500)

    cy.url().should("include", expectedUrl)
  } else {
    throw new Error(`${expectedUrl} URL for API check not found`)
  }
})

//Retain this basic version for the many tests that already have this
When("I click {string} from the navbar", (button) => {
  navigationBar.primaryBarSelector(button)
  cy.waitForNetworkIdle(500)
})

Then("admins landing page text is visible", () => {
  adminPage.elements.textAfterSelectingAdmin()
})

Then("common dashboard has loaded and available to the user", () => {
  cy.url().should("include", "/common-dashboard")
})

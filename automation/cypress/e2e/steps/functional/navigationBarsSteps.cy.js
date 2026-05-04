import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import navigationBar from "../../pages/navigationBars.cy.js"
import adminPage from "../../pages/adminPage.cy.js"

const associateURL = {
  "projects": {
    "PD": "projects",
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
    "PD": "project-developer-requests",
    "VVB": "assigned-requests",
    "ARM TEAM": "assigned-requests",
    "TAC": "requests",
    "FINANCE": "assigned-requests",
    "REVIEWER": "assigned-requests"
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
  const exceptionToExpectedPattern = ["users", "consultations", "my requests", "admin"]
  cy.log(`bingo: ${page} and ${username} and ${expectedUrl}`)

  if (expectedUrl) {
    if (page == "my requests" && username == "PD") {
      navigationBar.primaryConfigureIntercept("POST", expectedUrl, true)
    } else if (page == "my requests" && username == "FINANCE") {
      navigationBar.primaryConfigureIntercept("GET", expectedUrl, false)
    } else if (exceptionToExpectedPattern.includes(page) || (page == "projects" && username == "PD")) {
      navigationBar.primaryConfigureIntercept("GET", expectedUrl, true)
    } else {
      navigationBar.primaryConfigureIntercept("GET", expectedUrl, false)
    }

    navigationBar.primaryBarSelector(page, username)

    cy.wait("@apiCheck")

    cy.request(expectedUrl).then((resp) => {
      expect(resp.status).to.eq(200)
    })
  } else {
    throw new Error(`${expectedUrl} URL for API check not found`)
  }
})

//Retain this basic version for the many tests that already have this
When("I click {string} from the navbar", (button) => {
  navigationBar.primaryBarSelector(button)
})

Then("admins landing page text is visible", () => {
  adminPage.elements.textAfterSelectingAdmin()
})

Then("common dashboard has loaded and available to the user", () => {
  cy.url().should("include", "/common-dashboard")
})

// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add('login', (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add('drag', { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add('dismiss', { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite('visit', (originalFn, url, options) => { ... })

// Custom command to accept cookies

import loginPage from "../e2e/pages/loginPage.cy.js"
import projectCreationPage from "../e2e/pages/projectCreationPage.cy.js"
import "cypress-axe"

Cypress.Commands.add("acceptCookies", () => {
  cy.get('[data-testid="cookie-consent"]').find("button").contains("Accept").click()
})

Cypress.Commands.add("loginAsUser", (user) => {
  switch (user) {
    case "PD":
      cy.login(Cypress.env("PD_USERNAME"), Cypress.env("PD_PASSWORD"))
      break
    case "VVB":
      cy.login(Cypress.env("VVB_USERNAME"), Cypress.env("VVB_PASSWORD"))
      break
    case "TAC":
      cy.login(Cypress.env("TAC_USERNAME"), Cypress.env("TAC_PASSWORD"))
      break
    case "ADMIN":
      cy.login(Cypress.env("ADMIN_USERNAME"), Cypress.env("ADMIN_PASSWORD"))
      break
    case "REVIEWER":
      cy.login(Cypress.env("REVIEWER_USERNAME"), Cypress.env("REVIEWER_PASSWORD"))
      break
    case "ARM TEAM":
      cy.login(Cypress.env("ARM_USERNAME"), Cypress.env("ARM_PASSWORD"))
      break
    case "FINANCE":
      cy.login(Cypress.env("FINANCE_USERNAME"), Cypress.env("FINANCE_PASSWORD"))
      break
    default:
      throw new Error(`User ${user} is not defined in the switch statement`)
  }
})

Cypress.Commands.add("login", (username, password) => {
  cy.visit("/")
  loginPage.usernameField().type(username) // Enter the username
  loginPage.passwordField().type(password) // Enter the password
  loginPage.loginButton().click() // Click the login button
})

//find project row on projects page
Cypress.Commands.add("findProjectRow", (projectName, projectStatus, callback) => {
  cy.wait(2500)
  cy.get("tr").each(($row) => {
    const nameMatches = $row.find("td").filter((index, td) => Cypress.$(td).text().includes(projectName)).length > 0
    let statusMatches = false

    //Needs to expand to all status
    if (projectStatus === "New") {
      statusMatches = $row.find("td").filter((index, td) => Cypress.$(td).text().includes("New")).length > 0
    } else if (projectStatus === "Listed") {
      statusMatches = $row.find("td").find('img[alt="Listed"]').length > 0
    }

    if (nameMatches && statusMatches) {
      cy.wrap($row).within(() => {
        callback()
      })
      return false // Exit the .each() loop once the correct row is found
    }
  })
})

Cypress.Commands.add("getGSID", () => {
  return cy.get("tr").eq(1).find("td").eq(0).invoke("text")
})

Cypress.Commands.add("findExactText", (selector, text) => {
  cy.get(selector).then(($elements) => {
    const element = [...$elements].find((el) => el.innerText.trim() === text)
    if (element) {
      return cy.wrap(element)
    } else {
      throw new Error(`Element with exact text "${text}" not found`)
    }
  })
})

Cypress.Commands.add("findPartialTextWithExactEnd", (selector, text) => {
  cy.get(selector).then(($elements) => {
    const element = [...$elements].find((el) => {
      const trimmedText = el.innerText.trim()
      return trimmedText.endsWith(text)
    })
    if (element) {
      return cy.wrap(element)
    } else {
      throw new Error(`Element ending with exact text "${text}" not found`)
    }
  })
})

Cypress.Commands.add("captureProjectGSID", (projectStatusAndClass, user) => {
  let extractedId
  let extractedGsid

  cy.intercept("POST", "/api/projects", (req) => {
    req.continue((res) => {
      extractedId = res.body
    })
  }).as("getProjectID")

  //Due to structure changes in PD we want this choice, so far only admin can get here by design
  if (user != "admin") {
    cy.intercept("GET", "**/pages**", (req) => {
      req.continue((res) => {
        const content = res.body.content

        const findProjectById = (items, id) => {
          for (const item of items) {
            if (item.id === id) {
              return item
            }
            if (item.children && item.children.length > 0) {
              const nestedProject = findProjectById(item.children, id)
              if (nestedProject) {
                return nestedProject
              }
            }
          }
        }

        const project = findProjectById(content, extractedId)

        if (project) {
          extractedGsid = project.gsid
        }
      })
    }).as("getPages")
  } else {
    cy.intercept("GET", "**/projects**", (req) => {
      req.continue((res) => {
        const project = res.body.content.find((project) => project.id === extractedId)
        if (project) {
          extractedGsid = project.gsid
        }
      })
    }).as("getProjectGSID")
  }

  projectCreationPage.navigation.finishButton().click()
  cy.wait("@getProjectID")

  if (user != "admin") {
    cy.wait("@getPages")
  } else {
    cy.wait("@getProjectGSID")
  }

  cy.wait(3000)

  cy.get('[fill="var(--toastify-icon-color-success)"]')
  cy.readFile("cypress/fixtures/platformInfo.json").then((data) => {
    const gsidKeyMap = {
      "New Standalone": "New Standalone GSID",
      "New POA": "New POA GSID",
      "New Real": "New REALCASE GSID",
      "New Regular": "New REGULARCASE GSID",
      "Listed standalone project": "Listed Standalone GSID",
      "Listed POA": "Listed POA GSID",
      "Listed REALCASE": "Listed REALCASE GSID",
      "Listed REGULARCASE": "Listed REGULARCASE GSID"
    }

    const key = gsidKeyMap[projectStatusAndClass]
    if (!key) {
      throw new Error(`${projectStatusAndClass} invalid`)
    }

    data[key] = extractedGsid
    cy.log(extractedId)
    cy.log(extractedGsid)
    cy.writeFile("cypress/fixtures/platformInfo.json", data)
  })
})

Cypress.Commands.add("injectAxeAndLogViolation", () => {
  cy.url().then((currentUrl) => {
    const pageName = currentUrl.replace(/https?:\/\/|\/|\./g, "_") // sanitize for filename
    cy.injectAxe()
    cy.checkA11y(
      null,
      null,
      (violations) => {
        const result = {
          page: currentUrl,
          timestamp: new Date().toISOString(),
          violations: violations
        }
        cy.writeFile(`cypress/fixtures/a11y/a11y-${pageName}.json`, JSON.stringify(result, null, 2))
      },
      {
        skipFailures: true
      }
    )
  })
})

Cypress.Commands.add("loginApi", () => {
  cy.request("POST", "/api/public/login", {
    username: "Admin",
    password: Cypress.env('ADMIN_PASSWORD')
  })
})

Cypress.Commands.add("deleteTestDataApi", () => {
  cy.request("DELETE", "/api/automated-test-projects")
})

Cypress.Commands.add("getEmailNotificationsApi", (howManyEmails) => {
  return cy.request({
    method: "GET",
    url: `/api/notifications/emails?size=${howManyEmails}`
  })
})

Cypress.Commands.add("getRunNumber", () => {
  cy.readFile("cypress/fixtures/platformInfo.json").then((file) => {
    return file["Run Number"]
  })
})

import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import projectCreationPage from "../pages/projectCreationPage.cy.js"
import projectsPage from "../pages/projectsPage.cy.js"
import requestCreationPage from "../pages/requestCreationPage.cy.js"
import platformAccountsPage from "../pages/platformAccountsPage.cy.js"
import usersDashboardPage from "../pages/usersDashboardPage.cy.js"
import allRequestDashboard from "../pages/allRequestDashboardPage.cy.js"
import myRequestDashboard from "../pages/myRequestDashboardPage.cy.js"
import commonDashboard from "../pages/reportDashboardPage.cy.js"

And("I type {string} into {string}", (text, field) => {
  const actions = {
    "project title": projectCreationPage.fields.projectTitle,
    "project description": projectCreationPage.fields.projectDescription,
    "project location": () => projectCreationPage.fields.projectLocation().type(`${text}{enter}`),
    "emission reduction removal": projectCreationPage.fields.emissionReductionRemoval,
    "notes": projectCreationPage.fields.notes,
    "projects search bar": projectsPage.feature.projectSearchBar,
    "emission reduction or removal": requestCreationPage.fields.estimatedEmission,
    "methodology version": requestCreationPage.methodologies.methodologyVersion,
    "2nd methodology version": () => requestCreationPage.methodologies.methodologyVersionNth(1),
    "programme of activities": projectCreationPage.fields.selectPOA,
    "real case vpa": projectCreationPage.fields.selectRealCase,
    "filter by name - accounts": platformAccountsPage.openFilter.filterByNameInput,
    "account name": platformAccountsPage.editPlatformAccount.accountName,
    "name or email": usersDashboardPage.filters.usernameOrEmail,
    "name or gsid": allRequestDashboard.filters.projectNameOrGsid,
    "developer filter (all requests)": allRequestDashboard.filters.developerInput,
    "reviewer filter (all requests)": allRequestDashboard.filters.reviewerInput,
    "request type (all requests)": allRequestDashboard.filters.requestInput,
    "request status (all requests)": allRequestDashboard.filters.statusInput,
    "vvb filter (all requests)": allRequestDashboard.filters.vvbInput,
    "qaqc filter (all requests)": allRequestDashboard.filters.qaqcInput,
    "name or gsid (my requests)": myRequestDashboard.filters.projectNameOrGSID,
    "name or gsid (common dashboard)": commonDashboard.filters.projectNameOrGSID
  }
  const action = actions[field]

  if (action) {
    cy.readFile("cypress/fixtures/platformInfo.json").then((data) => {
      if (
        text === "automation test (standalone project)" ||
        text === "automation test (POA)" ||
        text === "automation test (REALCASE)" ||
        text === "automation test (REGULARCASE)"
      ) {
        action().click({ force: true }).type(`${text} run:${data["Run Number"]}`)
      } else {
        action().click({ force: true }).type(text)
      }
    })
  } else {
    throw new Error(`field ${field} not found`)
  }
})

Then("The {string} page is loaded", (page) => {
  cy.request(page).then((resp) => {
    expect(resp.status).to.eq(200)
  })
})

Then("I am taken to {string}", (url) => {
  cy.url().should("include", url)
})

Then("We finish and see a success message and store {string} details", (input) => {
  cy.captureProjectGSID(`${input}`, "PD") //projectStatusAndClass and User
})

Then("Success message appears", () => {
  cy.get('[fill="var(--toastify-icon-color-success)"]')
})

Then("Field contains {string} in {string}", (text, type) => {
  switch (type) {
    case "value field":
      cy.get(`[value*="${text}"]`)
      break
    case "text area":
      cy.get("textarea").contains(`${text}`)
      break
    case "div element":
      cy.get("div").contains(`${text}`)
      break
    default:
      throw new Error(`type ${type} not found`)
  }
})

Then("I am taken to page {string}", (page) => {
  const validPages = ["2", "3", "4", "5", "6"]

  if (validPages.includes(page)) {
    projectCreationPage.navigation.activePage().contains(page)
  } else {
    throw new Error(`page ${page} not found`)
  }
})

Then("{string} can be seen", (verify) => {
  if (verify == "Project Characteristics") {
    cy.wait(3500)
    cy.get("h3").contains("Project Characteristics")
  } else if (verify == "Update Project") {
    cy.get("h4").contains("Update Project")
  }
})

Then("This page has an accessability check performed", () => {
  cy.injectAxeAndLogViolation()
})

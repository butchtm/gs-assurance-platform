import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import loginPage from "../../pages/loginPage.cy.js"
import projectsPage from "../../pages/projectsPage.cy.js"
import selectedProjectsPage from "../../pages/selectedProjectPage.cy.js"

Then("Project Characteristics can be seen", () => {
  cy.wait(4500)
  selectedProjectsPage.projectInformation.projectCharacteristics()
})

Then("The request has expanded to reveal actions", () => {
  selectedProjectsPage.selectedRequest.actions()
})

When("I select the {string} nav button in my project", (button) => {
  switch (button) {
    case "requests":
      cy.wait(2500)
      selectedProjectsPage.internalNavbar.requests().click()
      break
    case "project vpas":
      selectedProjectsPage.internalNavbar.projectVPAs().click()
      break
    case "risk":
      selectedProjectsPage.internalNavbar.risk().click()
      break
    default:
      throw new Error(`Button ${button} not found on projects page`)
  }
})

Then("I can see list of requests or area for requests", () => {
  cy.get("div.requests-view")
})

When("I select {string} from the request view", (object) => {
  switch (object) {
    case "create new request":
      selectedProjectsPage.requestListView.createNewRequest().click()
      break
    case "preliminary review":
      selectedProjectsPage.requestListView.generic().click()
      break
    default:
      throw new Error(`Button ${button} not found on projects page`)
  }
})

And("I select and expand the whole list of available projects", () => {
  cy.get('svg[data-testid="ChevronRightIcon"]').click({ force: true })
  cy.get('svg[data-testid="ChevronRightIcon"]').click({ force: true })
})

Then("I can see my {string} id in my vpas list", (type) => {
  selectedProjectsPage.locateListedGsidFromFixture("REGULARCASE").then((value) => {
    cy.get("p").contains(value)
  })
})

And("I select the search bar on project vpas page", () => {
  cy.get('svg[data-testid="ChevronRightIcon"]').click({ force: true })
})

And("I type my regular case GSID into vpas search bar", () => {
  selectedProjectsPage.locateListedGsidFromFixture("REGULARCASE").then((value) => {
    selectedProjectsPage.projectVpasView.searchBar().click().type(value)
  })
})

import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import selectedProjectsPage from "../../pages/selectedProjectPage.cy.js"
import allRequestDashboard from "../../pages/allRequestDashboardPage.cy.js"
import viewAssignmentsModal from "../../pages/wizardsAndModals/viewAssignmentsModal.cy.js"
import myRequestDashboard from "../../pages/myRequestDashboardPage.cy.js"

When("I type {string} {string} GSID into search filter on {string} request dashboard", (status, project, dashboardType) => {
  cy.readFile("cypress/fixtures/platformInfo.json").then((data) => {
    const gsids = {
      Listed: {
        "automation test (POA)": data["Listed POA GSID"],
        "automation test (standalone project)": data["Listed Standalone GSID"],
        "automation test (REGULARCASE)": data["Listed REGULARCASE GSID"],
        "automation test (REALCASE)": data["Listed REALCASE GSID"]
      },
      New: {
        "automation test (POA)": data["New POA GSID"],
        "automation test (REGULARCASE)": data["New REGULARCASE GSID"],
        "automation test (REALCASE)": data["New REALCASE GSID"],
        "automation test (standalone project)": data["New Standalone GSID"]
      }
    }

    const gsid = gsids[status][project]
    cy.log("Located: " + gsid)

    if (dashboardType == "all") {
      allRequestDashboard.filters.projectNameOrGsid().type(gsid)
    } else {
      myRequestDashboard.filters.projectNameOrGsidInput().type(gsid)
    }
  })
})

When("I select request link {string} on all request dashboard", (request) => {
  cy.get("a").contains(request).invoke("removeAttr", "target").click()
})

When("I select view assignments button from request actions", () => {
  selectedProjectsPage.selectedRequest.actionsArea.viewAssignments().click()
})

And("I can see {string} header in assignment modal", (projectHeader) => {
  cy.get("h2").contains(projectHeader)
  cy.wait(1000)
})

And("I assign the {string} to collect fee", (name) => {
  viewAssignmentsModal.accounts.financeUser().click().type(name)
  cy.get("li").contains(name).click()
  viewAssignmentsModal.assignUser.saveAssignment()
})

Then("I can see assignments updated toast popup", () => {
  cy.get("div").contains("Assignments updated")
})

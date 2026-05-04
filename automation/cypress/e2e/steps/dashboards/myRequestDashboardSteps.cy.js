import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import selectedProjectsPage from "../../pages/selectedProjectPage.cy.js"
import myRequestDashboard from "../../pages/myRequestDashboardPage.cy.js"

When("I select {string} from my requests dashboard page", (selection) => {
  switch (selection) {
    case "Name or Exact GSID":
      myRequestDashboard.filters.projectNameOrGSID().click({ force: true })
      break
    case "request status (all requests)":
      myRequestDashboard.filters.status().click()
      break
    default:
      throw new Error(`${object} not found`)
  }
})

And("I clear the my requests dashboard request status filter as {string}", (user) => {
  cy.wait(1000)
  myRequestDashboard.filters.statusTypeB().click()
  myRequestDashboard.filters
    .statusInputB()
    .click()
    .type("{backspace}{backspace}{backspace}{backspace}{backspace}{esc}{esc}{esc}{esc}{esc}")
})

When("I select request link from my {string} from column {string}", (project, index) => {
  let firstClick = false

  cy.get("tbody")
    .find("tr")
    .each(($row) => {
      if (firstClick) return

      const cellText = $row.find("td").eq(1).text().trim()
      if (cellText.includes(project)) {
        cy.wrap($row).find("td").eq(index).find("a").invoke("removeAttr", "target").click()
        firstClick = true
      }
    })
})

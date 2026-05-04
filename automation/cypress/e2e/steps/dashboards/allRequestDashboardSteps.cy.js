import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import allRequestDashboard from "../../pages/allRequestDashboardPage.cy.js"
import selectedProjectsPage from "../../pages/selectedProjectPage.cy.js"

When("I select {string} from all requests dashboard page", (selection) => {
  switch (selection) {
    case "Name or Exact GSID":
      allRequestDashboard.filters.projectNameOrGsid().click()
      break
    case "rows per page":
      allRequestDashboard.rowsPerPage.rowsDropdown().click()
      break
    case "developer filter (all requests)":
      allRequestDashboard.filters.developerButton().click()
      break
    case "request type (all requests)":
      allRequestDashboard.filters.requestType().click()
      break
    case "request status (all requests)":
      allRequestDashboard.filters.status().click()
      break
    case "reviewer filter (all requests)":
      allRequestDashboard.filters.reviewer().click()
      break
    case "vvb filter (all requests)":
      allRequestDashboard.filters.vvb().click()
      break
    case "qaqc filter (all requests)":
      allRequestDashboard.filters.qaqc().click()
      break
    default:
      throw new Error(`${object} not found`)
  }
})

When("I click the {string} GSID from request list", (choice) => {
  let projectMaster
  if (choice == "automation test (POA)") {
    projectMaster = "POA"
  } else {
    throw new Error(`${object}!! Create new tests here only POA is covered`)
  }

  cy.readFile("cypress/fixtures/platformInfo.json").then((data) => {
    const gsidKey = `New ${projectMaster} GSID`
    const gsidValue = data[gsidKey]

    allRequestDashboard.link.linkToProject(gsidValue).invoke("removeAttr", "target").click()
  })
})

Then("project information can be seen", () => {
  selectedProjectsPage.projectInformation.projectCharacteristics()
})

Then("request information can be seen", () => {
  selectedProjectsPage.selectedRequest.actions()
})

When("I select request link from my {string} request list row", (choice) => {
  let firstClick = false

  cy.get("tbody")
    .find("tr")
    .each(($row) => {
      if (firstClick) return

      const cellText = $row.find("td").eq(1).text().trim()
      if (cellText.includes(choice)) {
        cy.wrap($row).find("td").eq(3).find("a").invoke("removeAttr", "target").click()
        firstClick = true
      }
    })
})

Then("{string} {string} is displayed in {string} index of table", (input, dataPoint, index) => {
  cy.wait(1000)

  let found = false

  cy.get("tbody")
    .within(() => {
      cy.get("tr").each(($row) => {
        cy.wrap($row)
          .find("td")
          .eq(index)
          .then(($cell) => {
            if ($cell.text().includes(`${input}`)) {
              found = true
            }
          })
      })
    })
    .then(() => {
      expect(found).to.be.true
    })
})

And("I select {string} from all request dashboard filter dropdown", (choice) => {
  allRequestDashboard.filterDropdowns.choiceDropdown(choice).click()
})

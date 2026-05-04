class _MyRequestDashboard {
  filters = {
    developerButton: () => cy.get("button").contains(`Developer`),
    developerInput: () => cy.get('input[id="projectAccountName"]'),
    requestType: () => cy.get("button").contains(`Request Type`),
    requestInput: () => cy.get('input[id="requestName"]'),

    statusTypeA: () => cy.get("button").contains(`Request Status`), //VVB Button
    statusTypeB: () => cy.get("button").contains(`Status`),
    statusInputA: () => cy.get('input[id="status-filter"]'),
    statusInputB: () => cy.get('input[id="request-status-filter"]'), //PD input

    project: () => cy.get("button").contains(/^Project$/),
    projectNameOrGSID: () => cy.get("label").contains("Name or exact GSID"),
    projectNameOrGsidInput: () => cy.get(`input[data-testid="global-search-input"]`)
  }

  filterDropdowns = {
    choiceDropdown: (x) => cy.get("li").contains(new RegExp(`^${x}$`))
  }

  rowsPerPage = {
    rowsDropdown: () => cy.get(`div[aria-label="Rows per page"]`),
    numberOfRows: (x) => cy.get(`li[data-value="${x}"]`)
  }

  link = {
    linkToProject: (x) => cy.get(`a`).contains(`${x}`),
    linkToRequest: (x) => cy.get(`a`).contains(`${x}`)
  }

  pageElements = {
    gsidHeader: () => cy.get(`th`).contains(`GSID`)
  }

  searchPaginatedTableForRequest(project, requestName, data, requestIndex) {
    const projectValue = this.determineProjectName(project, data)
    const gsidValue = data[`New ${project} GSID`]

    let found = false

    const checkRows = () => {
      return cy
        .get("tbody")
        .find("tr")
        .each(($row) => {
          const $tds = Cypress.$($row).find("td")
          const gsidText = $tds.eq(0).find("a").text().trim()
          const projectText = $tds.eq(1).text().trim()

          if (gsidText === gsidValue && projectText === projectValue) {
            const $requestLink = $tds
              .eq(requestIndex)
              .find("a")
              .filter((_, el) => Cypress.$(el).text().trim() === requestName)

            if ($requestLink.length > 0) {
              found = true
              cy.wrap($requestLink).invoke("removeAttr", "target").click()
              return false
            }
          }
        })
    }

    checkRows().then(() => {
      if (!found) {
        cy.get('[data-testid="page-2"]').then(($btn) => {
          if ($btn.length > 0) {
            cy.wrap($btn).click()
            cy.wait(500)
            checkRows()
          } else {
            cy.log("Request not found and no second page available.")
          }
        })
      }
    })
  }

  determineProjectName(project, data) {
    const runNumber = data["Run Number"]
    const poaGSID = data["New POA GSID"]
    const realCaseGSID = data["New REALCASE GSID"]

    switch (project) {
      case "Standalone":
        return `automation test (standalone project) run:${runNumber}`
      case "POA":
        return `automation test (${project}) run:${runNumber}`
      case "REALCASE":
        return `${poaGSID}-automation test (${project}) run:${runNumber}`
      case "REGULARCASE":
        return `${poaGSID}-${realCaseGSID}-automation test (${project}) run:${runNumber}`
      default:
        throw new Error(`Unknown project type: ${project}`)
    }
  }

  filterPageForRequestLink(project, requestName, requestIndex) {
    cy.readFile("cypress/fixtures/platformInfo.json").then((data) => {
      const projectValue = this.determineProjectName(project, data)

      this.filters.projectNameOrGSID().parent().find("input").click().type(`${projectValue}{enter}`, { delay: 100 })

      cy.wait(2500)
      this.searchPaginatedTableForRequest(project, requestName, data, requestIndex)
    })
  }
}

const myRequestDashboard = new _MyRequestDashboard()
export default myRequestDashboard

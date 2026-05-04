class _AllRequestDashboard {
  filters = {
    projectNameOrGsid: () => cy.get('input[placeholder="Name or Exact GSID"]'),
    developerButton: () => cy.get("button").contains(`Developer`),
    developerInput: () => cy.get('input[id="projectAccountName"]'),
    requestType: () => cy.get("button").contains(`Request Type`),
    requestInput: () => cy.get('input[id="requestName"]'),
    status: () => cy.get("button").contains(`Status`),
    statusInput: () => cy.get('input[id="requestStatus"]'),
    reviewer: () => cy.get("button").contains(`Reviewer`),
    reviewerInput: () => cy.get('input[id="reviewerAccountName"]'),
    vvb: () => cy.get("button").contains(`VVB`),
    vvbInput: () => cy.get('input[id="vvbAccountName"]'),
    qaqc: () => cy.get("button").contains(`QAQC`),
    qaqcInput: () => cy.get('input[id="qaqcAccountName"]')
  }

  filterDropdowns = {
    choiceDropdown: (x) => cy.get("li").contains(new RegExp(`^${x}$`))
  }

  rowsPerPage = {
    rowsDropdown: () => cy.get(`div[aria-label="Rows per page"]`),
    numberOfRows: (x) => cy.get(`li[data-value="${x}"]`)
  }

  link = {
    linkToProject: (x) => cy.get(`a`).contains(`${x}`)
  }
}

const allRequestDashboard = new _AllRequestDashboard()
export default allRequestDashboard

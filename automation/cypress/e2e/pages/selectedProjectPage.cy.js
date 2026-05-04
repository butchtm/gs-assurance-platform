class _SelectedProjectsPage {
  highRiskBanner = {
    highRiskBannerHeader: () => cy.get("h6").contains("High Risk"),
    highRiskBannerSummary: (x) => cy.get("p").contains(x),
    highRiskBannerEdit: () => cy.get('button[data-testid="update-risk"]')
  }

  projectInformation = {
    projectCharacteristics: () => cy.get("div").contains("Project Characteristics")
  }

  requestListView = {
    createNewRequest: () => cy.get("button").contains(/^Create New Request$/),
    prelimReview: () => cy.get("p").contains("Preliminary Review"),
    requestNameHeader: (x) => cy.get('p[data-testid="request-name-key-information"]').contains(`${x}`),
    generic: () => cy.get("p").contains("Generic"),
    wantedRequest: (x) => cy.get("p").contains(x)
  }

  projectVpasView = {
    searchBar: () => cy.get('input[data-testid="vpa-search"]')
  }

  projectRiskView = {
    updateRisk: () => cy.get("button").contains("Update Risk"),
    riskModal: {
      riskToggle: () => cy.get('label[data-testid="project-risk-checkbox"]').find('input[type="checkbox"]'),
      summaryTextBox: () => cy.get('textarea[rows="6"]'),
      updateButton: () => cy.get("button").contains(/^Update$/)
    },
    verifyTableContent: (date, risk, name, summary) => {
      cy.get("table").contains("td", date).should("exist")
      cy.get("table").contains("td", risk).should("exist")
      cy.get("table").contains("td", name).should("exist")
      cy.get("table").contains("td", summary).should("exist")
    }
  }

  selectedRequest = {
    actions: () => cy.get("div").contains("Actions"),
    actionsArea: {
      submitRequest: () => cy.get("button").contains(/^Submit Request$/),
      acceptOrDeclineRequest: () => cy.get("button").contains("Accept / Decline Request"),
      acceptOrDeclineRequestInput: (action) => cy.get("button").contains(new RegExp(`^${action}$`)),
      viewAssignments: () => cy.get("button").contains("View Assignments"),
      conversationButton: (buttonName) => cy.get("button").contains(buttonName)
    }
  }

  internalNavbar = {
    requests: () => cy.get("button").contains(/^Requests$/),
    projectVPAs: () => cy.get("button").contains(/^Project VPAs$/),
    risk: () => cy.get("button").contains(/^Risk$/)
  }

  locateListedGsidFromFixture(project) {
    const gsidKey = `Listed ${project} GSID`
    return cy.readFile("cypress/fixtures/platformInfo.json").then((data) => data[gsidKey])
  }
}

const selectedProjectsPage = new _SelectedProjectsPage()
export default selectedProjectsPage

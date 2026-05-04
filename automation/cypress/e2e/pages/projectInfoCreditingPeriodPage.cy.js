class _ProjectInfoCreditingPeriod {
  projectInfo = {
    creditingPeriodOne: () => cy.get("td").contains("Period 1")
  }

  expectedUrl = {
    creditingPeriodOne: () => cy.url().should("include", "/creditingPeriods/1"),
    platformAccount: () => cy.url().should("include", "/account/1")
  }

  pageElements = {
    methodologiesHeader: () => cy.get("h6").contains("Methodologies"),
    methodologyOne: () => cy.get("td").contains("ACM0001 Flaring or use of landfill gas"),
    methodologyTwo: () => cy.get("td").contains("ACM0002 Grid-connected electricity generation from renewable sources"),
    methodologyLinks: (x) => cy.get("a").contains(`${x}`).invoke("removeAttr", "target")
  }

  openedRequest = {
    requestHeaderName: (x) => cy.get("p").contains(`${x}`),
    requestStatusHeader: () => cy.get("div").contains("Status")
  }

  openedAccount = {
    accountHeaderName: (x) => cy.get("div").contains(`${x}`),
    accountDetailsHeader: () => cy.get("h3").contains("Account Details")
  }
}

const projectInfoCreditingPeriod = new _ProjectInfoCreditingPeriod()
export default projectInfoCreditingPeriod

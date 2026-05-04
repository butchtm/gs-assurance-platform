class _PlatformAccountsPage {
  buttons = {
    filterByName: () => cy.get("button").contains("Name"),
    filterByType: () => cy.get("button").contains("Type"),
    filterByActive: () => cy.get("button").contains("Active"),
    createAccount: () => cy.get("button").contains("Create")
  }

  openFilter = {
    filterByNameInput: () => cy.get(`input[aria-label="Filter by Name"]`),
    clearInput: () => cy.get(`button[aria-label="Clear filter"]`)
  }

  openTypeFilter = {
    filterByTypeInput: () => cy.get(`input[aria-label="Filter by Type"]`),
    typeVVB: () => cy.get("li").contains("VVB"),
    typeTAC: () => cy.get("li").contains("TAC"),
    typeReviewer: () => cy.get("li").contains("Reviewer"),
    typeDeveloper: () => cy.get("li").contains("Project Developer"),
    typeFinance: () => cy.get("li").contains("Gold Standard Finance")
  }

  rowsPerPage = {
    rowsDropdown: () => cy.get(`div[aria-label="Rows per page"]`),
    numberOfRows: (x) => cy.get(`li[data-value="${x}"]`)
  }

  openPlatformAccount = {
    editAccountDetails: () => cy.get(`button[aria-label="Edit account details"]`),
    editUserDetails: () => cy.get(`button[aria-label="Edit account users"]`)
  }

  editPlatformAccount = {
    accountName: () => cy.get(`label`).contains("Account Name").parent().find(`input`)
  }

  createPlatformAccount = {
    accountType: () => cy.get(`label`).contains("Account Type").parent().find(`input`),
    accountName: () => cy.get(`label`).contains("Account Name").parent().find(`input`),
    accountEmail: () => cy.get(`input[type="email"]`),
    accountAddress: () => cy.get(`label`).contains("Main Contact Address").parent().find(`input`),
    typeSelection: (x) => cy.get("li").contains(x),
    createButton: () =>
      cy
        .get(`h2`)
        .contains("Create Account")
        .parent()
        .within(() => {
          cy.get(`button`).contains("Create").click({ force: true })
        })
  }
}

const platformAccountsPage = new _PlatformAccountsPage()
export default platformAccountsPage

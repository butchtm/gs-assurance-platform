class _UsersDashboardPage {
  filters = {
    active: () => cy.get("button").contains(`Active`),
    usernameOrEmail: () => cy.get('input[placeholder="Username or Email"]'),
    lastPageActive: () => cy.get("button").contains("Last"),
    lastPageDisabled: () => cy.get("button[disabled]").contains("Last")
  }

  link = {
    linkToAccount: (x) => cy.get(`a`).contains(`${x}`)
  }
}

const usersDashboardPage = new _UsersDashboardPage()
export default usersDashboardPage

class _FooterBar {
  primaryBar = {
    privacyPolicy: () => cy.get("a").contains("Privacy Policy"),
    teesAndCees: () => cy.get("a").contains("Terms & Conditions"),
    accessability: () => cy.get("a").contains("Accessibility"),
    version: () => cy.get("a").contains("Version")
  }

  expectedUrl = {
    privacyPolicy: () => cy.url().should("include", "/privacy-policy"),
    teesAndCees: () =>
      cy.origin("https://www.goldstandard.org", () => {
        cy.url().should("include", "/terms-and-conditions")
      }),
    accessability: () => cy.url().should("include", "/accessibility-statement")
  }

  versionModal = {
    modalHeader: () => cy.get("h2").contains("Application Versions"),
    uiVersion: () => cy.get("b").contains("UI Version:"),
    apiVersion: () => cy.get("b").contains("API Version:"),
    closeModal: () => cy.get("button").contains("Close")
  }
}

const footerBar = new _FooterBar()
export default footerBar

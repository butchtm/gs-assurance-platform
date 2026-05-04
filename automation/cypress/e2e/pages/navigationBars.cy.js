class _NavigationBars {
  //covers the primary navigation bar located at the top of the page
  primaryBar = {
    pdProjects: () => cy.get('a[href="/project-trees"]').contains("Projects"),
    projects: () => cy.get('a[href="/projects"]').contains("Projects"),
    allRequests: () => cy.get('a[href="/all-requests"]').contains("All Requests"),
    platformAccounts: () => cy.get('a[href="/accounts"]').contains("Platform Accounts"),
    users: () => cy.get('a[href="/users"]').contains("Users"),
    consultations: () => cy.get('a[href="/public-consultations"]').contains("Consultations"),
    myRequest: () => cy.get('a[href="/my-requests"]').contains("My Requests"),
    admin: () => cy.get('a[href="/administration"]').contains("Admin"),
    dashboard: () => cy.get('a[href="/common-dashboard"]').contains("Dashboard"),
    assurancePlatformLogoTitle: () => cy.get('[class="logoTitle"]').contains("Assurance Platform")
  }

  primaryBarSelector(page, user = null) {
    switch (page) {
      case "projects":
        if (user == "PD") {
          this.primaryBar.pdProjects().click()
        } else {
          this.primaryBar.projects().click()
        }
        break
      case "all requests":
        this.primaryBar.allRequests().click()
        break
      case "platform accounts":
        this.primaryBar.platformAccounts().click()
        break
      case "users":
        this.primaryBar.users().click()
        break
      case "consultations":
        this.primaryBar.consultations().click()
        break
      case "my requests":
        this.primaryBar.myRequest().click()
        break
      case "admin":
        this.primaryBar.admin().click()
        break
      case "common-dashboard":
        this.primaryBar.dashboard().click()
        break
      default:
        throw new Error(`Button ${button} not found in primary navbar`)
    }
  }

  primaryConfigureIntercept(method, url, extraBracket) {
    if (extraBracket) {
      cy.intercept(method, `**/${url}/**`).as("apiCheck")
    } else {
      cy.intercept(method, `**/${url}**`).as("apiCheck")
    }
  }
}

const navigationBar = new _NavigationBars()
export default navigationBar

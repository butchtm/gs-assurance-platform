class _ProjectsPage {
  //Refactor into feature group
  createProjectsButton() {
    return cy.get('[type="button"]').contains("Create")
  }

  feature = {
    projectSearchBar: () => cy.get('[data-testid="global-search-input"]')
  }
}

const projectsPage = new _ProjectsPage()
export default projectsPage

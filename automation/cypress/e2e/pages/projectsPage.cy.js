class _ProjectsPage {
  //Refactor into feature group
  createProjectsButton() {
    return cy.get('[type="button"]').contains("Create")
  }

  feature = {
    projectSearchBar: () => cy.get('input[placeholder="Name or Exact GSID"]')
  }
}

const projectsPage = new _ProjectsPage()
export default projectsPage

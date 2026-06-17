import { createMount } from "cypress/react"

class _AdminPage {
  elements = {
    textAfterSelectingAdmin: () => cy.get("h6").contains("Please select an administrative function"),
    collapseSideBar: () => cy.get('[data-testid="close-drawer"]'),
    openSideBar: () => cy.get('[data-testid="open-drawer"]')
  }

  referenceTypes = {
    methodologies: () => cy.get("span").contains("Methodologies"),
    activeMethodology: () => cy.get("span").contains("Active"),
    searchBar: () => cy.get('[data-testid="global-search-input"]'),
    searchResult: (x) => cy.get("td").contains(x),
    searchResultNotExist: (x) => cy.get("td").contains(x).should('not.exist'),
    createNewMethodology: () => cy.get("button").contains("Create"),
    editMethodology: (x) => cy.get("td").contains(x).parent().parent().find('button[data-testid="Edit"]'),
    paaLabelOnMethodology: (x) => cy.get("td").contains(x).parents("tr").find('[data-testid="paa-tag"]').should('exist'),
    paaLabelNotOnMethodology: (x) => cy.get("td").contains(x).parent().find('[data-testid="paa-tag"]').should('not.exist'),
    paaLabelId: () => cy.get('[data-testid="paa-tag"]'),
    filterByPaaButton: () => cy.contains('button', 'PAA'),
    paaFilterSettingPaa: () => cy.get('[id="filter-element-paa"]').find('[data-testid="CheckBoxIcon"]'),
    paaFilterSettingNotPaa: () => cy.get('[id="filter-element-paa"]').find('[data-testid="CheckBoxOutlineBlankIcon"]'),
    paaFilterSettingUnset: () => cy.get('[id="filter-element-paa"]').find('[data-testid="IndeterminateCheckBoxIcon"]'),
    paaFilterIndicator: () => cy.contains('button', 'PAA').siblings().contains('span', '1'),
    paaClickOff: () => cy.get('body').click(0, 0, {force: true}),

    createModal: {
      createMethodologyTitle: () => cy.get("h2").contains("Create Methodology"),
      textInput: (t) => cy.get('textarea[rows="4"]').type(t),
      createButton: () => cy.get('div[role="dialog"]').find("button").contains("Create"),
      paaCheckbox: () => cy.get('[data-testid="methodology-paa-checkbox"]'),
      activeCheckbox: () => cy.get('[data-testid="methodology-active-checkbox"]')
    },

    editModal: {
      editMethodologyTitle: () => cy.get("h2").contains("Edit Methodology"),
      textInput: (t) => cy.get('textarea[rows="4"]').type(t),
      editButton: () => cy.get('div[role="dialog"]').find("button").contains("Edit"),
      deleteButton: () => cy.get('div[role="dialog"]').find("button").contains("Delete"),
      paaCheckbox: () => cy.get('[data-testid="methodology-paa-checkbox"]'),
      activeCheckbox: () => cy.get('[data-testid="methodology-active-checkbox"]')
    },

    confirmDeleteModal: {
      confirmDeleteTitle: () => cy.get("h2").contains("Confirm Delete"),
      deleteButton: () => cy.get("h2").contains("Confirm Delete").parent().find("button").contains("Delete")
    }
  }

  reporting = {
    statusReport: () => cy.get("span").contains("Status Report"),
    totalCompletedRequests: () => cy.get("p").contains("Total Completed Requests")
  }
}

const adminPage = new _AdminPage()
export default adminPage

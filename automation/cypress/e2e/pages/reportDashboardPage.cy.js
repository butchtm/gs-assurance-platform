class _ReportDashboardPage {
  firstReviewStartDateFilter = {
    openFilterButton: () => cy.get("button").contains("First RoR Start Date"),
    clearMinValueButton: () => cy.get('input[aria-label="Min"]').parent().find('svg[data-testid="ClearIcon"]'),
    clearMaxValueButton: () => cy.get('input[aria-label="Max"]').parent().find('svg[data-testid="ClearIcon"]'),
    filterCount: (x) => cy.get("span").contains(x),
    escapeFilter: () => cy.get('input[aria-label="Min"]')
  }

  filters = {
    projectNameOrGSID: () => cy.get('[data-testid="global-search-input"]'),
    requestStatus: () => cy.get("button").contains("Request Status"),
    requestType: () => cy.get("button").contains("Request Type"),
    otherFilterButtons: (x) => cy.get("button").contains(x)
  }

  selectFilterListItems(item) {
    cy.get("li").contains(item).click()
  }

  associateIndex(column) {
    const columnIndexMap = {
      "Request Status": 2,
      "Request Type": 3,
      "RoR Count": 8,
      "Project Classification": 11,
      "Project Type": 12,
      "Invoice Status": 13
    }

    return columnIndexMap[column]
  }
}

const commonDashboard = new _ReportDashboardPage()
export default commonDashboard

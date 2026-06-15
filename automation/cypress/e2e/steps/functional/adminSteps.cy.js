import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import adminPage from "../../pages/adminPage.cy"

When("I collapse the admin side nav area", () => {
  adminPage.elements.collapseSideBar().click()
})

Then("admin side nav bar is collapsed", () => {
  adminPage.elements.openSideBar()
})

When("I expand the admin side nav area", () => {
  adminPage.elements.openSideBar().click()
})

Then("admin side nav bar is expanded", () => {
  adminPage.elements.collapseSideBar()
})

Then("The methodology list is empty", () => {
  cy.contains("No results found").should("be.visible")
})

When("I select methodologies from admin side nav area", () => {
  adminPage.referenceTypes.methodologies().click()
})

Then("I can see a list of active methodologies", () => {
  adminPage.referenceTypes.activeMethodology()
})

Then("The list is currently empty", () => {
  cy.contains("No results found").should("be.visible")
})

When("I select status reporting from admin side nav area", () => {
  adminPage.reporting.statusReport().click()
})

Then("I can see the total completed requests", () => {
  adminPage.reporting.totalCompletedRequests()
})

And("I type {string} into methodologies search bar", (text) => {
  adminPage.referenceTypes.searchBar().type(text)
  cy.wait(500)
})

Then("I can see {string} methodology from the list", (text) => {
  adminPage.referenceTypes.searchResult(text)
})

And("I cannot see {string} methodology on the list", (text) => {
  adminPage.referenceTypes.searchResultNotExist(text)
})

And("I select create to add a new methodology", () => {
  adminPage.referenceTypes.createNewMethodology().click()
})

And("No PAA methodologies show on the list", () => {
  adminPage.referenceTypes.paaLabelId().should('not.exist')
})

And("Only PAA methodologies show on the list", () => {
  cy.get('table tbody tr').each(($tr, index) => {
    cy.wrap($tr)
      .find('[data-testid="paa-tag"]')
      .should('exist')
  })
})

Then("The PAA label on {string} should {string}", (methodology, paaStatus) => {
  if (paaStatus == "exist") {
    adminPage.referenceTypes.paaLabelOnMethodology(methodology)
  } else if (paaStatus == "not exist") {
    adminPage.referenceTypes.paaLabelNotOnMethodology(methodology)
  }
})

When("I select filter by PAA", () => {
  cy.contains("Filter by PAA").click()
  cy.waitForNetworkIdle(2000)
})

When("I click the PAA filter button", () => {
  adminPage.referenceTypes.filterByPaaButton().click()
})

When("I close the PAA filter popup", () => {
  adminPage.referenceTypes.paaClickOff()
})

And("The PAA filter button shows a filter is applied", () => {
  adminPage.referenceTypes.paaFilterIndicator()
})

Then("The PAA filter button is set to {string}", (filterSetting) => {
  if (filterSetting === "unset") {
    adminPage.referenceTypes.paaFilterSettingUnset()
  } else if (filterSetting === "paa") {
    adminPage.referenceTypes.paaFilterSettingPaa()
  } else if (filterSetting === "no paa") {
    adminPage.referenceTypes.paaFilterSettingNotPaa()
  }
})

Then("I can see create methodologies title in modal", () => {
  adminPage.referenceTypes.createModal.createMethodologyTitle()
})

When("I select text area and type {string}", (text) => {
  adminPage.referenceTypes.createModal.textInput(text)
})

And("I select create button which has become active", () => {
  adminPage.referenceTypes.createModal.createButton().click({ force: true })
})

When("I select edit icon from row for {string}", (methodologyText) => {
  adminPage.referenceTypes.editMethodology(methodologyText).click()
})

Then("I can see edit methodologies title in modal", () => {
  adminPage.referenceTypes.editModal.editMethodologyTitle()
})

Then("I can see the PAA checkbox on the modal", () => {
  adminPage.referenceTypes.createModal.paaCheckbox().should('be.visible')
})

Then("The PAA checkbox is currently {string}", (statusText) => {
  adminPage.referenceTypes.createModal.paaCheckbox().within(() => {
    
    if (statusText == "checked") {
      cy.get('[data-testid="CheckBoxIcon"]')
    } else if (statusText == "unchecked") {
      cy.get('[data-testid="CheckBoxOutlineBlankIcon"]')
    }
  })
})

And("The Active checkbox is currently {string}", (activeText) => {
    adminPage.referenceTypes.createModal.activeCheckbox().within(() => {
    
    if (activeText == "checked") {
      cy.get('[data-testid="CheckBoxIcon"]')
    } else if (activeText == "unchecked") {
      cy.get('[data-testid="CheckBoxOutlineBlankIcon"]')
    }
  })
})

When("I click the PAA checkbox", () => {
  adminPage.referenceTypes.createModal.paaCheckbox().click()
})

When("I click the Active checkbox", () => {
  adminPage.referenceTypes.createModal.activeCheckbox().click()
})

When("I select delete button from edit methodology modal", () => {
  adminPage.referenceTypes.editModal.deleteButton().click()
})

And("A delete confirmation modal is displayed", () => {
  adminPage.referenceTypes.confirmDeleteModal.confirmDeleteTitle()
})

Then("I confirm I want to delete methodology", () => {
  adminPage.referenceTypes.confirmDeleteModal.deleteButton().click()
})

And("I click the Edit button", () => {
  adminPage.referenceTypes.editModal.editButton().click()
})

And("I use the API to clear all test methodologies", () => {
  cy.deleteTestMethodologiesApi()
})
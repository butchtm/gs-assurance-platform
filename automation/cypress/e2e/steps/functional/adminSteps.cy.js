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

When("I select methodologies from admin side nav area", () => {
  adminPage.referenceTypes.methodologies().click()
})

Then("I can see a list of active methodologies", () => {
  adminPage.referenceTypes.activeMethodology()
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

And("I select create to add a new methodology", () => {
  adminPage.referenceTypes.createNewMethodology().click()
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

When("I select delete button from edit methodology modal", () => {
  adminPage.referenceTypes.editModal.deleteButton().click()
})

And("A delete confirmation modal is displayed", () => {
  adminPage.referenceTypes.confirmDeleteModal.confirmDeleteTitle()
})

Then("I confirm I want to delete methodology", () => {
  adminPage.referenceTypes.confirmDeleteModal.deleteButton()
})

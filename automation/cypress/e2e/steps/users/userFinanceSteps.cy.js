import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import openProjectOpenRequest from "../../pages/openProjectRequestListPage.cy"

When("I select request link {string} on finance my request dashboard", (request) => {
  cy.get("a").contains(request).invoke("removeAttr", "target").click()
})

When("I select create invoice button from request actions", () => {
  openProjectOpenRequest.actionsArea.createInvoice().click()
})

And("I can see invoice created toast popup", () => {
  cy.get("div").contains("Invoice Created Successfully")
})

And("I can see invoice status in request status area", () => {
  openProjectOpenRequest.openRequest.invoiceStatusField()
})

Then("Invoice status reads as {string}", (invoiceStatus) => {
  openProjectOpenRequest.openRequest.invoiceStatusCurrent(invoiceStatus)
})

When("I select update invoice status from request actions", () => {
  openProjectOpenRequest.actionsArea.updateInvoice().click()
})

And("I enter {string} as my invoice amount", (invoiceAmount) => {
  openProjectOpenRequest.updateInvoiceModal.invoiceAmount().click().type(invoiceAmount)
})

And("I can save my invoice update", () => {
  openProjectOpenRequest.updateInvoiceModal.saveButton().click()
})

And("I can see invoice updated toast popup", () => {
  cy.get("div").contains("Invoice Updated Successfully")
})

And("Invoice amount shows as {string} in request status area", (invoiceAmount) => {
  openProjectOpenRequest.openRequest.invoiceAmount(invoiceAmount)
})

And("I enter {string} as my invoice status", (invoiceStatus) => {
  openProjectOpenRequest.updateInvoiceModal.invoiceStatus().click({ force: true }).type("{downarrow}")
  cy.get(`li`).contains(invoiceStatus).click()
})

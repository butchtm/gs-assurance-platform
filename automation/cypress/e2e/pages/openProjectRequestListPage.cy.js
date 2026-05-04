class _OpenProjectOpenRequest {
  openRequest = {
    requestName: (x) => cy.get(`p`).contains(`${x}`),
    consultation: () => cy.get(`span`).contains(`In Stakeholder Consultation`),
    invoiceStatusField: () => cy.get("div").contains("Invoice Status"),
    invoiceStatusCurrent: (x) => cy.get("p").contains(`${x}`),
    invoiceAmount: (x) => cy.get("p").contains(`${x}`)
  }

  actionsArea = {
    addComment: () => cy.get(`button[data-testid="add-comment-button"]`),
    createInvoice: () => cy.get("button").contains("Create Invoice"),
    updateInvoice: () => cy.get("button").contains("Update Invoice Status")
  }

  newCommentModal = {
    accountType: (x) => cy.get(`p`).contains(`${x}`),
    addCommentHeader: () => cy.get(`h2`).contains(`Add Comment`),
    commentFieldSet: () => cy.get(`textarea[data-testid="comment-input"]`),
    checkForPii: () => cy.get(`button`).contains(`Check For Pii`),
    submitComment: () => cy.get(`button`).contains(`Submit Comment`),
    commentSuccess: () => cy.get(`div`).contains(`Commented Successfully`)
  }

  updateInvoiceModal = {
    invoiceAmount: () => cy.get(`input[data-testid="invoice-amount-input"]`),
    saveButton: () => cy.get("button").contains("Save"),
    invoiceStatus: () => cy.get('div[data-testid="update-invoice-status-select"]')
  }
}

const openProjectOpenRequest = new _OpenProjectOpenRequest()
export default openProjectOpenRequest

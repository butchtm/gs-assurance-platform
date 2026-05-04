class _ConsultationPage {
  //covers the primary navigation bar located at the top of the page
  filters = {
    collapseAllConsultations: () => cy.get('[data-testid="consultation-expand-all-button"]')
  }

  tableDisplay = {
    methodologyVersion: () => cy.get("td").contains("Version")
  }

  openConsultation = {
    requestHeader: () => cy.get("h5").contains("Request"),
    documentsHeader: () => cy.get("div").contains("Documents"),
    commentsHeader: () => cy.get("div").contains("Comments"),
    projectHeader: () => cy.get("h5").contains("Project"),
    addComment: () => cy.get("button").contains("Add Comment")
  }

  addCommentModal = {
    addCommentHeader: () => cy.get("h2").contains("Add Comment"),
    name: () => cy.get('[data-testid="comment-name-input"]'),
    email: () => cy.get('[data-testid="valid-email-text-field"]'), //hmm - odd field
    comment: () => cy.get('[data-testid="CommentTextField"]'),
    submitComment: () => cy.get("button").contains("Submit Comment"),
    checkPii: () => cy.get("button").contains("Check For Pii"),
    noPiiFound: () => cy.get("p").contains("No PII identified"),
  }

  popupMessages = {
    successComment: () => cy.get("Div").contains("Comment added successfully, it will show publicly upon review.")
  }
}

const consultationPage = new _ConsultationPage()
export default consultationPage

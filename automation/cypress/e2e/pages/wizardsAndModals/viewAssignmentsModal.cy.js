class _RequestAssignmentEngine {
  buttons = {
    save: () => cy.get("button").contains("Save")
  }

  //elements
  accounts = {
    reviewerUser: () => cy.get('[id="acc-assig-type-1"]'),
    financeUser: () => cy.get('[id="acc-assig-type-3"]'),
    qaqcUser: () => cy.get('[id="acc-assig-type-2"]'),
    vvbUser: () => cy.get('[id="acc-assig-type-0"]')
  }

  //functions
  assignUser = {
    assignReviewer() {
      viewAssignmentsModal.accounts.reviewerUser().click().type("Reviewer")
      cy.get("li").contains("Reviewer").click()
    },

    assignQaQc() {
      viewAssignmentsModal.accounts.qaqcUser().click().type("GS ARM Team")
      cy.get("li").contains("GS ARM Team").click()
    },

    assignVVB() {
      viewAssignmentsModal.accounts.vvbUser().click().type("VVB")
      cy.get("li").contains(/^VVB$/).click()
      cy.get('[id="assig-status-assig-type-0"]').click().type("{downarrow}")
      cy.get("li").contains("Accepted").click()
    },

    saveAssignment() {
      viewAssignmentsModal.buttons.save().click()
      cy.wait(500)
    }
  }
}

const viewAssignmentsModal = new _RequestAssignmentEngine()
export default viewAssignmentsModal

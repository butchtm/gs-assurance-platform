import viewAssignmentsModal from "./viewAssignmentsModal.cy.js"
import selectedProjectsPage from "../selectedProjectPage.cy.js"

class _RequestSubmissionEngine {
  //Variables & locators
  requestSubmissionWizard = {
    //Button on every submission page
    submitAnswer: () => cy.get("button").contains(/^Submit$/), //Requests that don't require VVB

    //do not like this file in general, prefer a general question list.
    questionList: {
      isThisRequestComplete: () => cy.get("label").contains("Is this Request Complete?")
    },

    //In Prep to In Audit
    submitToAudit: {
      selectUserGroupField: () => cy.get("label").contains("Select VVB"), //Requests that do require VVB
      selectUserGroupDropdown: () => cy.get("li").contains("VVB") //From dropdown triggered by the above
    },

    //Completeness Check to In Assignment
    submitToAssignment: {
      isThisRequestComplete: () => cy.get("label").contains("Is this Request Complete?"),
      isProjectPiiReviewed: () => cy.get(`label[data-testid="request-pii-issue-yes"]`),
      startStakeholderConsultation: () => cy.get('label[data-testid="stakeholder-consultation-checkbox"] input[type="checkbox"]')
    },

    //In Assignment to In Progress and In Assignment to In Round depedning on request type
    submitBeyondAssignment: {
      hasEverythingBeenAssigned: () => cy.get("label").contains("Has everything been assigned?")
    },

    submitToRoundComplete: {
      hasTheReviewBeenCompleted: () => cy.get("label").contains("Has the review been completed?")
    },

    submitBeyondRoundComplete: {
      isReviewComplete: () => cy.get("label").contains("Is Review Complete?"),
      doesThisRequestHaveAnyOpenFindings: () => cy.get("label").contains("Does this request have any open findings?"),
      updateProjectStatus: () => cy.get("label").contains("Does this request have any open findings?"),
      rejectThisRequest: () => cy.get("label").contains("Reject this request?")
    },

    confirmSubmission: { 
      confirmationHeader: () => cy.get("h2").contains("Confirm Request Approved"),
      selectConfirmButton: () => cy.get('button[data-testid="confirm-complete-request-button"]')
    }
  }

  ////////////////////////////
  //Functions & UI actions
  pdPaths = {
    noAuditRequired() {
      cy.log("Click Submit to continue")
    },

    yesAuditRequired() {
      cy.wait(1500)
      submissionModal.requestSubmissionWizard.submitToAudit.selectUserGroupField().click({ force: true }).type("{downarrow}")
      submissionModal.requestSubmissionWizard.submitToAudit.selectUserGroupDropdown().click({ force: true })
      cy.wait(1500)
    }
  }

  vvbPaths = {
    submitToNextStage() {
      cy.log("Click Submit to continue")
    }
  }

  armTeamPaths = {
    submitCompletenessCheckFailed() {
      cy.wait(500)
      submissionModal.requestSubmissionWizard.questionList
        .isThisRequestComplete()
        .parent()
        .find('input[name="is-complete"][value="false"]')
        .should("exist")
    },

    submitToAssignment() {
      submissionModal.requestSubmissionWizard.submitToAssignment
        .isThisRequestComplete()
        .parent()
        .find('input[name="is-complete"][value="true"]')
        .click()
    },

    submitToAssignmentWithConsultation() {
      submissionModal.requestSubmissionWizard.submitToAssignment
        .isThisRequestComplete()
        .parent()
        .find('input[name="is-complete"][value="true"]')
        .click()

      submissionModal.requestSubmissionWizard.submitToAssignment.startStakeholderConsultation().click()
    },

    submitToAssignmentWithPiiCheck() {
      submissionModal.armTeamPaths.submitToAssignment()
      //submissionModal.requestSubmissionWizard.submitToAssignment.isProjectPiiReviewed().find("input").click()
    },

    fastTrackPrelimReviewToRoundEnd() {
      submissionModal.requestSubmissionWizard.submitBeyondAssignment
        .hasEverythingBeenAssigned()
        .parent()
        .find('input[name="assigned"][value="true"]')
        .click()
    },

    submitToInProgress() {
      submissionModal.requestSubmissionWizard.submitBeyondAssignment
        .hasEverythingBeenAssigned()
        .parent()
        .find('input[name="assigned"][value="true"]')
        .click()
    },

    submitToCompletedApproved(x) {
      submissionModal.requestSubmissionWizard.submitBeyondRoundComplete
        .isReviewComplete()
        .parent()
        .find('input[name="review-complete"][value="true"]')
        .click()

      /* -- Probably not needed unless a project has PII the ci/cd has no PII 
      if (x == "prelim review") {
        submissionModal.requestSubmissionWizard.submitToAssignment.isProjectPiiReviewed().find("input").click()
      } */

      submissionModal.requestSubmissionWizard.submitBeyondRoundComplete
        .doesThisRequestHaveAnyOpenFindings()
        .parent()
        .find('input[name="request-issue"][value="false"]')
        .click()
    }
  }

  reviewerPaths = {
    submitToRoundComplete() {
      submissionModal.requestSubmissionWizard.submitToRoundComplete
        .hasTheReviewBeenCompleted()
        .parent()
        .find('input[name="review-completed"][value="true"]')
        .click()
    }
  }
}

const submissionModal = new _RequestSubmissionEngine()
export default submissionModal

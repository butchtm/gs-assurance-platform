import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import selectedProjectsPage from "../../pages/selectedProjectPage.cy.js"
import submissionModal from "../../pages/wizardsAndModals/requestSubmissionModal.cy.js"
import myRequestDashboard from "../../pages/myRequestDashboardPage.cy.js"
import viewAssignmentsModal from "../../pages/wizardsAndModals/viewAssignmentsModal.cy.js"

When("I am {string} who wants to submit a {string} on a {string}", (username, request, project) => {
  cy.loginAsUser(username)
  cy.wait(2500)

  switch (username) {
    case "ARM TEAM":
      myRequestDashboard.filterPageForRequestLink(project, request, 3)
      break
    case "VVB":
    case "REVIEWER":
    case "PD":
      /*
      myRequestDashboard.filterUsingGSID(project)
      cy.wait(2500)

      myRequestDashboard.locateRequestFromDashboard(request).invoke("removeAttr", "target").click({ force: true })
      cy.wait(2500) */
      myRequestDashboard.filterPageForRequestLink(project, request, 2)
      break
    default:
      throw new Error(`${username} or ${request} or ${project} not found`)
  }
})

When("I select submit request as {string} for {string} going to {string}", (username, request, targetStatus) => {
  if (username == "VVB" || username == "REVIEWER") {
    cy.wait(3000)
    cy.get("body")
      .then(($body) => {
        // Check for visibility without causing test failure if not found
        const $btn = $body.find('button:contains("Accept / Decline Request"):visible')

        if ($btn.length) {
          selectedProjectsPage.selectedRequest.actionsArea.acceptOrDeclineRequest().click()
          selectedProjectsPage.selectedRequest.actionsArea.acceptOrDeclineRequestInput("Accept").click()

          cy.wait(2500) 
        }
        // If not visible, do nothing here; we still submit below.
      })
      .then(() => {
        // Default step ALWAYS happens
        selectedProjectsPage.selectedRequest.actionsArea.submitRequest().click()
      })
  } else if (username == "ARM TEAM" && targetStatus == "In Progress") {
    selectedProjectsPage.selectedRequest.actionsArea.viewAssignments().click()
    cy.wait(3000)
    viewAssignmentsModal.assignUser.assignReviewer()
    viewAssignmentsModal.assignUser.saveAssignment()
    cy.wait(250)
    selectedProjectsPage.selectedRequest.actionsArea.submitRequest().click()
  } else {
    selectedProjectsPage.selectedRequest.actionsArea.submitRequest().click()
  }
})

And("{string} selects my answers for {string} going to {string}", (username, request, target) => {
  const requestHandlers = {
    "PD": {
      "Completeness Check": {
        "Preliminary Review no. 1": () => submissionModal.pdPaths.noAuditRequired(),
        "Inclusion | Regular Case VPA no. 1": () => submissionModal.pdPaths.noAuditRequired(),
        "Manage Information | Project or PoA or VPA no. 1": () => submissionModal.pdPaths.noAuditRequired(),
        "Decertification | Project no. 1": () => submissionModal.pdPaths.noAuditRequired()
      },
      "In Audit": {
        "Design Change | With VVB no. 1": () => submissionModal.pdPaths.yesAuditRequired(),
        "Design Certification | Project no. 1": () => submissionModal.pdPaths.yesAuditRequired(),
        "Combined Design and Performance Certification | Project no. 1": () => submissionModal.pdPaths.yesAuditRequired(),
        "Performance Certification | Project no. 1": () => submissionModal.pdPaths.yesAuditRequired()
      }
    },
    "VVB": {
      "Completeness Check": {
        "Design Change | With VVB no. 1": () => submissionModal.vvbPaths.submitToNextStage(),
        "Design Certification | Project no. 1": () => submissionModal.vvbPaths.submitToNextStage(),
        "Combined Design and Performance Certification | Project no. 1": () => submissionModal.vvbPaths.submitToNextStage()
      }
    },
    "ARM TEAM": {
      "In Assignment": {
        "Design Change | With VVB no. 1": () => submissionModal.armTeamPaths.submitToAssignment(),
        "Preliminary Review no. 1": () => submissionModal.armTeamPaths.submitToAssignment(),
        "Design Certification | Project no. 1": () => submissionModal.armTeamPaths.submitToAssignment(),
        "Combined Design and Performance Certification | Project no. 1": () => submissionModal.armTeamPaths.submitToAssignment(),
        "Inclusion | Regular Case VPA no. 1": () => submissionModal.armTeamPaths.submitToAssignment(),
        "Manage Information | Project or PoA or VPA no. 1": () => submissionModal.armTeamPaths.submitToAssignment(),
        "Performance Certification | Project no. 1": () => submissionModal.armTeamPaths.submitToAssignment(),
        "Decertification | Project no. 1": () => submissionModal.armTeamPaths.submitToAssignment()
      },
      "In Progress": {
        "Design Change | With VVB no. 1": () => submissionModal.armTeamPaths.submitToInProgress(),
        "Design Certification | Project no. 1": () => submissionModal.armTeamPaths.submitToInProgress(),
        "Combined Design and Performance Certification | Project no. 1": () => submissionModal.armTeamPaths.submitToInProgress(),
        "Inclusion | Regular Case VPA no. 1": () => submissionModal.armTeamPaths.submitToInProgress(),
        "Manage Information | Project or PoA or VPA no. 1": () => submissionModal.armTeamPaths.submitToInProgress(),
        "Performance Certification | Project no. 1": () => submissionModal.armTeamPaths.submitToInProgress(),
        "Decertification | Project no. 1": () => submissionModal.armTeamPaths.submitToInProgress()
      },
      //For requests that skip review
      "In Round Completion": {
        "Preliminary Review no. 1": () => submissionModal.armTeamPaths.fastTrackPrelimReviewToRoundEnd()
      },
      //End goal 1
      "Completed - Approved": {
        "Preliminary Review no. 1": () => submissionModal.armTeamPaths.submitToCompletedApproved("prelim review"),
        "Design Change | With VVB no. 1": () => submissionModal.armTeamPaths.submitToCompletedApproved()
      }
    },
    "REVIEWER": {
      //most requests need a reviewer to go from in progress to in round completion
      "In Round Completion": {
        "Design Change | With VVB no. 1": () => submissionModal.reviewerPaths.submitToRoundComplete(),
        "Design Certification | Project no. 1": () => submissionModal.reviewerPaths.submitToRoundComplete(),
        "Combined Design and Performance Certification | Project no. 1": () =>
          submissionModal.reviewerPaths.submitToRoundComplete(),
        "Inclusion | Regular Case VPA no. 1": () => submissionModal.reviewerPaths.submitToRoundComplete(),
        "Manage Information | Project or PoA or VPA no. 1": () => submissionModal.reviewerPaths.submitToRoundComplete(),
        "Performance Certification | Project no. 1": () => submissionModal.reviewerPaths.submitToRoundComplete(),
        "Decertification | Project no. 1": () => submissionModal.reviewerPaths.submitToRoundComplete()
      }
    }
  }

  function handleRequest(username, target, request) {
    if (requestHandlers[username][target][request]) {
      requestHandlers[username][target][request]()
    } else {
      console.log("No handler found for this user and request and target")
    }
  }

  handleRequest(username, target, request)
})

When("I submit my answers to go to {string}", (targetStatus) => {
  cy.wait(500)
  submissionModal.requestSubmissionWizard.submitAnswer().click({ force: true })
  if (targetStatus == 'Completed - Approved')
  {
      submissionModal.requestSubmissionWizard.confirmSubmission.confirmationHeader()
      submissionModal.requestSubmissionWizard.confirmSubmission.selectConfirmButton().click()
  }
  cy.wait(2500)
})

Then("I can observe {string} as a tag on {string}", (targetStatus, request) => {
  cy.wait(2500)
  cy.get("p", { timeout: 20000 }).contains(request).parent().find("span", { timeout: 20000 }).contains(targetStatus)
})

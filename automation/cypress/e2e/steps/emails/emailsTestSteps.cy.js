import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import selectedProjectsPage from "../../pages/selectedProjectPage.cy.js"
import submissionModal from "../../pages/wizardsAndModals/requestSubmissionModal.cy.js"
import emailHelper from "../../../support/emailUtilityFunctions.js"
import viewAssignmentsModal from "../../pages/wizardsAndModals/viewAssignmentsModal.cy.js"

And("I open my request {string} from the request view", (request) => {
  selectedProjectsPage.requestListView.wantedRequest(request).click()
})

And("I select the submit button", () => {
  selectedProjectsPage.selectedRequest.actionsArea.submitRequest().click()
})

And("I select submit from submission modal", () => {
  submissionModal.requestSubmissionWizard.submitAnswer().click()
  cy.wait(10000)
})

Then("I can capture my last {string} email notifications", (numberOfNotifications) => {
  cy.getEmailNotificationsApi(numberOfNotifications).then((notifications) => {
    cy.writeFile("cypress/fixtures/caughtEmailNotifications.json", notifications)
  })
})

Then("I check my email notifications {string} for {string} to {string}", (templates, requestTypeSubmission, targetStatus) => {
  cy.readFile("cypress/fixtures/caughtEmailNotifications.json").then((emailsFile) => {
    const emails = emailsFile.body.content
    const keys = templates.split(",").map((k) => k.trim())
    cy.fixture("platformInfo").then((platformInfo) => {
      const runNumber = platformInfo["Run Number"]
      const gsid = platformInfo["Listed POA GSID"]

      cy.fixture("emailTemplates").then((emailTemplates) => {
        keys.forEach((templateKey) => {
          const template = emailTemplates[templateKey]
          if (!template) {
            throw new Error(`Template "${templateKey}" not found in emailTemplates.json`)
          }

          const recipient = emailHelper.determineRecipient(targetStatus, templateKey)
          const feedbackRound = emailHelper.determineFeedbackRound(targetStatus)
          const dynamicLink = emailHelper.determineLink(emails)
          const dynamicLinkPOA = emailHelper.determineLinkPOA(emails)
          const dynamicLinkProjectActivity = emailHelper.determineLinkProjectActivity(emails)
          const reviewDueDate = emailHelper.generateReviewDate()

          const dynamicData = {
            recipient,
            projectName: `automation test (POA) run:${runNumber} (L)`,
            gsid,
            requestType: requestTypeSubmission,
            projectType: "Agriculture",
            feedbackRound,
            dynamicLink,
            dynamicLinkPOA,
            dynamicLinkProjectActivity,
            reviewDueDate,
            projectStatus: "Listed"
            //requestChangelog: changelogData
          }

          const expectedTextBody = emailHelper.fillEmailTemplate(template, dynamicData).trim()
          const expectedJson = JSON.stringify(expectedTextBody)
          const actualTextBody = JSON.stringify(emails)
          cy.writeFile(`cypress/fixtures/${templateKey}emailCompare.json`, `Expected: ${expectedJson} \n\n Received: ${actualTextBody}`)
          
          const matchedEmail = emails.find((email) => email.textBody.startsWith(expectedTextBody))
          

          // Logging for debugging
          cy.task('log',`🔍 Checking template: "${templateKey}"`)
          cy.task('log',`📨 Expected email body:\n${expectedTextBody}`)
          if (matchedEmail) {
            cy.task('log',`✅ Matched email body:\n${matchedEmail.textBody}`)
          } else {
            cy.task('log',`❌ No match found for template "${templateKey}"`)
            cy.task('log',`📬 All caught emails:`)
            emails.forEach((email, index) => {
              cy.task('log',`--- Email ${index + 1} ---\n${email.textBody}`)
            })
          }

          expect(matchedEmail.textBody).to.include(expectedTextBody)
        })
      })
    })
  })
})

Then("I can see the correct {string} header", (requestName) => {
  selectedProjectsPage.requestListView.requestNameHeader(requestName)
})

Then("I set my answers so that completeness check has not passed", () => {
  submissionModal.armTeamPaths.submitCompletenessCheckFailed()
})

Then("I set my answers so that completeness check has passed with consultation", () => {
  submissionModal.armTeamPaths.submitToAssignmentWithConsultation()
})

Then("I select a VVB account to allow me to submit", () => {
  submissionModal.pdPaths.yesAuditRequired()
})

Then("I assign the {string} to perform the review stage", (reviewerAccount) => {
  viewAssignmentsModal.assignUser.assignReviewer(reviewerAccount)
  viewAssignmentsModal.assignUser.saveAssignment()
})

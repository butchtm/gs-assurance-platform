import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import myRequestDashboard from "../../pages/myRequestDashboardPage.cy"
import openProjectOpenRequest from "../../pages/openProjectRequestListPage.cy"

When("I open a combined design request from my request dashboard", () => {
  myRequestDashboard.link.linkToRequest("Combined Design and Performance Certification | Project no. 1").click()
})

Then("An open combined design request with stakeholder consultation can be seen", () => {
  openProjectOpenRequest.openRequest.requestName("Combined Design and Performance Certification | Project no. 1")
  openProjectOpenRequest.openRequest.consultation()
})

When("I can click add comment from the request actions area", () => {
  openProjectOpenRequest.actionsArea.addComment().click()
})

Then("Add comment modal has appeared for TAC user", () => {
  openProjectOpenRequest.newCommentModal.accountType("TAC")
  openProjectOpenRequest.newCommentModal.addCommentHeader()
})

When("I select comment from field text area", () => {
  openProjectOpenRequest.newCommentModal.commentFieldSet().click({ force: true })
})

And("I type {string} into comment field text area", (text) => {
  openProjectOpenRequest.newCommentModal.commentFieldSet().type(text)
})

And("I can check my comment check for pii issues", () => {
  openProjectOpenRequest.newCommentModal.checkForPii().click()
})

Then("I can submit my comment and see success popup", () => {
  openProjectOpenRequest.newCommentModal.submitComment().click()
  openProjectOpenRequest.newCommentModal.commentSuccess()
})

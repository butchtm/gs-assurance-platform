import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import requestConversationModal from "../../pages/wizardsAndModals/requestConversationModal.cy"
import selectedProjectsPage from "../../pages/selectedProjectPage.cy"
import viewAssignmentsModal from "../../pages/wizardsAndModals/viewAssignmentsModal.cy.js"

When("I select the request conversation button {string}", (buttonName) => {
  selectedProjectsPage.selectedRequest.actionsArea.conversationButton(buttonName).click()
})

Then("I can see the conversation modal header which reads {string}", (requestType) => {
  requestConversationModal.openModal.modalHeader(requestType)
})

When("I select close conversation to prevent anyone sending messages", () => {
  requestConversationModal.openModal.closeAndStopConversation().click()
})

And("I can see the confirmation modal to close conversations", () => {
  requestConversationModal.confirmationModal.confirmationClosureHeader()
})

And("I confirm my intent to close conversation to prevent anyone sending messages", () => {
  requestConversationModal.confirmationModal.confirmClosure().click()
})

Then("I can see my conversation button says {string}", (buttonName) => {
  cy.wait(250)
  selectedProjectsPage.selectedRequest.actionsArea.conversationButton(buttonName)
})

When("I select re-open conversation to again allow sending messages", () => {
  requestConversationModal.openModal.reOpenClosedConversation().click()
})

And("I can see the confirmation modal to re-open conversation", () => {
  requestConversationModal.confirmationModal.confirmationReopenHeader()
})

And("I confirm my intent to re-open conversation to prevent anyone sending messages", () => {
  requestConversationModal.confirmationModal.confirmReopening().click()
})

When("I select the rich text area and type {string}", (message) => {
  requestConversationModal.richTextArea.mainTypeArea().click().type(message)
})

And("I select the send message button", () => {
  requestConversationModal.openModal.sendMessage().click()
  cy.wait(250)
})

Then("I can see my {string} in the message header", (displayName) => {
  requestConversationModal.openModal.messageHeader(displayName)
})

And("I can see {string} in the messages", (messageContent) => {
  requestConversationModal.openModal.standardMessageBody(messageContent)
})

When("I select the {string} button to apply style to my text", (style) => {
  requestConversationModal.richTextArea.styleChoice(style).click({ multiple: true, force: true })
})

And("I can see my {string} with 4 styles applied to my message", (messageContent) => {
  requestConversationModal.openModal.boldMessageBody(messageContent)
  requestConversationModal.openModal.italicMessageBody(messageContent)
  requestConversationModal.openModal.underlineMessageBody(messageContent)
  requestConversationModal.openModal.strikeMessageBody(messageContent)
})

And("For later tests I assign an qaqc and vvb users", () => {
  requestConversationModal.openModal.closeModal().click()
  selectedProjectsPage.selectedRequest.actionsArea.viewAssignments().click()
  viewAssignmentsModal.assignUser.assignQaQc()
  viewAssignmentsModal.assignUser.assignVVB()
  viewAssignmentsModal.assignUser.saveAssignment()
})

And("I locate {string} new message label for my {string} row", (messageCount, requestType) => {
  cy.contains("tr", requestType)
    .find('div[aria-label*="new messages"]')
    .should("have.attr", "aria-label", `${messageCount} new messages`)
})

When("I select my {string} link to my request", (requestType) => {
  cy.get("p").contains(requestType).invoke("removeAttr", "target").click()
})

And("I locate {string} new messages in my request header", (messageCount) => {
  cy.contains("span", `${messageCount} New Messages`).should("be.visible")
})

And("I locate {string} in top corner of view conversation button", (messageCount) => {
  cy.wait(250)

  cy.contains("button", "View Active Conversation") // anchor on visible label
    .should("be.visible")
    .parent("span") // the outer badge wrapper
    .children("span") // direct child spans only
    .last() // the numeric badge
    .should("have.text", String(messageCount))
})

Then("I can see {string} as the message sender", (displayName) => {
  requestConversationModal.openModal.messageHeader(displayName)
})

And("I can see the pd and vvb user has read my message", () => {
  cy.wait(250)
  requestConversationModal.openModal.readReceipt("Seen by: VVB, Project Developer")
})

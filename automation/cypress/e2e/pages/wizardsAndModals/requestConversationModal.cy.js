class _requestConversationModal {
  openModal = {
    modalHeader: (requestHeaderName) => cy.get("h2").contains(requestHeaderName),
    closeAndStopConversation: () => cy.get("button").contains(/^Close Conversation$/),
    reOpenClosedConversation: () => cy.get("button").contains(/^Re-Open Conversation$/),
    sendMessage: () => cy.get("button").contains("Send"),
    messageHeader: (displayName) => cy.get("p").contains(displayName),
    standardMessageBody: (messageContent) => cy.get("p").contains(messageContent),
    boldMessageBody: (messageContent) => cy.get("strong").contains(messageContent),
    italicMessageBody: (messageContent) => cy.get("em").contains(messageContent),
    underlineMessageBody: (messageContent) => cy.get("u").contains(messageContent),
    strikeMessageBody: (messageContent) => cy.get("s").contains(messageContent),
    closeModal: () => cy.get("button").contains("Close"),
    readReceipt: (readReceiptMessage) => cy.get("span").contains(readReceiptMessage)
  }

  confirmationModal = {
    confirmationClosureHeader: () => cy.get("h2").contains("Confirm Closure of Conversation"),
    confirmationReopenHeader: () => cy.get("h2").contains("Confirm Re-Open of Conversation"),
    confirmClosure: () => cy.get(`button[data-testid="confirm-close-conversation-button"]`).contains(/^Close Conversation$/),
    confirmReopening: () => cy.get(`button[data-testid="confirm-re-open-conversation-button"]`).contains(/^Re-Open Conversation$/)
  }

  richTextArea = {
    mainTypeArea: () => cy.get(`div[data-placeholder="Type a message..."]`),
    styleChoice: (x) => cy.get(`button.ql-${x}[aria-label="${x}"]`)
  }
}

const requestConversationModal = new _requestConversationModal()
export default requestConversationModal

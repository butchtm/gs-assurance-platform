class _ViewRequestPage {

    buttons = {
        updateRequestButton: () => cy.contains('button', 'Update Request'),
        nextButton: () => cy.contains('button', 'Next'),
        backButton: () => cy.contains('button', 'Back'),
        finishButton: () => cy.contains('button', 'Finish')
    }

}

const viewRequestPage = new _ViewRequestPage()
export default viewRequestPage

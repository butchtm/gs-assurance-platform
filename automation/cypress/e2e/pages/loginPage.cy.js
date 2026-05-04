class _LoginPage {
  usernameField() {
    return cy.get('[autocomplete="username"]')
  }

  passwordField() {
    return cy.get('[data-testid="login-password-input"]')
  }

  loginButton() {
    return cy.get('[type="submit"]')
  }

  // Slightly out of scope for /login page
  // Included as unhappy path messages will appear on /login
  // Will later expand this function to include all messages
  messagePopup() {
    return cy.get('[fill="var(--toastify-icon-color-success)"]')
  }
}

const loginPage = new _LoginPage()
export default loginPage

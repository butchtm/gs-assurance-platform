import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import loginPage from "../../pages/loginPage.cy.js"
//Needs reformatting as more developed functions may be available
Given("I visit assurance platform", () => {
  cy.visit("/")
})

Given("I can see the username & password fields", () => {
  loginPage.usernameField().should("be.visible")
  loginPage.passwordField().should("be.visible")
})

When("I click the username field", () => {
  loginPage.usernameField().click()
})

When("I click the password field", () => {
  loginPage.passwordField().click()
})

//Documentation: example of how examples in .feature files are translated in step definition files
And("I input user {string}", (username) => {
  loginPage.usernameField().type(Cypress.env(username))
})

And("I input pass {string}", (password) => {
  loginPage.passwordField().type(Cypress.env(password))
})

When("I click login button", () => {
  loginPage.loginButton().click()
})

And("I can see login success message", () => {
  loginPage.messagePopup().should("be.visible")
})

And("I login as {string}", (user) => {
  cy.loginAsUser(user)
})

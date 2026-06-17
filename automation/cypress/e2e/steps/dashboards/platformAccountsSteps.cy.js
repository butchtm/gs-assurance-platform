import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import platformAccountsPage from "../../pages/platformAccountsPage.cy.js"

When("I select {string} from accounts page", (object) => {
  switch (object) {
    case "name filter":
      platformAccountsPage.buttons.filterByName().click()
      break
    case "type filter":
      platformAccountsPage.buttons.filterByType().click()
      break
    case "active filter":
      platformAccountsPage.buttons.filterByActive().click()
      break
    case "create account":
      platformAccountsPage.buttons.createAccount().click()
      break
    case "rows per page":
      platformAccountsPage.rowsPerPage.rowsDropdown().click()
      break
    default:
      throw new Error(`${object} not found`)
  }
})

And("I delete all test accounts using the API", () => {
  cy.deleteTestPlatformAccountsApi()
})

Then("No platform accounts are found", () => {
  cy.contains("No results found").should("be.visible")
})

Then("account name {string} is displayed in the list", (accountName) => {
  cy.waitForNetworkIdle(1000)
  cy.get(`td[data-index="2"]`).contains(accountName).should('exist')
})

And("I select {string} from account type filter", (object) => {
  switch (object) {
    case "vvb":
      platformAccountsPage.openTypeFilter.typeVVB().click()
      break
    case "tac":
      platformAccountsPage.openTypeFilter.typeTAC().click()
      break
    case "reviewer":
      platformAccountsPage.openTypeFilter.typeReviewer().click()
      break
    case "project developer":
      platformAccountsPage.openTypeFilter.typeDeveloper().click()
      break
    case "finance":
      platformAccountsPage.openTypeFilter.typeFinance().click()
      break
    default:
      throw new Error(`${object} not found`)
  }
})

And("I select {string} from rows drop down", (rowNumber) => {
  platformAccountsPage.rowsPerPage.numberOfRows(rowNumber).click()
})

Then("More than {int} rows are on the page", (expectedCount) => {
  cy.wait(1000)
  let count = 0

  cy.get("tbody")
    .within(() => {
      cy.get("tr").each(() => {
        count += 1
      })
    })
    .then(() => {
      expect(count).to.be.greaterThan(expectedCount)
    })
})


Then("{int} rows are on the page", (expectedCount) => {
  cy.wait(1000)
  let count = 0

  cy.get("tbody")
    .within(() => {
      cy.get("tr").each(() => {
        count += 1
      })
    })
    .then(() => {
      expect(count).to.equal(expectedCount)
    })
})


When("I select {string} account from displayed list", (accountName) => {
  cy.get(`td[data-index="2"]`).contains(accountName).click({ force: true })
})

Then("platform account title {string} can be seen", (accountName) => {
  cy.get(`div`).contains(accountName)
})

When("I select edit icon from account details area", () => {
  platformAccountsPage.openPlatformAccount.editAccountDetails().click()
})

And("edit account text is at header of modal", () => {
  cy.get(`h2`).contains("Edit Account")
})

And("I select update button to apply new account details", () => {
  cy.wait(1500)
  cy.get(`button`).contains("Update").click()
  cy.contains("Account Updated Successfully!").should('not.exist')
})

And("I type {string} into account name field", (name) => {
  platformAccountsPage.editPlatformAccount.accountName().click({ force: true }).type(`{selectall}{backspace}${name}`)
})

And("I input my details for {string} account", (accountType) => {
  platformAccountsPage.createPlatformAccount.accountType().click({ force: true })
  platformAccountsPage.createPlatformAccount.typeSelection(accountType).click()

  let accountName = `autotest-${accountType}`
  let mainContactEmail = `gjennings@caci.co.uk`
  let mainContactAddress = `testAddress`

  platformAccountsPage.createPlatformAccount.accountName().click({ force: true }).type(accountName)
  platformAccountsPage.createPlatformAccount.accountEmail().click({ force: true }).type(mainContactEmail)
  platformAccountsPage.createPlatformAccount.accountAddress().click({ force: true }).type(mainContactAddress)
})

Then("I select create account button", () => {
  let extractAccountId
  cy.intercept("POST", "/api/accounts").as("getAccountId")

  platformAccountsPage.createPlatformAccount.createButton()

  cy.wait("@getAccountId").then((interceptAction) => {
    extractAccountId = interceptAction.response.body
    cy.log("AccountID:", extractAccountId)

    cy.readFile("cypress/fixtures/platformInfo.json").then((data) => {
      data["New Platform Account"] = extractAccountId
      cy.writeFile("cypress/fixtures/platformInfo.json", data)
    })
  })
})

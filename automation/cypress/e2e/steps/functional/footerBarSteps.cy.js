import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import footerBar from "../../pages/footerBarPages.cy"

When("I click {string} from the page footer", (footerChoice) => {
  switch (footerChoice) {
    case "Privacy Policy":
      footerBar.primaryBar.privacyPolicy().click()
      break
    case "Terms & Conditions":
      footerBar.primaryBar.teesAndCees().invoke("removeAttr", "target").click()
      cy.wait(1500)
      break
    case "Accessability":
      footerBar.primaryBar.accessability().invoke("removeAttr", "target").click()
      break
    case "Version":
      footerBar.primaryBar.version().click()
      break
    default:
      throw new Error(`Button ${button} not found in footer`)
  }
})

Then("{string} from footer has loaded a new page", (footerChoice) => {
  switch (footerChoice) {
    case "Privacy Policy":
      footerBar.expectedUrl.privacyPolicy()
      break
    case "Terms & Conditions":
      footerBar.expectedUrl.teesAndCees()
      break
    case "Accessability":
      footerBar.expectedUrl.accessability()
      break
    default:
      throw new Error(`Button ${button} not found in footer`)
  }
})

Then("Version modal has opened with UI and API version", () => {
  footerBar.versionModal.modalHeader()
  footerBar.versionModal.apiVersion()
  footerBar.versionModal.uiVersion()
})

And("I can close the version modal", () => {
  footerBar.versionModal.closeModal().click()
})

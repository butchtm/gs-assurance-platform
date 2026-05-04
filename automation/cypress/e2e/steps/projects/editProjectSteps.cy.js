import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import editProjectPage from "../../pages/editProjectPage.cy.js"

When("I change {string} to include new information", (field) => {
  switch (field) {
    case "project description":
      editProjectPage.fields.projectDescription().type(editProjectPage.newInformation.projectDescription)
      break

    case "project location":
      selectDropdownOption(editProjectPage.fields.projectLocation, editProjectPage.dropdowns.switzerland)
      break

    case "project types":
      selectDropdownOption(editProjectPage.fields.projectTypes, editProjectPage.dropdowns.geothermal)
      break

    case "certified products":
      selectDropdownOption(editProjectPage.fields.certifiedProducts, editProjectPage.dropdowns.gender)
      break

    case "s d goals":
      selectDropdownOption(editProjectPage.fields.sustainableDevelopmentGoals, editProjectPage.dropdowns.lifeBelowWater)
      break

    case "registration type":
      selectDropdownOption(editProjectPage.fields.registrationType, editProjectPage.dropdowns.retroactive)
      break

    case "carbon stream":
      selectDropdownOption(editProjectPage.fields.carbonStream, editProjectPage.dropdowns.gsCer)
      break

    case "fee schedule":
      selectDropdownOption(editProjectPage.fields.feeSchedule, editProjectPage.dropdowns.shareOfProceeds)
      break

    case "project scale":
      selectDropdownOption(editProjectPage.fields.projectScale, editProjectPage.dropdowns.smallScale)
      break

    default:
      throw new Error(`field ${field} not found`)
  }

  function selectDropdownOption(field, dropdown) {
    field().click({ force: true }).type("{backspace}").type("{downarrow}")
    dropdown().click()
  }
})

And("I select update", () => {
  cy.scrollTo("bottom")
  editProjectPage.buttons.updateButton().click()
})

And("I can observe new {string} on the project information page", (field) => {
  cy.wait(2500)

  const fieldMap = {
    "project description": editProjectPage.newInformation.projectDescription,
    "project location": editProjectPage.newInformation.projectLocation,
    "project scale": editProjectPage.newInformation.projectScale,
    "project types": editProjectPage.newInformation.projectTypes,
    "certified products": editProjectPage.newInformation.certifiedProducts,
    "registration type": editProjectPage.newInformation.registrationType,
    "carbon stream": editProjectPage.newInformation.carbonStream,
    "fee schedule": editProjectPage.newInformation.feeSchedule,
    "s d goals": editProjectPage.newInformation.sustainableDevelopmentGoals
  }

  const fieldValue = fieldMap[field]

  if (fieldValue) {
    if (field === "s d goals") {
      cy.get(`[title="${fieldValue}"]`)
    } else {
      cy.get("div").contains(fieldValue)
    }
  } else {
    throw new Error(`Field ${field} not found`)
  }
})

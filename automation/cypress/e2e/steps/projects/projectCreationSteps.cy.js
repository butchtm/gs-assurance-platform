import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import projectCreationPage from "../../pages/projectCreationPage.cy.js"
import projectsPage from "../../pages/projectsPage.cy.js"

When("I click {string} from project creation page", (field) => {
  switch (field) {
    case "project developer account":
      projectCreationPage.fields.projectDeveloperAccount().click({ force: true }).type("{downarrow}")
      break
    case "project classification":
      projectCreationPage.fields.projectClassification().click()
      break
    case "project title":
      projectCreationPage.fields.projectTitle().click({ force: true })
      break
    case "next":
      projectCreationPage.navigation.nextButton().click()
      break
    case "finish":
      projectCreationPage.navigation.finishButton().click()
      break
    case "project description":
      projectCreationPage.fields.projectDescription().click()
      break
    case "project location":
      projectCreationPage.fields.projectLocation().click({ force: true })
      break
    case "project scale":
      projectCreationPage.fields.projectScale().click({ force: true }).type("{downarrow}") //inputs down arrow key
      break
    case "project type(s)":
      //We look at the parent element of what has been defined in the page object
      //and search through cascading elements to find the only button
      projectCreationPage.fields.projectTypes().parent().find('button[aria-label="Open"]').click()
      break
    case "registration type":
      projectCreationPage.fields.registrationType().click({ force: true }).type("{downarrow}")
      break
    case "certified products":
      projectCreationPage.fields.certifiedProducts().click({ force: true }).type("{downarrow}")
      break
    case "carbon stream":
      projectCreationPage.fields.carbonStream().click({ force: true }).type("{downarrow}") //inputs down arrow key
      break
    case "transition project":
      projectCreationPage.fields.transitioningProject().click({ force: true }).type("{downarrow}") //inputs down arrow key
      break
    case "emission reduction removal":
      projectCreationPage.fields.emissionReductionRemoval().click({ force: true })
      break
    case "sustainable development goals":
      projectCreationPage.fields.sustainableDevelopmentGoals().parent().find("button").click({ scrollBehavior: "center" })
      break
    case "fee schedule":
      projectCreationPage.fields.feeSchedule().click({ force: true }).type("{downarrow}") //inputs down arrow key
      break
    case "notes":
      projectCreationPage.fields.notes().click({ force: true })
      break
    case "programme of activities":
      projectCreationPage.fields.selectPOA().parent().find("button").click({ scrollBehavior: "center" })
      break
    case "real case vpa":
      projectCreationPage.fields.selectRealCase().click({ force: true }).type("{downarrow}")
      break
    case "version":
      projectCreationPage.fields.methodologyVersionNth(0).click({ force: true }).type("1")
      break
    case "project methodologies":
      projectCreationPage.fields.projectMethodologiesNth(0).parent().find("button").click({ scrollBehavior: "center" })
      break
    default:
      throw new Error(`field ${field} not found`)
  }
})

When("I click {string} from dropdown", (field) => {
  const actions = {
    "pd account": projectCreationPage.dropdown.pdAccount,
    "standalone project": projectCreationPage.dropdown.standaloneProject,
    "poa project": projectCreationPage.dropdown.poaProject,
    "real project": projectCreationPage.dropdown.realProject,
    "regular project": projectCreationPage.dropdown.regularProject,
    "automation test (POA)": projectCreationPage.dropdown.selectPoa,
    "automation test (REALCASE)": projectCreationPage.dropdown.selectRealCase,
    "micro scale": projectCreationPage.dropdown.microScale,
    "agriculture": projectCreationPage.dropdown.agriculture,
    "acm0001 flaring": projectCreationPage.dropdown.flaring,
    "regular": projectCreationPage.dropdown.regular,
    "carbon": projectCreationPage.dropdown.carbon,
    "gs ver": projectCreationPage.dropdown.gsVer,
    "yes": projectCreationPage.dropdown.yes,
    "no": projectCreationPage.dropdown.no,
    "climate action": projectCreationPage.dropdown.climateAction,
    "cash per credit": projectCreationPage.dropdown.cashPerCredit
  }

  const action = actions[field]

  if (action) {
    action().click()
  } else {
    throw new Error(`field ${field} not found`)
  }
})

Then("Field contains {string} in {string}", (text, type) => {
  switch (type) {
    case "value field":
      cy.get(`[value*="${text}"]`)
      break
    case "text area":
      cy.get("textarea").contains(`${text}`)
      break
    case "div element":
      cy.get("div").contains(`${text}`)
      break
    case "p element":
      cy.get("p").contains(`${text}`)
      break
    default:
      throw new Error(`type ${type} not found`)
  }
})

When("I select edit for {string} via summary page", (text) => {
  if (text == "carbon stream") {
    cy.contains("strong", "Carbon Stream").parent().next("p").find("button").contains("Edit").click()
  } else {
    throw new Error(`field ${text} not found`)
  }
})

When("I click {string} from project creation nav buttons", (text) => {
  if (text == "review") {
    projectCreationPage.navigation.summaryButton().click()
  } else {
    throw new Error(`field ${text} not found`)
  }
})

Then("I am taken to page {string}", (page) => {
  const validPages = ["2", "3", "4", "5", "6"]

  if (validPages.includes(page)) {
    switch(page) {
      case "2":
        cy.contains('Project Description')
        break
      case "3":
        cy.contains('Crediting Periods')
        break
      case "4":
        cy.contains('Transitioning Project')
        break
      case "5":
        cy.get('[data-testid="Sustainable Development Goals-input"]').should('be.visible')
        break
      case "6":
        cy.contains('Review Your Information')
        break
    }
  } else {
    throw new Error(`page ${page} not found`)
  }
})
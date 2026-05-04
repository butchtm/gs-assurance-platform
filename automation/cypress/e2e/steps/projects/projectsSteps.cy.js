import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import projectsPage from "../../pages/projectsPage.cy.js"

When("I click {string} from projects page", (button) => {
  switch (button) {
    case "create":
      projectsPage.createProjectsButton().click()
      break
    case "projects search bar":
      projectsPage.feature.projectSearchBar().click({ force: true })
      break
    /* case "contact":
            projectsPage.navbarContactButton().click()
            break
        */
    default:
      throw new Error(`Button ${button} not found on projects page`)
  }
})

Then("My project {string} with {string} status can be seen", (projectName, projectStatus) => {
  cy.findProjectRow(projectName, projectStatus, () => {
    // No additional action needed, as the presence of the row itself is the verification
    cy.log(`Project ${projectName} with status ${projectStatus} found.`)
  })
})

Then("I click {string} for my {string} with {string} status", (button, projectName, projectStatus) => {
  if (button == "View") {
    cy.findProjectRow(projectName, projectStatus, () => {
      cy.get("span").contains(projectName).click()
    })
  } else if (button == "Edit") {
    cy.findProjectRow(projectName, projectStatus, () => {
      cy.get("button").contains(button).click()
    })
  } else {
    cy.log(`${button} not real`)
  }
})

/*
The below could do with resolving rather than reducing
 unfortunately I am at a loss to resolve it further
*/
//Contemplating removal: had to comment out/remove the main premise of this step
//Moved closer to the execution of the next step to reduce flakey test
And("I search a {string} {string} GSID", (status, text) => {
  cy.readFile("cypress/fixtures/platformInfo.json").then((data) => {
    const gsids = {
      Listed: {
        "automation test (POA)": data["Listed POA GSID"],
        "automation test (standalone project)": data["Listed Standalone GSID"],
        "automation test (REGULARCASE)": data["Listed REGULARCASE GSID"],
        "automation test (REALCASE)": data["Listed REALCASE GSID"]
      },
      New: {
        "automation test (POA)": data["New POA GSID"],
        "automation test (REGULARCASE)": data["New REGULARCASE GSID"],
        "automation test (REALCASE)": data["New REALCASE GSID"],
        "automation test (standalone project)": data["New Standalone GSID"]
      }
    }

    const gsid = gsids[status][text]
    cy.log("Located: " + gsid)
    //projectsPage.feature.projectSearchBar().click({ force: true }).type(gsid+'{enter}')
  })
})

And("I select {string} {string} GSID from results as {string}", (status, text, username) => {
  cy.readFile("cypress/fixtures/platformInfo.json").then((data) => {
    const gsids = {
      Listed: {
        "automation test (POA)": data["Listed POA GSID"],
        "automation test (standalone project)": data["Listed Standalone GSID"],
        "automation test (REGULARCASE)": data["Listed REGULARCASE GSID"],
        "automation test (REALCASE)": data["Listed REALCASE GSID"]
      },
      New: {
        "automation test (POA)": data["New POA GSID"],
        "automation test (REGULARCASE)": data["New REGULARCASE GSID"],
        "automation test (REALCASE)": data["New REALCASE GSID"],
        "automation test (standalone project)": data["New Standalone GSID"]
      }
    }

    cy.log("status: " + status)
    cy.log("text: " + text)
    const gsid = gsids[status][text]

    cy.intercept("GET", `**${gsid}**`).as("waitOnApi")
    //Taken from the step above and added intercept to reduce flakey part of tests
    projectsPage.feature
      .projectSearchBar()
      .click({ force: true })
      .type(gsid + "{enter}")
    cy.wait("@waitOnApi")
    cy.get(`[aria-label="${gsid}"]`).click({ force: true })
  })
})

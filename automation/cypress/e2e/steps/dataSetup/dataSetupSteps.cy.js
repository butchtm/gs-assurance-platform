import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import projectCreationPage from "../../pages/projectCreationPage.cy.js"
import projectsPage from "../../pages/projectsPage.cy.js"
import editProjectPage from "../../pages/editProjectPage.cy.js"
import navigationBar from "../../pages/navigationBars.cy.js"

When("I repeat adding {string} {string} projects", (numberOf, type) => {
  for (let i = 0; i < parseInt(numberOf); i++) {
    cy.readFile("cypress/fixtures/platformInfo.json").then((data) => {
      projectCreationPage.fields.projectClassification().click()

      if (type == "REGULARCASE") {
        projectCreationPage.dropdown.regularProject().click()
      } else {
        projectCreationPage.dropdown.realProject().click()
      }

      projectCreationPage.fields.selectPOA().parent().find("button").click({ scrollBehavior: "center" })
      projectCreationPage.fields.selectPOA().click().type(`automation test (POA) run:${data["Run Number"]}`)
      projectCreationPage.dropdown.selectPoa().click()

      if (type == "REGULARCASE") {
        projectCreationPage.fields.selectRealCase().click({ force: true }).type("{downarrow}")
        projectCreationPage.fields.selectRealCase().type(`${data["New REALCASE GSID"]}`)
        projectCreationPage.dropdown.selectRealCase().click()
      }

      projectCreationPage.fields.projectTitle().click({ force: true })
      projectCreationPage.fields
        .projectTitle()
        .click()
        .type(`automation test (${type}) run:${data["Run Number"]} extra:${i + 1}`)

      projectCreationPage.navigation.nextButton().click()

      //projectCreationPage.fields.projectTypes().parent().find("button").click()
      //projectCreationPage.dropdown.agriculture().click()

      projectCreationPage.navigation.nextButton().click()

      projectCreationPage.fields.projectMethodologiesNth(0).click({ scrollBehavior: "center" })
      projectCreationPage.dropdown.flaring().click()

      projectCreationPage.fields.methodologyVersionNth(0).click({ force: true }).type("1")

      projectCreationPage.navigation.nextButton().click()

      projectCreationPage.fields.transitioningProject().click({ force: true }).type("{downarrow}")
      projectCreationPage.dropdown.no().click()

      projectCreationPage.navigation.nextButton().click()

      projectCreationPage.fields.feeSchedule().click({ force: true }).type("{downarrow}")
      projectCreationPage.dropdown.cashPerCredit().click()

      projectCreationPage.navigation.nextButton().click()
      projectCreationPage.navigation.finishButton().click()

      projectsPage.createProjectsButton().click()
    })
  }
})

And("Run number increased", () => {
  cy.readFile("cypress/fixtures/platformInfo.json").then((data) => {
    data["Run Number"] += 1
    cy.writeFile("cypress/fixtures/platformInfo.json", data)
  })
})

//Specific to the 2nd data set
Then("I add {string} {string} projects", (number, type) => {
  for (let i = 0; i < parseInt(number); i++) {
    cy.readFile("cypress/fixtures/platformInfo.json").then((data) => {
      projectCreationPage.fields.projectDeveloperAccount().click({ force: true }).type("{downarrow}")
      projectCreationPage.dropdown.pdAccount().click()

      projectCreationPage.fields.projectClassification().click()

      switch (type) {
        case "POA":
          projectCreationPage.dropdown.poaProject().click()
          break
        case "standalone project":
          projectCreationPage.dropdown.standaloneProject().click()
          break
        case "REALCASE":
          projectCreationPage.dropdown.realProject().click()
          break
        case "REGULARCASE":
          projectCreationPage.dropdown.regularProject().click()
          break
        default:
          throw new Error(`Button ${button} not found`)
      }

      if (type == "REGULARCASE") {
        projectCreationPage.fields.selectPOA().parent().find("button").click({ scrollBehavior: "center" })
        projectCreationPage.fields.selectPOA().click().type(`automation test (POA) run:${data["Run Number"]} (L)`) // select poa for regular case
        projectCreationPage.dropdown.selectPoa().click()

        projectCreationPage.fields.selectRealCase().click({ force: true }).type("{downarrow}")
        projectCreationPage.fields.selectRealCase().type(`automation test (REALCASE) run:${data["Run Number"]} (L)`) // select real case for regular case
        projectCreationPage.dropdown.selectRealCase().click()
      } else if (type == "REALCASE") {
        projectCreationPage.fields.selectPOA().parent().find("button").click({ scrollBehavior: "center" })
        projectCreationPage.fields.selectPOA().click().type(`automation test (POA) run:${data["Run Number"]} (L)`) // select poa for real case
        projectCreationPage.dropdown.selectPoa().click()
      }

      projectCreationPage.fields.projectTitle().click({ force: true })
      //add a function to project creation page to determine
      projectCreationPage.functions.createProjectStructureTitles(i, "Listed", type)

      projectCreationPage.navigation.nextButton().click()

      if (type == "POA" || type == "standalone project") {
        projectCreationPage.fields.projectTypes().parent().find("button").click()
        projectCreationPage.dropdown.agriculture().click()
      }

      projectCreationPage.navigation.nextButton().click()

      projectCreationPage.fields.projectMethodologiesNth(0).click({ scrollBehavior: "center" })
      projectCreationPage.dropdown.flaring().click()

      projectCreationPage.fields.methodologyVersionNth(0).click({ force: true }).type("1")

      projectCreationPage.navigation.nextButton().click()

      projectCreationPage.fields.transitioningProject().click({ force: true }).type("{downarrow}")
      projectCreationPage.dropdown.no().click()

      projectCreationPage.navigation.nextButton().click()

      projectCreationPage.fields.feeSchedule().click({ force: true }).type("{downarrow}")
      projectCreationPage.dropdown.cashPerCredit().click()

      projectCreationPage.navigation.nextButton().click()

      //GATHER GSID of FIRST LISTED for each PROJECT CLASS
      if (i === 0) {
        cy.captureProjectGSID(`Listed ${type}`, "admin")
      } else {
        projectCreationPage.navigation.finishButton().click()
      }

      //edit project to be listed as designed - as done by admin is ok if done PD will require update
      cy.findProjectRow(`automation test (${type}) run:${data["Run Number"]} (L)`, "New", () => {
        cy.get("button").contains("Edit").click()
      })

      cy.wait(1500)
      editProjectPage.buttons.updateProjectStatusButton().click()
      editProjectPage.fields.projectStatus().click({ force: true }).type("{downarrow}")
      editProjectPage.dropdowns.projectStatus().click()
      editProjectPage.buttons.updateButton().click()

      cy.wait(1000)
      navigationBar.primaryBar.projects().click()
      cy.wait(2500)

      //reselect createProject to continue the loop via workflow
      projectsPage.createProjectsButton().click()
    })
  }
})


Given("I delete all existing projects", () => {
  cy.loginApi().then(() => {
    cy.deleteTestDataApi()
  })
})

Then("There are no projects shown on the projects page", () => {
  cy.contains('automation test').should("not.exist")
})

Then("an email should have subject {string} and body containing {string}", (expectedSubject, expectedBodyText) => {
  cy.loginApi().then(() => {
    cy.getEmailNotificationsApi(5).then((response) => {
      const emails = response.body.content

      const found = emails.some((email) => {
        const subjectMatch = email.subject === expectedSubject
        const bodyMatch = email.htmlBody.includes(expectedBodyText) || email.textBody.includes(expectedBodyText)

        if (subjectMatch && bodyMatch) {
          cy.log(`Matched email addresses: ${email.addresses.join(", ")}`)
          console.log("Matched email addresses:", email.addresses) // for terminal output
          return true
        }

        return false
      })

      expect(found, `No email found with subject "${expectedSubject}" and body containing "${expectedBodyText}"`).to.be.true
    })
  })
})

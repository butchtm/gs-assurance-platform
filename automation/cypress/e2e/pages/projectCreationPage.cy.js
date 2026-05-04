class _ProjectCreationPage {
  fields = {
    projectDeveloperAccount: () => cy.get("label").contains("Project Developer Account"),
    projectClassification: () => cy.get('[id="projectClassificationId"]'),
    projectTitle: () => cy.get("label").contains("Project Title").parent().find("input"),
    projectDescription: () => cy.get('[rows="5"]'),
    projectLocation: () => cy.get("label").contains("Project Location"),
    projectScale: () => cy.get('[id="sizeId-input"]').parent(),
    projectTypes: () => cy.get("label").contains("Project Type(s)"),
    registrationType: () => cy.get('[id="registrationTypeId-input"]').parent(),
    certifiedProducts: () => cy.get("label").contains("Certified Products"),
    carbonStream: () => cy.get('[id="carbonStreamId-input"]').parent(),
    transitioningProject: () => cy.get('[id="transitioningProject-input"]').parent(),
    emissionReductionRemoval: () => cy.get("label").contains("Estimated Annual Emission Reduction/Removal"),
    sustainableDevelopmentGoals: () => cy.get("label").contains("Sustainable Development Goals"),
    feeSchedule: () => cy.get('[id="feeScheduleId-input"]').parent(),
    notes: () => cy.get("label").contains("Notes"),
    selectPOA: () => cy.get("label").contains("Programme of Activities"),
    selectRealCase: () => cy.get("label").contains("Real Case VPA"),

    projectMethodologiesNth: (x) => cy.get(`[data-testid="methodology-input-${x}"]`),
    methodologyVersionNth: (x) => cy.get(`[data-testid="version-input-${x}"]`)
  }

  navigation = {
    nextButton: () => cy.get('[type="submit"]').contains("Next"),
    activePage: () => cy.get('[class="MuiStepLabel-root MuiStepLabel-horizontal MuiStepLabel-alternativeLabel css-1abj2s5"]'),
    finishButton: () => cy.get('[type="submit"]').contains("Finish"),
    summaryButton: () => cy.get("span").contains("Review")
  }

  dropdown = {
    standaloneProject: () => cy.get('[id="projectClassificationId-option-3"]'), //From project classification
    poaProject: () => cy.get('[id="projectClassificationId-option-0"]'), //From project classification
    realProject: () => cy.get('[id="projectClassificationId-option-1"]'), //From project classification
    regularProject: () => cy.get('[id="projectClassificationId-option-2"]'), //From project classification
    selectPoa: () => cy.get("li").contains("automation test (POA)"), //From select programme of activity (Appears when creating a project structure)
    selectRealCase: () => cy.get("li").contains("automation test (REALCASE)"), //From select real case vpa ^^^
    microScale: () => cy.get("li").contains("Micro Scale"), //From project scale
    agriculture: () => cy.get("li").contains("Agriculture"), //From project types
    flaring: () => cy.get("li").contains("ACM0001 Flaring"), //From project methodologies
    regular: () => cy.get("li").contains("Regular"), //From registration type
    carbon: () => cy.get("li").contains("Carbon"), //From certified products
    gsVer: () => cy.get("li").contains("GS VER"), //From carbon stream
    yes: () => cy.get("li").contains("Yes"), //From transition project
    no: () => cy.get("li").contains("No"), //From transition project
    climateAction: () => cy.get("li").contains("13 - Climate Action"), //From sustainable development goals
    cashPerCredit: () => cy.get("li").contains("Cash per Credit"), //From fee schedule
    pdAccount: () => cy.get("li").contains(/^PD$/) //From project developer account
  }

  functions = {
    createProjectStructureTitles(position, status, projectClass) {
      cy.readFile("cypress/fixtures/platformInfo.json").then((data) => {
        if (status === "Listed") {
          if (position === 0) {
            projectCreationPage.fields
              .projectTitle()
              .click({ force: true })
              .type(`automation test (${projectClass}) run:${data["Run Number"]} (L)`) // designate my name
          } else {
            projectCreationPage.fields
              .projectTitle()
              .click({ force: true })
              .type(`automation test (${projectClass}) run:${data["Run Number"]} (L) extra:${position}`) // designate my name
          }
        }
      })
    }
  }
}

const projectCreationPage = new _ProjectCreationPage()
export default projectCreationPage

class _EditProjectPage {
  fields = {
    projectDescription: () => cy.get("label").contains("Project Description").parent().find("textarea").first(),
    projectLocation: () => cy.get("label").contains("Project Location"),
    projectScale: () => cy.get('[id="sizeId-input"]').parent(),
    projectTypes: () => cy.get("label").contains("Project Type(s)"),
    registrationType: () => cy.get('[id="registrationTypeId-input"]').parent(),
    carbonStream: () => cy.get('[id="carbonStreamId-input"]').parent(),
    feeSchedule: () => cy.get('[id="feeScheduleId-input"]').parent(),
    certifiedProducts: () => cy.get("label").contains("Certified Products"),
    sustainableDevelopmentGoals: () => cy.get("label").contains("Sustainable Development Goals"),
    projectStatus: () => cy.get('[aria-labelledby="project-status-label"]').parent()
  }

  //dropdowns encountered during the course of testing named as desired choices
  dropdowns = {
    smallScale: () => cy.get("li").contains("Small Scale"), //From project scale
    switzerland: () => cy.get("li").contains("Switzerland"), //From project location
    geothermal: () => cy.get("li").contains("Geothermal"), //From project types
    gender: () => cy.get("li").contains("Gender"), //From certified products
    retroactive: () => cy.get("li").contains("Retroactive"), //From registration type
    gsCer: () => cy.get("li").contains("GS CER"), //From carbon stream
    shareOfProceeds: () => cy.get("li").contains("Share of Proceeds"), //From fee schedule
    lifeBelowWater: () => cy.get("li").contains("14 - Life Below Water"), //From sustainable development goals
    projectStatus: () => cy.get("li").contains("Listed") //From project status
  }

  buttons = {
    updateButton: () => cy.get('[type="submit"]').contains("Update"),
    updateProjectStatusButton: () => cy.get('[data-testid="project-status-update-btn"]')
  }

  newInformation = {
    projectDescription: "This project description has been edited //",
    projectLocation: "Switzerland",
    projectScale: "Small Scale",
    projectTypes: "Geothermal",
    certifiedProducts: "Gender",
    registrationType: "Retroactive",
    carbonStream: "GS CER",
    feeSchedule: "Share of Proceeds",
    sustainableDevelopmentGoals: "14 - Life Below Water"
  }
}

const editProjectPage = new _EditProjectPage()
export default editProjectPage

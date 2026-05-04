import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps"
import projectInfoCreditingPeriod from "../../pages/projectInfoCreditingPeriodPage.cy"

When("I click {string} from project information page", (object) => {
  projectInfoCreditingPeriod.projectInfo.creditingPeriodOne().click()
})

Then("{string} from project information page has loaded", (object) => {
  projectInfoCreditingPeriod.expectedUrl.creditingPeriodOne()
})

And("I can see Methodologies for {string} displayed", (object) => {
  projectInfoCreditingPeriod.pageElements.methodologiesHeader()
  projectInfoCreditingPeriod.pageElements.methodologyOne()
  projectInfoCreditingPeriod.pageElements.methodologyTwo()
})

When("I click {string} from open {string} page", (link, creditingPeriodId) => {
  projectInfoCreditingPeriod.pageElements.methodologyLinks(link).click()
})

Then("Request {string} has opened and request status is displayed", (requestName) => {
  projectInfoCreditingPeriod.openedRequest.requestHeaderName(requestName)
  projectInfoCreditingPeriod.openedRequest.requestStatusHeader()
})

Then("Account {string} has opened and account details are displayed", (accountName) => {
  projectInfoCreditingPeriod.expectedUrl.platformAccount()
  projectInfoCreditingPeriod.openedAccount.accountHeaderName(accountName)
  projectInfoCreditingPeriod.openedAccount.accountDetailsHeader()
})

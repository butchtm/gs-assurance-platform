
# Assurance Automation 

This project contains end-to-end tests for our web application using Cypress and Cucumber. The tests are primarily UI / Functional and are written in Gherkin syntax and executed with Cypress, providing a clear and concise way to describe test scenarios. Test

#### The provided instructions and code snippets are appropriate for Linux, macOS, and windows environments.

## Installation

Quick install: 

```bash
through gitlab go to the selected branch
select the 'Code' button 
select 'Visual Studio Code (SSH)' from 'Open in your IDE'
open cmd in VScode and type npm install 
```

Follow these steps to set up the project:

```bash
# Clone the repository into projects folder
git clone [PLACEHOLDER]

# Navigate to the project directory
cd automation

# Install dependencies (will install from package.json)
npm install
```

## Local Environment Configuration
Users are synced from Cognito, however Platform Accounts need to be created manually locally.  These should be:

PD
ARM
TAC
VVB
Finance
Reviewer
Support

Each needs to be created in the UI and the relevent autotest-xxx user added as an Assigned User.  E.g. When creating the PD platform account the autotest-PD user must be added as an Assigned User.

## Dependencies
This project relies on the following dependencies:

1. Cypress: A JavaScript end-to-end testing framework.
2. Cypress Cucumber Preprocessor: A plugin to enable Cucumber syntax in Cypress tests.
3. Dotenv: A module to load environment variables from a .env file. (More on this below)
4. Prettier: Code formatter 
- npm run formatSteps (formats files in /steps)
- npm run formatSupport (format files in /support)
- npm run formatPages (format files in /pages)
- npm run formatLarge (format all .js files)

These dependencies are specified in the package.json file:
```
# From package.json
"dependencies": {
  "cypress-cucumber-preprocessor": "^4.3.1",
  "dotenv": "^16.4.5"
},
"devDependencies": {
    "prettier": "^3.3.3",
      "cypress": "^15.14.2",
}
```
(The .prettierrc, file is available via contact to gjennings@caci.co.uk)


.gitignore file:
```
node_modules    
.env
.png
.prettierrc
cypress/screenshots #To avoid uploading many screenshots from cypress output
.gitlab-ci copy.yml
```
file is available via contact to gjennings@caci.co.uk

## User Configuration (Dotenv)
Ensure you have a .env file in the root directory with the necessary environment variables:

```bash
# You will want to use your accounts
# Format is as follows caps: USERGROUP_FIELD e.g. VVB_USERNAME=
ADMIN_USERNAME=
ADMIN_PASSWORD=
PD_USERNAME=
PD_PASSWORD=
```
We have generic users created in the name of automation for each user group, contact gjennings@caci.co.uk for the env file to use on your local environment.

The same set of users are in use for the gitlab CI/CD pipeline. 

## Usage

To run the tests, use the following commands:

#### Open Cypress Test Runner
```
# When writing tests use this mode as very helpful for debugging
With a LOCAL front-end:
npm run cypress:open -- --config-file cypress.config.local.js

With the DEV environment front-nd:
npm run cypress:open

# Once gui is open, select E2E Testing
# You can select any browser installed on your machine
# Then run dataSetup.feature
# You are free to run any of the features
```
#### Run Tests in Headless Mode
- Provides output to console (with --spec and other commands can become a quick debugger)
```
With a LOCAL front-end:
npm run cypress:run -- --config-file cypress.config.local.js

With a DEV environment front-end:
npm run cypress:run

#Example of a more specific command:
npm run cypress:run -- --spec .\cypress\e2e\features\projectCreation.feature --env TAGS='@group0A'

#The above will run only in that file and only scenarios with that name
```
#### Run Tests from automation main repo in gitlab
- Useful for running tests on demand & trigger quickly
```
1. Go to: https://git.iig.caci.co.uk/gold-standard/automation
2. Select build from the left hand side
3. Select pipelines
4. Select new pipeline from top right
5. Select run pipeline (no need to change anything)
```

## Project structure

End to End Testing: \automation\cypress\e2e 
- Master 

Cucumber Feature Files: \automation\cypress\e2e\features
- Contains the cucumber syntax
- Contains files called (example): editProject.feature

Page Objects (POM): \automation\cypress\e2e\pages
- Classes containing different elements from the site
- Classes are used in steps to locate elements
- Contains files called (example): editProjectPage.cy.js

Step Definitions: \automation\cypress\e2e\steps
- Uses classes from the above section to locate elements needing interaction from cypress
- Written in java script
- Contains files called (example): editProjectSteps.cy.js
 
Support Files: \automation\cypress\support
- Custom cypress commands can be found here

Fixture Files: \automation\cypress\fixtures
- JSON file called platformInfo.json which stores GSIDs and other information generated
- Also contains test run information like Run Number, this information is used in a few key places like project searching, which is needed for almost every test

## Automation Development Pipeline & Parallel testing 
!!This section is pretty important!!

To reduce the time it takes to complete the automation tests as a whole individual scenarios or whole feature files are tagged into different groups. These groups are run as seperate jobs on the pipeline.

Example of the gitlab pipeline file:
```

image: cypress/base:16.0.0

stages:          # List of stages for jobs, and their order of execution
  - automated UI/Functional tests - data setup stage
  - automated UI/Functional tests - main stage

# Define common variables and scripts
.default_variables: &default_variables
  variables:
    ADMIN_USERNAME: $ADMIN_USERNAME
    ADMIN_PASSWORD: $ADMIN_PASSWORD
    PD_USERNAME: $PD_USERNAME
    PD_PASSWORD: $PD_PASSWORD
    ARM_USERNAME: $ARM_USERNAME
    ARM_PASSWORD: $ARM_PASSWORD
    VVB_USERNAME: $VVB_USERNAME
    VVB_PASSWORD: $VVB_PASSWORD
    FINANCE_USERNAME: $FINANCE_USERNAME
    FINANCE_PASSWORD: $FINANCE_PASSWORD
    REVIEWER_USERNAME: $REVIEWER_USERNAME
    REVIEWER_PASSWORD: $REVIEWER_PASSWORD
    TAC_USERNAME: $TAC_USERNAME
    TAC_PASSWORD: $TAC_PASSWORD

.default_before_script: &default_before_script
  before_script:
    - node -v
    - npm install

.default_script: &default_script
  script:
    - npx cypress install
    - npx cypress --version

.default_artifacts: &default_artifacts
  artifacts:
    when: always
    paths:
      - cypress/screenshots
      - cypress/videos
      - cypress/fixtures/*.json
    reports:
      junit:
        - cypress/results/junit-report.xml

cypress_tests_group0A:
  stage: automated UI/Functional tests - data setup stage
  tags: 
    - caci
  <<: *default_before_script
  <<: *default_script
  script:
    - npx cypress run --headed --env TAGS='@group0A' --spec ./cypress/e2e/features/dataSetup/dataSetup.feature
  <<: *default_artifacts
  <<: *default_variables
```
The above first half of the file defines the default items for the jobs. 
It has 1 job that runs the data setup before it moves onto the rest.

The below second half of the file has each group. The main part being the npx cypress run and following options.
```
cypress_tests_group1A:
  stage: automated UI/Functional tests - main stage
  tags: 
    - caci
  <<: *default_before_script
  <<: *default_script
  script:
    - npx cypress run --headed --env TAGS='@group1A' --spec ./cypress/e2e/features/editProject.feature 
  <<: *default_artifacts
  <<: *default_variables

cypress_tests_group1B:
  stage: automated UI/Functional tests - main stage
  tags: 
    - caci
  <<: *default_before_script
  <<: *default_script
  script:
    - npx cypress run --headed --env TAGS='@group1B'
  <<: *default_artifacts
  <<: *default_variables

cypress_tests_group2A:
  stage: automated UI/Functional tests - main stage
  tags: 
    - caci
  <<: *default_before_script
  <<: *default_script
  script:
    - npx cypress run --headed --env TAGS='@group2A'
  <<: *default_artifacts
  <<: *default_variables
```
- This allows us to specify what group a test goes into. We add the @group1A or other tags to the feature file where a test is created (see an example below). 
- To further reduce the time we also specify the --spec to be used so that the system does not need to go through every feature file to decide which tests to run. This only saves about 1-2 mins but important for some groups to meet the 15 min goal. 
- Sometimes we don't use --spec if the group is much less then 15 mins. Group1B for example.
- To name groups we assign a number to a feature file, then each scenario has a letter. No hard and fast rule.
- The pipeline occurs in two parts, data setup then the main group of testing

Once you are happy with the testing done by the automation and you wish to clean up you can delete the data created with this endpoint:
https://dev.goldstandard-assurance.org/api/automated-test-projects

Note: a future refactor planned to have the end point called automatically in a clear up stage

Note: gjennings@caci.co.uk has a postman collection to do this and is available on request

## Writing New Tests

Tests are written in Gherkin syntax and placed in \automation\cypress\e2e\features directory. Here’s an example of a feature file, login.feature:

~~~
@group1A
Feature: Login page 

    Scenario: Login page loads
        Given I visit staging
        Then The page is loaded

    Scenario Outline: Login as user (Happy path)
        Given I can see the username & password fields
        When I click the username field
        And I input user "<username>"
        When I click the password field
        And I input pass "<password>"
        When I click login button
        Then I am taken to "<url>"
        And I can see login success message

    Examples:
        | username                | password              | url              |
        | ADMIN_USERNAME          | ADMIN_PASSWORD        | /accounts        |
        | PD_USERNAME             | PD_PASSWORD           | /my-requests     |
~~~
- The above feature file is the human readable format of the test. We can copy out the syntax for clients. 
- The examples table will inject the column / value where it has been mentioned in the above scenario outline.
- The cypress GUI will run each test above (there are 2 tests, however with the examples there is 3 tests, one for each username or unique combination, with more unique combindations the more tests will be run)
- Given, When, And, Then are translated in the steps file.
- Has a group tag to indicate the gitlab job to be included with.

Pages are written in JavaScript, they capture elements on the page to use in steps, loginPage.cy.js:
~~~
class _LoginPage {

    usernameField() {
        return cy.get('[autocomplete="username"]')
    }

    passwordField() {
        return cy.get('[autocomplete="new-password"]')
    }

    loginButton() {
        return cy.get('[type="submit"]')
    }

    successMessage() {
        return cy.get('[fill="var(--toastify-icon-color-success)"]')
    }
}

const loginPage = new _LoginPage()
export default loginPage
~~~

Both the above are brought together in step definitions, this is a snippet example, loginSteps.cy.js:
~~~
import { Given, When, Then, And } from "cypress-cucumber-preprocessor/steps";
import loginPage from "../pages/loginPage.cy.js"

Given ("I visit staging", () => {
    cy.visit('https://staging.gsdevs.de/');
})

Then ("The page is loaded", () => {
    cy.request('/login').then((resp) => {
        expect(resp.status).to.eq(200)
    })
})

Given ("I can see the username & password fields", () => {
    loginPage.usernameField().should('be.visible')
    loginPage.passwordField().should('be.visible')
})  

When ("I click the username field", () => {
    loginPage.usernameField().click()
})

//Documentation: example of how examples in .feature files are translated in step definition files 
And ("I input user {string}", (username) => {
    loginPage.usernameField().type(Cypress.env(username));
})
~~~
- File naming convention: the name must match location its in and the core reason for it being. For example the above are around login, depending on location it is login.feature / loginPage.cy.js / loginSteps.cy.js
- Another example of the naming: editProject.feature / editProjectPage.cy.js / editProjectSteps.cy.js

If you need further information on how cypress works please reach out.

## Contributing & Best Practices

Locators:
- For cypress to be most effective and reliable, especially when interacting with on screen elements, a unique data test id should be added as part of developing features.
- Specifically as direct to the input element as possible, example of this already on the platform:
Inspecting methodology field during project creation: 
~~~
<input aria-invalid="false" autocomplete="off" id=":r10:" type="text" class="MuiInputBase-input
MuiOutlinedInput-input MuiInputBase-inputAdornedEnd MuiAutocomplete-input
MuiAutocomplete-inputFocused css-1uvydh2" aria-autocomplete="list" aria-expanded="false" autocapitalize="none"
spellcheck="false" role="combobox" data-testid="methodology-input-0" value="ACM0001 Flaring or use of landfill gas">
~~~
- As you continue to inspect each methodolgy field and version the data-testid tag is unique to each one selected as well as persistent. 
When inspecting the same fields for request creation we also get similar unique tags, this is merge request example of what the development side looks like:
~~~
https://git.iig.caci.co.uk/gold-standard/front-end/-/merge_requests/682/diffs
~~~

What it looks like being used in cypress (from project creation page file):
~~~
        projectMethodologiesNth : (x) => cy.get(`[data-testid="methodology-input-${x}"]`),
        methodologyVersionNth : (x) => cy.get(`[data-testid="version-input-${x}"]`),
        
        instead of:
        notes : () => cy.get('label').contains("Notes"),
        selectPOA : () => cy.get('label').contains("Select Programme of Activities"),
        selectRealCase : () => cy.get('label').contains("Select Real Case VPA"),
~~~

Practices: 
- One feature file per /page mentioned e.g. the projects list is /projects however editing a project is /update-project. 
- Try to name them as they intended feature, the button to access /update-project is called edit so editProject.feature or editProjectSteps.cy.js or editProjectPage.cy.js 
- Try to avoid excessive use of cy.wait() in milliseconds (best to have cy.wait on API)

Branch handling:
- Create a new branch off main
- Make your changes.
- Commit your changes
- Open merge request to main
- If all pass then merge to main and delete branch

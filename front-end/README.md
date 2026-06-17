# Gold Standard Assurance Platform Front End

### Quickstart

[Install nvm](https://github.com/nvm-sh/nvm#installing-and-updating)  
Node version 20.19.0 is the preferred development version for this project since
it is used in both the pipeline build and the deployed container. Install and set
it to be your default with the following commands:
```
nvm install 20.19.0
nvm alias default 20.19.0
```

Then run `npm install` followed by `npm start` to start the front end in a browser window.

#### Quickstart using development back-end
The instructions above start a front-end that calls to the local back-end. To use
Gold Standard's Development Account, create a file in the root of this repository
named `.env`. In that file, paste the following:
```
VITE_PROXY_TARGET=https://dev.goldstandard-assurance.org
VITE_ENVIRONMENT=development
ESLINT_NO_DEV_ERRORS=true
VITE_GIT_TAG=test-tag-value
```

The front-end will now use the local back-end when started with `npm start`.

### Starting the front-end (React Development Server Build)

1. Run "npm install" at the top level of the project and wait for it to finish.
2. Run "npm start" at the top level of the project, when this has finished "localhost:3000/" should be open in your browser
   - Running npm start should also start the express server on port 3001
3. Connect to the IIG VPN.
4. You will be presented with a Login page, log in with a Cognito user or go to the "Creating a User and Assigning Roles" section to create a new user.
5. You will now be at the user dashboard and ready to start interacting with systems!

### Starting the front-end (React Single Page App Build - use with Cypress)

1. Run "npm install" at the top level of the project and wait for it to finish
2. Run "npm run start:prod-local" at the top level of the project, when this has finished the console will output "Server is running on 3001", you should now be able to access "localhost:3001/" from your browser and login with setup credentials
3. Note: this will point at a local backend which should be running on port 8080, this can be changed in express/server-prod-local.js if needed

### Auto-login with existing test accounts

For easy login, follow these steps:

1. Install [TamperMonkey](https://chromewebstore.google.com/detail/tampermonkey/dhdgffkkebhmkfjojejmpbldmpobfkfo)
2. Copy the UserScript below
3. Click the TamperMonkey extension icon
4. Select "Create a new script..."
5. Replace the default code with the UserScript below
6. Populate the `TEST_ACCOUNT_PASSWORD` variable
7. Save the script and refresh the GoldStandard login page
8. Buttons to auto-login to the different test accounts should appear

<details>
<summary>Click to reveal</summary>

```js
// ==UserScript==
// @name         GoldStandard - Fast Login
// @version      1.2
// @description  Add login buttons to quickly enter dev accounts
// @author       Freddie Chessell
// @match        http://localhost:3000/*
// @match        https://staging.goldstandard-assurance.org/*
// @match        https://dev.goldstandard-assurance.org/*
// @grant        GM_addStyle
// @run-at       document-idle
// ==/UserScript==

const TEST_ACCOUNT_PASSWORD = "";

const LOGIN_BUTTON_SELECTOR = "button[type='submit']";
const FAST_LOGIN_BUTTON_CLASS = "fast-login-button";

class LoginPageHelper {
  constructor() {
    this.loginButton = document.querySelector(LOGIN_BUTTON_SELECTOR);
    this.container = document.querySelector(".fullscreen-form-container");
    this.usernameInput = document.querySelector("input[autocomplete='username']");
    this.passwordInput = document.querySelector("input[type='password']");
  }

  addUserButtons() {
    this.container.appendChild(this._createUserButtons());
  }

  _createUserButtons(username, password) {
    const userButtons = document.createElement("div");
    userButtons.appendChild(this._createUserButton("Admin", TEST_ACCOUNT_PASSWORD));
    userButtons.appendChild(this._createUserButton("Developer", TEST_ACCOUNT_PASSWORD));
    userButtons.appendChild(this._createUserButton("VVB", TEST_ACCOUNT_PASSWORD));
    userButtons.appendChild(this._createUserButton("TAC", TEST_ACCOUNT_PASSWORD));
    userButtons.appendChild(this._createUserButton("ARMTeam", TEST_ACCOUNT_PASSWORD));
    userButtons.appendChild(this._createUserButton("Registry", TEST_ACCOUNT_PASSWORD));
    userButtons.appendChild(this._createUserButton("Finance", TEST_ACCOUNT_PASSWORD));
    return userButtons;
  }

  _createUserButton(username, password) {
    let newButton = document.createElement("input");
    newButton.className = this.loginButton.className;
    newButton.classList.remove("Mui-disabled");
    newButton.classList.add(FAST_LOGIN_BUTTON_CLASS);
    newButton.type = "button";
    newButton.value = username;
    newButton.addEventListener("click", () => {
      this._setReactInputValue(this.usernameInput, username);
      this._setReactInputValue(this.passwordInput, password);
      this.loginButton.click();
    });
    return newButton;
  }

  _setReactInputValue(inputElement, value) {
    let lastValue = inputElement.value;
    inputElement.value = value;
    let event = new Event("input", { bubbles: true });
    // hack React16
    let tracker = inputElement._valueTracker;
    if (tracker) {
      tracker.setValue(lastValue);
    }
    inputElement.dispatchEvent(event);
  }
}

function waitForElement(selector) {
  return new Promise((resolve) => {
    if (document.querySelector(selector)) {
      return resolve(document.querySelector(selector));
    }

    const observer = new MutationObserver((mutations) => {
      if (document.querySelector(selector)) {
        resolve(document.querySelector(selector));
        observer.disconnect();
      }
    });
    observer.observe(document.body, {
      childList: true,
      subtree: true,
    });
  });
}

function continuouslyWaitForElement(selector, callback) {
  const observer = new MutationObserver((mutations) => {
    const desiredElement = document.querySelector(selector);
    if (desiredElement) {
      callback(desiredElement);
    }
  });
  observer.observe(document.body, {
    childList: true,
    subtree: true,
  });
}

(function () {
  "use strict";

  GM_addStyle(`
        .${FAST_LOGIN_BUTTON_CLASS} {
            background: none !important;
            box-shadow: none !important;
            margin: 8px !important;
            border-color: rgb(0, 186, 190) !important;
            color: rgb(0, 186, 190) !important;
        }
        .${FAST_LOGIN_BUTTON_CLASS}:hover {
            background: rgb(0, 186, 190) !important;
            color: #fff !important;
            box-shadow: none !important;
            cursor: pointer;
        }
        .${FAST_LOGIN_BUTTON_CLASS}:active {
            background: rgb(0, 156, 160) !important;
        }
    `);

  continuouslyWaitForElement(LOGIN_BUTTON_SELECTOR, (element) => {
    if (element.textContent != "Login") return;

    const buttonsAlreadyInjected = document.getElementsByClassName(FAST_LOGIN_BUTTON_CLASS).length > 0;
    if (buttonsAlreadyInjected) return;

    let loginPageHelper = new LoginPageHelper();
    loginPageHelper.addUserButtons();
  });
})();
```

</details>

### Creating custom Users and Assigning Roles for local or dev

1. Login to the Gold Standard Amazon Web Services page.
2. Select AWS Account, then Development and then AdministratorAccess.
3. Ensure you are connected to Frankfurt (eu-central-1) in the top right of the page.
4. Search for and open the cognito page this should take you to the "User pool" page, if not open the tab at the left side of the screen and select "User pool".
5. Open the "gs-assurance-user-pool" with the user pool id: `eu-central-1_XIzobZSx7`.
6. Select "Create User" and choose at least a username and temporary password for the new user.
7. Once the user is created, assign the relevant group to your user (the admin role is 'GS-Admin')
8. Log into the front-end with that username and enter a different, permanent password.

## Required dev-tools

- VSCode
- [SonarLint](https://marketplace.visualstudio.com/items?itemName=SonarSource.sonarlint-vscode)
- [Code Spell Checker](https://marketplace.visualstudio.com/items?itemName=streetsidesoftware.code-spell-checker)
- [ESLint code formatter](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint)
- [Jest test runner](https://marketplace.visualstudio.com/items?itemName=Orta.vscode-jest)

### VSCode workspace config

Create the file `.vscode/settings.json` with the following contents:

<details>
<summary>Click to expand</summary>

```json
{
  "typescript.tsdk": "node_modules\\typescript\\lib",
  "jest.runMode": "on-demand",
  "search.exclude": {
    "**/node_modules": true,
    "/src/express/public/**": true
  },
  "eslint.format.enable": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.formatOnSave": true, // disable to prevent auto-formatting
  "editor.formatOnType": true, // disable to prevent auto-formatting
  "[typescriptreact][typescript]": {
    "editor.codeActionsOnSave": [
      "source.addMissingImports", // disable to prevent auto-imports
      "source.organizeImports", // disable to prevent auto import clean-up
      "source.fixAll.eslint" // disable to prevent ESLint auto-formatting (disabling this will break VSCode ESLint warnings)
    ]
  },
  "cSpell.words": [
    "Batchable",
    "collapsable",
    "datepicker",
    "fontawesome",
    "formik",
    "fortawesome",
    "gothambold",
    "gothambook",
    "gsid",
    "Gsids",
    "lcov",
    "lightgray",
    "linted",
    "MICROSCALE",
    "Popover",
    "Revo",
    "revogrid",
    "revolist",
    "Roboto",
    "sass",
    "sonarlint",
    "sonarqube",
    "svgr",
    "testid",
    "Toastify",
    "VPAS",
    "VVBs"
  ]
}
```

</details>

## Contributions

All changes must pass review before being merged. Once merged into main, a pipeline runs to publish the latest image to the ECR development server for wider team use.

## Proxying

- Local proxying happens within the 'vite.config.ts' file which forwards /api to dev
- See `.env.example` to forward requests to a locally ran backend
- In deployment (Dev, Staging and Production), proxying happens with NGinx and is setup within the dockerfile image build to proxy the /ui to express
- Production API proxying happens on the ALB within AWS.

## Testing

Our testing uses Jest, we aim to test each component we implement and to cover all use cases for the front-end. Testing should be done using mocked data and mock api functions, with no personally identifiable information being used.

## Linting

All code must pass ESLint checks on the pipeline before being merged. Code linting is also validated with a pre-commit Husky hook (setup in `package.json`)

## Local Optimisation

Vite uses optimizeDeps (in `vite.config.ts`) to pre-bundle dependencies with esbuild for faster dev server startup. This section is useful when you need to explicitly include or exclude certain packages from optimisation (e.g., packages using non-standard exports or dynamic imports). Adjusting this can help fix issues like slow starts or runtime errors caused by unoptimized dependencies.

## Using ENV VARS in VITE with FE Logic + JEST

Jest does not support import.meta.env because it does not use Vite’s build system, and import.meta is not valid in Node.js. This means that any direct use of import.meta.env in components or modules will cause tests to fail with syntax errors.

To ensure components or utilities using environment variables remain testable, always access import.meta.env through a function utility or helper file, we currently have stored them in /env/env.ts

i.e.
export const isProduction = () => import.meta.env.VITE_ENVIRONMENT === "production";

Then we can mock them in JEST such as:

jest.mock("../../env", () => ({
isProduction: jest.fn(() => false),
}));

## Vite Production Build Chunk-Optimisation

We are using rollup manual chunking in the (`chunking.ts`) file, which packages our code into small parts to load rather than multiple large files

This is configured in (`vite.config.ts`)

We can use https://www.npmjs.com/package/rollup-plugin-visualizer to visualise our current build-package, what is in each build chunk and the sizes of each package and their dependencies

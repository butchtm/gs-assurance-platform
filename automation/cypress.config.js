  const { defineConfig } = require("cypress");
const cucumber = require("cypress-cucumber-preprocessor").default;
require('dotenv').config();

module.exports = defineConfig({
  e2e: {
    baseUrl: process.env.CYPRESS_BASE_URL || "https://dev.goldstandard-assurance.org/",
    userAgent: "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36",
    specPattern: "**/*.feature",
    setupNodeEvents(on, config) {
      on("task", {
        log(message) {
          console.log(message)
          return null
        }
      })
      on("before:browser:launch", (browser = {}, launchOptions) => {
        if (browser.family === "chromium") {
          launchOptions.args.push("--disable-gpu");
          launchOptions.args.push("--disable-dev-shm-usage");
          launchOptions.args.push("--disable-software-rasterizer");
        }
        return launchOptions;
      });
      on("file:preprocessor", cucumber());

      config.env.ADMIN_USERNAME = process.env.ADMIN_USERNAME || config.env.ADMIN_USERNAME;
      config.env.ADMIN_PASSWORD = process.env.ADMIN_PASSWORD || config.env.ADMIN_PASSWORD;
      config.env.PD_USERNAME = process.env.PD_USERNAME || config.env.PD_USERNAME;
      config.env.PD_PASSWORD = process.env.PD_PASSWORD || config.env.PD_PASSWORD;
      config.env.ARM_USERNAME = process.env.ARM_USERNAME || config.env.ARM_USERNAME;
      config.env.ARM_PASSWORD = process.env.ARM_PASSWORD || config.env.ARM_PASSWORD;
      config.env.VVB_USERNAME = process.env.VVB_USERNAME || config.env.VVB_USERNAME;
      config.env.VVB_PASSWORD = process.env.VVB_PASSWORD || config.env.VVB_PASSWORD;
      config.env.FINANCE_USERNAME = process.env.FINANCE_USERNAME || config.env.FINANCE_USERNAME;
      config.env.FINANCE_PASSWORD = process.env.FINANCE_PASSWORD || config.env.FINANCE_PASSWORD;
      config.env.REVIEWER_USERNAME = process.env.REVIEWER_USERNAME || config.env.REVIEWER_USERNAME;
      config.env.REVIEWER_PASSWORD = process.env.REVIEWER_PASSWORD || config.env.REVIEWER_PASSWORD;
      config.env.TAC_USERNAME = process.env.TAC_USERNAME || config.env.TAC_USERNAME;
      config.env.TAC_PASSWORD = process.env.TAC_PASSWORD || config.env.TAC_PASSWORD;
      
      on('after:spec', (spec, results) => {
        if (results.stats.failures > 0)
        {
          console.log(`Failed Tests in ${spec.name}`);
          if (spec.name.includes('dataSetup.feature')) {
            console.log('A data setup test has failed and will now nuke current test data')
          }
        }
      })

      return config;
    },
    //scrollBehavior: false
    browser: 'chrome',
    /*retries: {
      runMode: 2, 
      openMode: 2
    },*/
  },
  reporter: 'junit',
  reporterOptions: {
    mochaFile: `reporting/${process.env.CI_JOB_NAME}/test-results.xml`,
    toConsole: true
  },
  defaultCommandTimeout: 12000,
  pageLoadTimeout: 120000
});

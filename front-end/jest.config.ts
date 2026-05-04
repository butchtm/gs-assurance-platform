import type { Config } from "jest";

const esModules = [
  "^@aws-sdk/client-cognito-identity-provider",
  "formik-mui",
  "@mui",
  "@emotion",
  "react-quill-new",
  "quill",
  "lodash",
  "parchment",
  "@babel/runtime",
].join("|");

const config: Config = {
  roots: ["<rootDir>/src"],
  transform: {
    "^.+\\.tsx?$": "ts-jest",
    "^.+\\.jsx?$": "babel-jest", // Handle ESM files
  },
  testRegex: "(/__tests__/.*|(\\.|/)(test|spec))\\.tsx?$",
  moduleFileExtensions: ["ts", "tsx", "js", "jsx", "json", "node"],
  collectCoverage: true,
  coverageReporters: ["json", "lcov", "text", "clover"],
  moduleNameMapper: {
    "\\.(css|scss)$": "<rootDir>/jest/__mocks__/styleMock.ts",
    "\\.(jpg|jpeg|png|gif|webp|svg)$": "<rootDir>/jest/__mocks__/svgMock.ts",
    "react-markdown": "<rootDir>/src/mock/ReactMarkdownMock.tsx",
    "^uuid$": "uuid",
  },
  testResultsProcessor: "jest-sonar-reporter",
  testEnvironment: "jsdom",
  // Transform selected ESM packages (MUI v5, emotion, date pickers, formik-mui)
  transformIgnorePatterns: [`<rootDir>/node_modules/(?!(^${esModules}))`],
  setupFilesAfterEnv: ["<rootDir>/src/setupTests.ts"],
  coverageThreshold: {
    global: {
      branches: 37,
      functions: 41,
      lines: 57,
      statements: 57,
    },
  },
};

export default config;

"use strict";

const { FlatCompat } = require("@eslint/eslintrc");
const js = require("@eslint/js");
const tsParser = require("@typescript-eslint/parser");
const tsPlugin = require("@typescript-eslint/eslint-plugin");
const reactPlugin = require("eslint-plugin-react");
const reactHooksPlugin = require("eslint-plugin-react-hooks");
const importPlugin = require("eslint-plugin-import");

const compat = new FlatCompat({
  baseDirectory: __dirname,
  recommendedConfig: js.configs.recommended,
  allConfig: js.configs.all,
});

module.exports = [
  {
    ignores: ["node_modules", "build", "dist", "coverage", "jest.config.ts", "vite.config.ts", "jest/*", "chunking.ts"],
  },

  ...compat.extends(
    "eslint:recommended",
    "plugin:react/recommended",
    "plugin:@typescript-eslint/recommended",
    "plugin:prettier/recommended",
    "plugin:import/errors",
    "plugin:import/warnings",
    "plugin:import/typescript",
  ),

  {
    files: ["**/*.{ts,tsx}"],

    languageOptions: {
      parser: tsParser,
      parserOptions: {
        project: "./tsconfig.json",
        sourceType: "module",
        ecmaVersion: 2018,
        ecmaFeatures: {
          jsx: true,
        },
      },
      globals: {
        browser: true,
        commonjs: true,
        es6: true,
        jest: true,
      },
    },

    plugins: {
      "@typescript-eslint": tsPlugin,
      react: reactPlugin,
      "react-hooks": reactHooksPlugin,
      import: importPlugin,
    },

    settings: {
      "import/resolver": {
        typescript: { project: "./tsconfig.json" },
        node: {
          extensions: [".js", ".jsx", ".ts", ".tsx"],
        },
      },
      react: {
        version: "detect",
      },
    },

    rules: {
      "require-await": "error",
      "import/order": [
        "error",
        {
          groups: ["builtin", "external", "internal", "parent", "sibling", "index"],
          "newlines-between": "always",
        },
      ],
      "no-debugger": "error",
      "no-console": ["warn", { allow: ["warn", "error"] }],
      "no-restricted-syntax": [
        "error",
        {
          selector: "MemberExpression[object.name='window'][property.name='location']",
          message: "Use useNavigation or useLocation from react-router-dom to query and change the browser URL",
        },
        {
          selector: "MemberExpression[object.name='screen'][property.name='debug']",
          message: "Avoid leaving debugging code",
        },
      ],
      "react/react-in-jsx-scope": "off",
      "react/jsx-uses-react": "off",
      "@typescript-eslint/no-unused-vars": [
        "error",
        {
          argsIgnorePattern: "^_",
          varsIgnorePattern: "^_",
          caughtErrorsIgnorePattern: "^_",
        },
      ],
    },
  },
  {
    files: ["**/*.js"],
    languageOptions: {
      sourceType: "commonjs",
      globals: {
        commonjs: true,
        __dirname: "readonly",
        __filename: "readonly",
        require: "readonly",
        module: "readonly",
        console: "readonly",
      },
    },
    rules: {
      "@typescript-eslint/no-require-imports": "off",
    },
  },
  {
    files: ["**/*.cjs"],
    languageOptions: {
      sourceType: "commonjs",
      globals: {
        commonjs: true,
        __dirname: "readonly",
        __filename: "readonly",
      },
    },
    rules: {
      "@typescript-eslint/no-require-imports": "off",
    },
  },
  {
    files: ["**/*.d.ts"],
    rules: {
      "@typescript-eslint/no-unused-vars": "off",
    },
  },
];

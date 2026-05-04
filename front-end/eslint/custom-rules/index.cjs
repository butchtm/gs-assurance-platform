module.exports = {
  rules: {
    "no-waitfor-without-await": require("./rules/no-waitfor-without-await.cjs"),
    "no-anchor-without-blank": require("./rules/no-anchor-without-blank.cjs"),
    "no-mui-link": require("./rules/no-mui-link.cjs"),
    "no-direct-dayjs": require("./rules/no-direct-dayjs.cjs"),
    "require-data-test-id": require("./rules/require-datatest-id-on-inputs.cjs"),
  },
};

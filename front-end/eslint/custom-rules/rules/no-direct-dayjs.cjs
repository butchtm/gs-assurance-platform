module.exports = {
  meta: {
    type: "problem",
    docs: {
      description: "disallow direct use of dayjs constructor",
      category: "Best Practices",
      recommended: false,
    },
    schema: [],
  },
  create(context) {
    return {
      CallExpression(node) {
        if (node.callee.name === "dayjs" && node.arguments.length > 0) {
          context.report({
            node,
            message: "Use 'dayjs.utc()' instead of 'dayjs()' constructor, GS prefer UTC time.",
          });
        }
      },
    };
  },
};

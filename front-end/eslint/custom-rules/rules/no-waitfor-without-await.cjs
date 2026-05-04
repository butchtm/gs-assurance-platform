module.exports = {
  meta: {
    type: "problem",
    docs: {
      description: "waitFor must be awaited as an async method",
      category: "Best Practices",
      recommended: false,
    },
    schema: [],
  },
  create(context) {
    return {
      CallExpression(node) {
        if (node.callee.name === "waitFor") {
          const nodeBefore = context.sourceCode.getTokenBefore(node);        
          if (nodeBefore.value !== "await") {
            context.report({
              node,
              message: "waitFor must be awaited as it is an asynchronous method",
            });
          }
        }
      },
    };
  },
};

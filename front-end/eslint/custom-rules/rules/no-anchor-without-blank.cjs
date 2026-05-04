module.exports = {
  meta: {
    type: "problem",
    docs: {
      description: "disallow <a> elements with href attribute without target='_blank'",
      category: "Possible Errors",
      recommended: true,
    },
    schema: [],
  },
  create(context) {
    return {
      JSXOpeningElement(node) {
        if (node.name.name !== "a") return;

        const hrefAttribute = node.attributes.find((attr) => attr.name?.name === "href");
        if (!hrefAttribute) return;

        const isMailerLink = hrefAttribute.value?.value?.startsWith("mailto:");
        if (isMailerLink) return;

        const hasTargetBlank = node.attributes.some(
          (attr) => attr.name?.name === "target" && attr.value?.value === "_blank",
        );
        if (hasTargetBlank) return;

        context.report({
          node,
          message: "<a href='...'> causes a full app reload, use react-router-dom's <Link> instead",
        });
      },
    };
  },
};

"use strict";

function getJSXElementName(node) {
  if (node.type === "JSXIdentifier") {
    return node.name;
  }
  if (node.type === "JSXMemberExpression") {
    return `${getJSXElementName(node.object)}.${getJSXElementName(node.property)}`;
  }
  return null;
}

module.exports = {
  meta: {
    type: "suggestion",
    docs: {
      description:
        "require a data-testid or data-test-id on all input fields, buttons, TextField, and Formik Field+TextField",
      category: "Testing",
      recommended: false,
    },
    schema: [],
    messages: {
      missingTestId: "JSX <{{name}}> must have a `data-testid` or `data-test-id` attribute for testing.",
    },
  },

  create(context) {
    const TARGET_HTML = new Set(["input"]);
    const TARGET_COMPONENTS = new Set(["TextField", "Select", "FormControlLabel"]);

    return {
      JSXOpeningElement(node) {
        const name = getJSXElementName(node.name);

        // detect Formik Field with component={TextField}
        let isFieldWithTextField = false;
        if (name === "Field") {
          const compAttr = node.attributes.find((a) => a.type === "JSXAttribute" && a.name.name === "component");
          if (
            compAttr &&
            compAttr.value &&
            compAttr.value.expression &&
            compAttr.value.expression.type === "Identifier" &&
            compAttr.value.expression.name === "TextField"
          ) {
            isFieldWithTextField = true;
          }
        }

        // skip anything not in our targets
        const isTarget = TARGET_HTML.has(name) || TARGET_COMPONENTS.has(name) || isFieldWithTextField;
        if (!isTarget) return;

        // 1) direct data-testid / data-test-id
        const hasDirect = node.attributes.some(
          (attr) =>
            attr.type === "JSXAttribute" && (attr.name.name === "data-testid" || attr.name.name === "data-test-id"),
        );

        // 2) via props object: inputProps/InputProps on TextField or Field+TextField
        let hasViaProps = false;
        if (TARGET_COMPONENTS.has(name) || isFieldWithTextField) {
          for (const propName of ["inputProps", "InputProps"]) {
            if (hasViaProps) break;
            const propsAttr = node.attributes.find(
              (attr) => attr.type === "JSXAttribute" && attr.name.name === propName,
            );
            if (
              propsAttr &&
              propsAttr.value &&
              propsAttr.value.expression &&
              propsAttr.value.expression.type === "ObjectExpression"
            ) {
              hasViaProps = propsAttr.value.expression.properties.some((prop) => {
                if (prop.type !== "Property") return false;
                const key = prop.key;
                return (
                  (key.type === "Literal" && key.value === "data-testid") ||
                  (key.type === "Identifier" && key.name === "data-testid")
                );
              });
            }
          }
        }

        if (!hasDirect && !hasViaProps) {
          context.report({
            node,
            messageId: "missingTestId",
            data: { name },
          });
        }
      },
    };
  },
};

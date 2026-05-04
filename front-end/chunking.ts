export function manualChunks(id: string): string | null {
  // Application chunks
  if (id.includes("/src/common/env/")) return "env";
  if (id.includes("/src/components/Project/")) return "project";
  if (id.includes("/src/components/requests/")) return "requests";
  if (id.includes("/src/components/create-request/")) return "create-request";
  if (id.includes("/src/components/Public/")) return "public";
  if (id.includes("/src/components/User/")) return "user";
  if (id.includes("/src/components/Administration/")) return "reference-type";
  if (id.includes("/src/components/Formik/")) return "formik-components";
  if (id.includes("/src/components/Reusable/")) return "reusable";
  // fallback for anything else in components
  if (id.includes("/src/components/")) return "components";

  if (
    id.includes("node_modules/react/") ||
    id.includes("node_modules/react-dom/") ||
    id.includes("node_modules/scheduler/")
  ) {
    return "react";
  }

  if (id.includes("node_modules/lodash-es/") || id.includes("node_modules/lodash/")) {
    return "lodash";
  }

  if (id.includes("node_modules/react-router/") || id.includes("node_modules/react-router-dom/")) {
    return "react-router";
  }

  if (
    id.includes("node_modules/react-select/") ||
    id.includes("node_modules/react-toastify/") ||
    id.includes("node_modules/react-window/")
  ) {
    return "ui-addons";
  }

  if (id.includes("node_modules/axios/") || id.includes("node_modules/follow-redirects/")) {
    return "http";
  }

  if (id.includes("node_modules")) {
    return "vendor";
  }

  return null;
}

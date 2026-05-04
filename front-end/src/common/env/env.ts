type EnvVarsType = Pick<ImportMetaEnv, "VITE_GIT_TAG" | "VITE_ENVIRONMENT">;

// Environment Variable Template to Be Replaced at Runtime
const envVars: EnvVarsType = {
  VITE_GIT_TAG: "${VITE_GIT_TAG}",
  VITE_ENVIRONMENT: "${VITE_ENVIRONMENT}",
};

export const getGitTag = () => {
  return getEnvVarValue("VITE_GIT_TAG");
};

export const isEndConsultationEnabled = () => {
  return !isEnvironment(["production"]);
};

export const isFormSandboxEnabled = () => {
  return isEnvironment(["local", "development"]);
};

export const isFormEnabled = () => {
  return !isEnvironment(["production"]);
};

export const getEnvironment = () => {
  return getEnvVarValue("VITE_ENVIRONMENT");
};

const isEnvironment = (environments: string[]) => {
  return environments.includes(getEnvVarValue("VITE_ENVIRONMENT"));
};

const getEnvVarValue = (key: keyof EnvVarsType) => {
  const template = envVars[key];
  if (template.includes("VITE_")) {
    return import.meta.env[key];
  }
  return template;
};

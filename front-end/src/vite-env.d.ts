export {};

interface ImportMetaEnv {
  readonly VITE_GIT_TAG: string;
  readonly VITE_ENVIRONMENT: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}

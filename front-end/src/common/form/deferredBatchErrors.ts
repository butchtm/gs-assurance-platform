import { CreateRequestForm } from "../../components/requests/create-request/Wizard/Request";

interface MinimalFieldMeta {
  touched?: boolean;
  error?: string;
}

interface DeferredBatchErrorArgs {
  meta?: MinimalFieldMeta;
  formValues: CreateRequestForm;
  submitCount: number;
  required: boolean;
  isMissing: boolean;
  requiredMessage: string;
  deferUntilFinal?: boolean;
}

interface DeferredBatchErrorResult {
  showError: boolean;
  errorMessage?: string;
}

export function computeDeferredBatchError({
  meta,
  formValues,
  submitCount,
  required,
  isMissing,
  requiredMessage,
  deferUntilFinal = true,
}: DeferredBatchErrorArgs): DeferredBatchErrorResult {
  const baseline = formValues.batchStepProjectValues?.finalProjectInitialSubmitCount;
  const pastFinalSubmit = deferUntilFinal && baseline !== undefined && submitCount > baseline;

  if (meta?.error && (meta.touched || pastFinalSubmit)) {
    return { showError: true, errorMessage: meta.error };
  }

  if (pastFinalSubmit && required && isMissing) {
    return { showError: true, errorMessage: requiredMessage };
  }

  return { showError: false };
}

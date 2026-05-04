import { RJSFSchema, UiSchema } from "@rjsf/utils";

import { apiClient } from "../APIClient/Client";
import { ReferenceType } from "../../common/data-types/DataReferenceTypes";

interface StepFormObjectData {
  [key: string]: unknown;
}

export type StepFormData = StepFormObjectData | StepFormObjectData[];

export interface CompleteFormData {
  [stepKey: string]: StepFormData;
}

export interface StepConfig {
  title: string;
  description?: string;
  schema: RJSFSchema;
  uiSchema: UiSchema;
}

export interface RequestForm {
  id: number;
  version: number;
  type: ReferenceType;
  schema: { steps: StepConfig[] };
  data?: StepFormData[];
  status: ReferenceType;
  createdAt: string;
  updatedAt?: string;
  submittedAt?: string;
}

export const getAllRequestForms = async (requestId: number) => {
  try {
    return await apiClient.makeRequest(`/requests/${requestId}/forms`, "GET");
  } catch (error) {
    console.error("Unexpected error retrieving request form data:", error);
    return;
  }
};

export const createForm = async (requestId: number, formTypeId?: number) => {
  try {
    return await apiClient.makeRequest(`/requests/${requestId}/forms`, "POST", JSON.stringify({ typeId: formTypeId }));
  } catch (error) {
    console.error("Unexpected error creating form:", error);
    return;
  }
};

export const saveForm = async (requestId: number, formData: StepFormData[], formId?: number) => {
  try {
    await apiClient.makeRequest(`/requests/${requestId}/forms/${formId}`, "PUT", JSON.stringify({ data: formData }));
  } catch (error) {
    console.error("Unexpected error saving form:", error);
    return;
  }
};

export const submitForm = async (requestId: number, formData: StepFormData[], formId?: number) => {
  try {
    await apiClient.makeRequest(
      `/requests/${requestId}/forms/${formId}/submissions`,
      "POST",
      JSON.stringify({ data: formData }),
    );
  } catch (error) {
    console.error("Unexpected error submitting form:", error);
    return;
  }
};

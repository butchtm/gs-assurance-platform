import { toast } from "react-toastify";

import { ReferenceType } from "../../../common/data-types/DataReferenceTypes";
import { apiClient } from "../../APIClient/Client";

export interface MethodologyReferenceType extends ReferenceType {
  inUse: boolean;
}

interface MethodologyResponse {
  totalElements: number;
  content: MethodologyReferenceType[];
}

export interface EditableMethodologyValues {
  description: string;
  active: boolean;
  id?: number;
}

export const getMethodologies = (search: string, page: number, pageSize: number): Promise<MethodologyResponse> => {
  const filterParams = new URLSearchParams({});
  if (search) {
    filterParams.append("description", search);
  }
  return apiClient.makeRequest("/methodologies/pages", "GET", "{}", {
    page,
    pageSize,
    filters: filterParams,
  });
};

export const getAllMethodologies = (): Promise<MethodologyReferenceType[]> => {
  return apiClient.makeRequest("/methodologies", "GET", "{}");
};

export const createMethodology = async (body: EditableMethodologyValues) => {
  try {
    await apiClient.makeRequest(`/methodologies`, "POST", JSON.stringify(body));
  } catch (error) {
    console.error(error);
    toast.error("Failed to create methodology");
  }
};

export const updateMethodology = async (body: EditableMethodologyValues) => {
  try {
    await apiClient.makeRequest(`/methodologies/${body.id}`, "PUT", JSON.stringify(body));
  } catch (error) {
    console.error(error);
    toast.error("Failed to update methodology");
  }
};

export const deleteMethodology = async (id: number) => {
  try {
    await apiClient.makeRequest(`/methodologies/${id}`, "DELETE", "{}");
  } catch (error) {
    console.error(error);
    toast.error("Failed to delete methodology");
  }
};

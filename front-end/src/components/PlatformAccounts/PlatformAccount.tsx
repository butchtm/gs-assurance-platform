import { PagedResource } from "../../common/filterable-sortable-table/model/PagedResource";
import { apiClient } from "../APIClient/Client";

import { PlatformAccount, UpdateCreateCompetenciesPayload } from "./PlatformAccountTypes";

export type AccountFilters = {
  name?: string;
  active?: boolean;
  assignmentTypeId?: number;
  accountTypeIds?: number[];
};

export async function getAccounts(
  filters?: AccountFilters,
  page?: number,
  pageSize?: number,
): Promise<PagedResource<PlatformAccount[]>> {
  try {
    const queryParams = new URLSearchParams({});

    if (filters) {
      Object.entries(filters).forEach(([key, value]) => {
        queryParams.set(key, value.toString());
      });
    }

    if (!page) {
      page = 1;
    }

    if (!pageSize) {
      pageSize = 99999;
    }

    return await apiClient.makeRequest("/accounts", "GET", undefined, {
      page,
      pageSize,
      filters: queryParams,
    });
  } catch (error) {
    console.error("Failed to fetch accounts from API", error);
    throw error;
  }
}

export function getAccount(platformAccountId: number): Promise<PlatformAccount> {
  return apiClient.makeRequest(`/accounts/${platformAccountId}`, "GET");
}

interface UpdateCreatePayload {
  name: string | undefined;
  emailAddress: string | undefined;
  active: boolean | undefined;
  address: string | undefined;
  accountTypeId: number | undefined;
  userIds: number[];
}

export const updateAccount = async (payload: UpdateCreatePayload, accountId: number) => {
  try {
    await apiClient.makeRequest(`/accounts/${accountId}`, "PUT", JSON.stringify(payload));
  } catch (error) {
    console.error("Failed to update account", error);
    throw error;
  }
};

export const createAccount = async (payload: UpdateCreatePayload) => {
  try {
    await apiClient.makeRequest(`/accounts`, "POST", JSON.stringify(payload));
  } catch (error) {
    console.error("Failed to create account", error);
    throw error;
  }
};

export const getAccountCompetencies = (accountId: number): Promise<UpdateCreateCompetenciesPayload> => {
  return apiClient.makeRequest("/accounts/" + accountId + "/competencies", "GET");
};

export const updateAccountCompetencies = async (payload: UpdateCreateCompetenciesPayload, accountId: number) => {
  return await apiClient.makeRequest(`/accounts/${accountId}/competencies`, "PUT", JSON.stringify(payload));
};

export const createAccountCompetencies = async (payload: UpdateCreateCompetenciesPayload, accountId: number) => {
  return await apiClient.makeRequest(`/accounts/${accountId}/competencies`, "POST", JSON.stringify(payload));
};

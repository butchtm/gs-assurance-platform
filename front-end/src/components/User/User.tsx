import { MRT_SortingState } from "material-react-table";

import { PermissionGroup } from "../../PermissionGroup";
import { apiClient } from "../APIClient/Client";
import { Account } from "../PlatformAccounts/PlatformAccountTypes";

export interface User {
  id: number;
  userName: string;
  emailAddress: string;
  isActive: boolean;
  account?: Account;
  givenName: string;
  familyName: string;
  groups: PermissionGroup[];
}

interface GetUsersResponse {
  content: User[];
  currentPage: number;
  totalPages: number;
  totalElements: number;
}

export interface BatchUserResponse {
  id: number;
  errors: BatchUserError[];
  createdAt: string;
  updatedAt: string;
  complete: boolean;
  count: number;
  successCount: number;
}

export interface BatchUserError {
  userName: string;
  message: string;
  account: string;
}

export function formatName(given: string, family: string): string {
  if (given || family) {
    return `${given} ${family}`.trim();
  }
  return "";
}

export async function getAllActiveUsers(): Promise<User[]> {
  try {
    const data = await apiClient.makeRequest("/users", "GET");
    return (data as User[]).filter((user) => user.isActive);
  } catch (_error) {
    throw new Error("Failed to fetch active users");
  }
}

export async function getUsers(
  page: number,
  pageSize: number,
  sortingState: MRT_SortingState,
  searchQuery: string,
  filterByActive?: boolean,
): Promise<GetUsersResponse> {
  try {
    const queryParams = new URLSearchParams();
    queryParams.set("page", page.toString());
    queryParams.set("size", pageSize.toString());
    queryParams.set("searchTerm", searchQuery);

    if (filterByActive !== undefined) {
      queryParams.set("active", filterByActive.toString());
    }

    if (sortingState.length > 0) {
      const directionString = sortingState[0].desc ? "desc" : "asc";
      queryParams.set("sort", `${sortingState[0].id},${directionString}`);
    }

    return await apiClient.makeRequest(`/users/pages?${queryParams.toString()}`, "GET");
  } catch (_error) {
    throw new Error("Failed to fetch users");
  }
}

export function getUser(cognitoId: string): Promise<User | null> {
  const queryParams = new URLSearchParams({ idpUserId: cognitoId });
  return apiClient.makeRequest(`/user?${queryParams.toString()}`, "GET");
}

export function uploadBatchUsers(csvFile: File): Promise<BatchUserResponse> {
  const data = new FormData();
  const csvFileForceType = new File([csvFile], csvFile.name, {
    type: "text/csv",
  });
  data.append("data", csvFileForceType);
  return apiClient.makeRequest("/users/batches", "POST", data);
}

export function getBatchStatus(id: number): Promise<BatchUserResponse> {
  return apiClient.makeRequest(`/users/batches/${id}`, "GET");
}

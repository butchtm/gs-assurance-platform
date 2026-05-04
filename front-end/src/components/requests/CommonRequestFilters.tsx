import { Box } from "@mui/material";
import React, { ReactNode, useContext, useEffect, useState } from "react";

import { AccountTypeIds } from "../../AccountType";
import { useReferenceTypes } from "../../common/data-types/DataReferenceTypes";
import { PermissionGroup } from "../../PermissionGroup";
import ProtectedComponent from "../../ProtectedComponent";
import { AuthContext } from "../Auth/Login/AuthProvider";
import { getAccounts } from "../PlatformAccounts/PlatformAccount";
import ListFilterAutoComplete from "../Reusable/Filter/ListFilterAutoComplete";
import ProjectSearchTextBox from "../Reusable/Filter/ProjectSearchTextBox";

import { getAssignedRequestAccounts, getAssignedRequestTypes } from "./Requests";

export interface DashboardFilters {
  requestStatusIds?: number[];
  projectAccountIds?: number[];
  projectIds?: number[];
  projectSearch?: string;
  vvbAccountIds?: number[];
  reviewerAccountIds?: number[];
  invoiceStatusIds?: number[];
  requestTypeIds?: number[];
  sort?: string;
}

export interface FilterOptions {
  value: number | string;
  label: string;
}

interface DashboardFilterBlockProps {
  filters: DashboardFilters;
  onFilterChange: (name: keyof DashboardFilters, values: number[]) => void;
  isAllRequests: boolean;
  onSearchChange: (name: keyof DashboardFilters, value: string) => void;
  topToolbarCustomActions?: ReactNode;
}

const CommonRequestFilters: React.FC<DashboardFilterBlockProps> = ({
  filters,
  onFilterChange,
  isAllRequests,
  onSearchChange,
  topToolbarCustomActions,
}) => {
  const { isArmUser } = useContext(AuthContext);
  const [statusOptions, setStatusOptions] = useState<FilterOptions[]>([]);
  const [developerOptions, setDeveloperOptions] = useState<FilterOptions[]>([]);
  const [reviewerOptions, setReviewerOptions] = useState<FilterOptions[]>([]);
  const [vvbOptions, setVvbOptions] = useState<FilterOptions[]>([]);
  const [requestTypeOptions, setRequestTypeOptions] = useState<FilterOptions[]>([]);
  const { activeReferenceTypes } = useReferenceTypes();

  const getAccountOptions = async (accountTypeId: number) => {
    if (isAllRequests) {
      const filters = { accountTypeIds: [accountTypeId] };
      const accounts = await getAccounts(filters);
      return accounts ? accounts.content.map((account) => ({ value: account.id, label: account.name })) : [];
    } else {
      const accounts = await getAssignedRequestAccounts(accountTypeId);
      return accounts ? accounts.map((account) => ({ value: account.id, label: account.name })) : [];
    }
  };

  const getStatusOptions = () => {
    return activeReferenceTypes.RequestStatus.map((status) => ({ value: status.id, label: status.description }));
  };

  const getRequestTypeOptions = async () => {
    if (isArmUser()) {
      const requestTypes = await getAssignedRequestTypes();
      return requestTypes.map((type) => ({ value: type.id, label: type.description }));
    }
    return [];
  };

  useEffect(() => {
    const loadData = async () => {
      const [fetchedDeveloperOptions, fetchedReviewerOptions, fetchedVvbOptions, fetchedRequestTypeOptions] =
        await Promise.all([
          getAccountOptions(AccountTypeIds.PROJECT_DEVELOPER),
          getAccountOptions(AccountTypeIds.GS_REVIEWER),
          getAccountOptions(AccountTypeIds.VVB),
          getRequestTypeOptions(),
        ]);

      setDeveloperOptions(fetchedDeveloperOptions);
      setReviewerOptions(fetchedReviewerOptions);
      setVvbOptions(fetchedVvbOptions);
      setStatusOptions(getStatusOptions());
      setRequestTypeOptions(fetchedRequestTypeOptions);
    };

    loadData();
  }, [isAllRequests]);

  const handleAutocompleteChange = (
    name: keyof DashboardFilters,
    event: React.SyntheticEvent<Element, Event>,
    newValue: FilterOptions[] | null,
  ) => {
    const selectedValues = newValue ? newValue.map((option) => option.value) : [];
    onFilterChange(name, selectedValues as number[]);
  };

  const getValueFromFilters = (filterValues: number[], options: FilterOptions[]) => {
    return filterValues
      .map((value) => options.find((option) => option.value === value))
      .filter(Boolean) as FilterOptions[];
  };

  const drawCustomActions = () => {
    return (
      <Box display="flex" flexDirection="row">
        {topToolbarCustomActions}
      </Box>
    );
  };

  return (
    <Box className="search-container">
      <Box display="flex" flexDirection="row" gap={2}>
        {topToolbarCustomActions && drawCustomActions()}
        <ListFilterAutoComplete
          id="request-status-filter"
          label="Request Status"
          options={statusOptions}
          value={(filters.requestStatusIds && getValueFromFilters(filters.requestStatusIds, statusOptions)) || []}
          onChange={(event, newValue) => handleAutocompleteChange("requestStatusIds", event, newValue)}
        />
        <ProtectedComponent roles={[PermissionGroup.ARM_TEAM]}>
          <ListFilterAutoComplete
            id="request-type-filter"
            label="Request Type"
            options={requestTypeOptions}
            value={(filters.requestTypeIds && getValueFromFilters(filters.requestTypeIds, requestTypeOptions)) || []}
            onChange={(event, newValue) => handleAutocompleteChange("requestTypeIds", event, newValue)}
          />
        </ProtectedComponent>
        <ListFilterAutoComplete
          id="developer-filter"
          label="Developer"
          options={developerOptions}
          value={(filters.projectAccountIds && getValueFromFilters(filters.projectAccountIds, developerOptions)) || []}
          onChange={(event, newValue) => handleAutocompleteChange("projectAccountIds", event, newValue)}
        />
        <ProtectedComponent roles={[PermissionGroup.GS_ADMIN, PermissionGroup.ARM_TEAM]}>
          <ListFilterAutoComplete
            id="reviewer-filter"
            label="Reviewer"
            options={reviewerOptions}
            value={
              (filters.reviewerAccountIds && getValueFromFilters(filters.reviewerAccountIds, reviewerOptions)) || []
            }
            onChange={(event, newValue) => handleAutocompleteChange("reviewerAccountIds", event, newValue)}
          />
        </ProtectedComponent>
        <ListFilterAutoComplete
          id="vvb-filter"
          label="VVB"
          options={vvbOptions}
          value={(filters.vvbAccountIds && getValueFromFilters(filters.vvbAccountIds, vvbOptions)) || []}
          onChange={(event, newValue) => handleAutocompleteChange("vvbAccountIds", event, newValue)}
        />
        <ProjectSearchTextBox defaultValue={filters.projectSearch} onChange={onSearchChange} />
      </Box>
    </Box>
  );
};

export default CommonRequestFilters;

import { Box, CircularProgress } from "@mui/material";
import React, { useEffect, useState } from "react";

import { AccountTypeIds } from "../../../../AccountType";
import { useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import ListFilterAutoComplete from "../../../Reusable/Filter/ListFilterAutoComplete";
import { getAssignedRequestAccounts } from "../../Requests";
import { DashboardFilters, FilterOptions } from "../../CommonRequestFilters";
import ProjectSearchTextBox from "../../../Reusable/Filter/ProjectSearchTextBox";

interface VVBRequestsFilterBlockProps {
  filters: DashboardFilters;
  onFilterChange: (name: keyof DashboardFilters, values: number[]) => void;
  onSearchChange: (name: keyof DashboardFilters, value: string) => void;
}

const VVBRequestsFilterBlock: React.FC<VVBRequestsFilterBlockProps> = ({ filters, onFilterChange, onSearchChange }) => {
  const [statusOptions, setStatusOptions] = useState<FilterOptions[]>([]);
  const [developerOptions, setDeveloperOptions] = useState<FilterOptions[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const { activeReferenceTypes } = useReferenceTypes();

  const getStatusOptions = () => {
    return activeReferenceTypes.RequestStatus.map((status) => ({ value: status.id, label: status.description }));
  };

  const getAccountOptions = async (accountTypeId: number) => {
    const accounts = await getAssignedRequestAccounts(accountTypeId);
    return accounts
      ? accounts.map((account) => ({
          value: account.id,
          label: account.name,
        }))
      : [];
  };

  useEffect(() => {
    const loadData = async () => {
      const [fetchedDeveloperOptions] = await Promise.all([getAccountOptions(AccountTypeIds.PROJECT_DEVELOPER)]);
      setStatusOptions(getStatusOptions());
      setDeveloperOptions(fetchedDeveloperOptions);
      setIsLoading(false);
    };

    loadData();
  }, []);

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

  if (isLoading) {
    return <CircularProgress />;
  }

  return (
    <Box className="search-container">
      <Box display="flex" flexDirection="row" gap={2}>
        <ListFilterAutoComplete
          id="request-status-filter"
          label="Request Status"
          options={statusOptions}
          value={(filters.requestStatusIds && getValueFromFilters(filters.requestStatusIds, statusOptions)) || []}
          onChange={(event, newValue) => handleAutocompleteChange("requestStatusIds", event, newValue)}
        />
        <ListFilterAutoComplete
          id="developer-filter"
          label="Project Developer"
          options={developerOptions}
          value={(filters.projectAccountIds && getValueFromFilters(filters.projectAccountIds, developerOptions)) || []}
          onChange={(event, newValue) => handleAutocompleteChange("projectAccountIds", event, newValue)}
        />
        <ProjectSearchTextBox defaultValue={filters.projectSearch} onChange={onSearchChange} />
      </Box>
    </Box>
  );
};

export default VVBRequestsFilterBlock;

import { Box } from "@mui/material";
import React from "react";

import ListFilterAutoComplete from "../../../Reusable/Filter/ListFilterAutoComplete";
import { DashboardFilters, FilterOptions } from "../../CommonRequestFilters";
import { useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import ProjectSearchTextBox from "../../../Reusable/Filter/ProjectSearchTextBox";

interface ProjectDeveloperFilterBlockProps {
  filters: DashboardFilters;
  onFilterChange: (name: keyof DashboardFilters, values: (number | string)[]) => void;
  onSearchChange: (name: keyof DashboardFilters, value: string) => void;
}

const ProjectDeveloperFilterBlock: React.FC<ProjectDeveloperFilterBlockProps> = ({
  filters,
  onFilterChange,
  onSearchChange,
}) => {
  const referenceDataContext = useReferenceTypes();
  const requestStatusOptions = referenceDataContext.getCustomReferenceOptions(
    "RequestStatus",
    filters.requestStatusIds ? filters.requestStatusIds : [],
  );

  const handleAutocompleteChange = (
    name: keyof DashboardFilters,
    event: React.SyntheticEvent<Element, Event>,
    newValue: FilterOptions[] | null,
  ) => {
    const selectedValues = newValue ? newValue.map((option) => option.value) : [];
    onFilterChange(name, selectedValues);
  };

  const getValueFromFilters = (filterValues: number[] | string[], options: FilterOptions[]) => {
    return filterValues
      .map((value) => options.find((option) => option.value === value))
      .filter(Boolean) as FilterOptions[];
  };

  return (
    <Box className="search-container">
      <Box display="flex" flexDirection="row" gap={2}>
        <ListFilterAutoComplete
          id="request-status-filter"
          label="Request Status"
          options={requestStatusOptions}
          value={
            (filters.requestStatusIds && getValueFromFilters(filters.requestStatusIds, requestStatusOptions)) || []
          }
          onChange={(event, newValue) => handleAutocompleteChange("requestStatusIds", event, newValue)}
        />
        <ProjectSearchTextBox defaultValue={filters.projectSearch} onChange={onSearchChange} />
      </Box>
    </Box>
  );
};

export default ProjectDeveloperFilterBlock;

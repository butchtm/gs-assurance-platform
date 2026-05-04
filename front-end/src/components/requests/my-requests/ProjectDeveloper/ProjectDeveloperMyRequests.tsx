import { ThemeProvider } from "@mui/material";
import React, { useState } from "react";

import { getReferenceValueIdsByCodes, useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { theme } from "../../../../theme";
import { DashboardFilters } from "../../CommonRequestFilters";

import ProjectDeveloperFilterBlock from "./ProjectDeveloperFilterBlock";
import ProjectDeveloperRequestsTable from "./ProjectDeveloperRequestsTable";

const ProjectDeveloperMyRequests: React.FC = () => {
  const { activeReferenceTypes } = useReferenceTypes();

  const getDefaultRequestStatusIds = () => {
    return getReferenceValueIdsByCodes(["IN_PREPARATION"], activeReferenceTypes.RequestStatus);
  };

  const [filters, setFilters] = useState<DashboardFilters>({
    requestStatusIds: getDefaultRequestStatusIds(),
  });

  const handleFilterChange = (name: keyof DashboardFilters, values: (number | string)[]) => {
    setFilters((prevFilters) => ({
      ...prevFilters,
      [name]: values,
    }));
  };

  const handleSearchChange = (name: keyof DashboardFilters, value: string) => {
    setFilters((prevFilters) => ({
      ...prevFilters,
      [name]: value,
    }));
  };

  return (
    <ThemeProvider theme={theme}>
      <ProjectDeveloperFilterBlock
        filters={filters}
        onFilterChange={handleFilterChange}
        onSearchChange={handleSearchChange}
      />
      <ProjectDeveloperRequestsTable filters={filters} />
    </ThemeProvider>
  );
};

export default ProjectDeveloperMyRequests;

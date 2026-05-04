import { ThemeProvider } from "@mui/material";
import React, { useState } from "react";

import { getReferenceValueIdsByCodes, useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { theme } from "../../../../theme";
import { DashboardFilters } from "../../CommonRequestFilters";

import VVBRequestsFilterBlock from "./VVBRequestsFilterBlock";
import VVBRequestsTable from "./VVBRequestsTable";

const VVBMyRequests: React.FC = () => {
  const { activeReferenceTypes } = useReferenceTypes();

  const getDefaultRequestStatusIds = () => {
    return getReferenceValueIdsByCodes(["IN_AUDIT", "IN_PREPARATION"], activeReferenceTypes.RequestStatus);
  };

  const [filters, setFilters] = useState<DashboardFilters>({
    requestStatusIds: getDefaultRequestStatusIds(),
  });

  const handleFilterChange = (name: keyof DashboardFilters, values: number[]) => {
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
      <VVBRequestsFilterBlock
        filters={filters}
        onFilterChange={handleFilterChange}
        onSearchChange={handleSearchChange}
      ></VVBRequestsFilterBlock>
      <VVBRequestsTable filters={filters} />
    </ThemeProvider>
  );
};

export default VVBMyRequests;

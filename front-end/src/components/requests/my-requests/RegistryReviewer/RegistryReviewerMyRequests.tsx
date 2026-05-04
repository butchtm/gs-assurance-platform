import { Grid, ThemeProvider } from "@mui/material";
import React, { useState } from "react";

import { getReferenceValueIdsByCodes, useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { theme } from "../../../../theme";
import CommonRequestFilters, { DashboardFilters } from "../../CommonRequestFilters";

import RegistryReviewerRequestsTable from "./RegistryReviewerRequestsTable";

const RegistryReviewerMyRequests: React.FC = () => {
  const { activeReferenceTypes } = useReferenceTypes();

  const getDefaultRequestStatusIds = () => {
    return getReferenceValueIdsByCodes(["IN_PROGRESS"], activeReferenceTypes.RequestStatus);
  };

  const [filters, setFilters] = useState<DashboardFilters>({
    requestStatusIds: getDefaultRequestStatusIds(),
    projectIds: [],
    projectAccountIds: [],
    vvbAccountIds: [],
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
      <Grid item xs={12}>
        <CommonRequestFilters
          filters={filters}
          onFilterChange={handleFilterChange}
          onSearchChange={handleSearchChange}
          isAllRequests={false}
        />
      </Grid>
      <Grid item xs={12}>
        <RegistryReviewerRequestsTable filters={filters} />
      </Grid>
    </ThemeProvider>
  );
};

export default RegistryReviewerMyRequests;

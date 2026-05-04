import { Button, ThemeProvider } from "@mui/material";
import React, { useState } from "react";

import { getReferenceValueIdsByCodes, useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { theme } from "../../../../theme";
import CommonRequestFilters, { DashboardFilters } from "../../CommonRequestFilters";

import ARMTeamRequestsTable from "./ARMTeamRequestsTable";

const ARMTeamMyRequests: React.FC = () => {
  const handleSummaryModal = () => {
    setIsSummaryModalOpen((prev) => !prev);
  };
  const { activeReferenceTypes } = useReferenceTypes();
  const [isSummaryModalOpen, setIsSummaryModalOpen] = useState(false);

  const getDefaultRequestStatusIds = () => {
    return getReferenceValueIdsByCodes(
      ["COMPLETENESS_CHECK", "IN_ASSIGNMENT", "IN_ROUND_COMPLETION"],
      activeReferenceTypes.RequestStatus,
    );
  };

  const [filters, setFilters] = useState<DashboardFilters>({
    requestStatusIds: getDefaultRequestStatusIds(),
    projectSearch: "",
    projectAccountIds: [],
    reviewerAccountIds: [],
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
      <CommonRequestFilters
        filters={filters}
        onFilterChange={handleFilterChange}
        isAllRequests={false}
        onSearchChange={handleSearchChange}
        topToolbarCustomActions={
          <Button onClick={handleSummaryModal} variant="contained">
            Assignments Summary
          </Button>
        }
      />
      <ARMTeamRequestsTable
        filters={filters}
        openSummaryModal={isSummaryModalOpen}
        handleSummaryModal={handleSummaryModal}
      />
    </ThemeProvider>
  );
};

export default ARMTeamMyRequests;

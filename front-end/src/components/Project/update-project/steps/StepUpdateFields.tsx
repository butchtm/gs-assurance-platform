import React from "react";
import { Box } from "@mui/material";

import { ReferenceType } from "../../../../common/data-types/DataReferenceTypes";

import { DateFields } from "./DateFields";
import { FlagFields } from "./FlagFields";

export const StepUpdateFields: React.FC<{
  projectStatus?: ReferenceType;
  migratedProject?: boolean;
  displayNotRequestedStatusRequests?: boolean;
}> = ({ projectStatus, migratedProject, displayNotRequestedStatusRequests }) => {
  return (
    <Box p={4}>
      <DateFields projectStatus={projectStatus} />
      <FlagFields
        migratedProject={migratedProject}
        displayNotRequestedStatusRequests={displayNotRequestedStatusRequests}
      />
    </Box>
  );
};

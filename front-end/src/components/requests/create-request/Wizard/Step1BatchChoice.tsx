import { Box } from "@mui/material";
import React from "react";

import { ReferenceType } from "../../../../common/data-types/DataReferenceTypes";
import { CustomFormSelect } from "../../../Reusable/FormFields/CustomFormSelect";

interface Step1BatchChoiceProps {
  batchRequestTypes: ReferenceType[];
  update: boolean;
}

export const Step1BatchChoice: React.FC<Step1BatchChoiceProps> = ({ batchRequestTypes, update }) => {
  return (
    <Box>
      <CustomFormSelect
        name="createBatchRequest"
        label="Create Batch Request"
        options={[
          { value: "yes", label: "Yes" },
          { value: "no", label: "No" },
        ]}
        disabled={batchRequestTypes.length === 0 || update}
        required
      />
    </Box>
  );
};

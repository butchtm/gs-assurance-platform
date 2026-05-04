import { Box } from "@mui/material";

import { RequestForm } from "../../../Forms/Forms";
import { ReferenceType } from "../../../../common/data-types/DataReferenceTypes";

import { FormDetails } from "./FormDetails";
import { FormActions } from "./FormActions";

export const Form = ({
  form,
  requestId,
  requestStatus,
  isBatchRequest,
  gsid,
}: {
  form: RequestForm;
  requestId: number;
  requestStatus: ReferenceType;
  isBatchRequest?: boolean;
  gsid: string;
}) => {
  return (
    <Box
      sx={{
        marginBottom: "8px",
        padding: 1.5,
        borderRadius: "5px",
        backgroundColor: "rgb(235, 250, 250);",
      }}
    >
      <Box sx={{ display: "flex", justifyContent: "space-between" }}>
        <FormDetails form={form} />
        <FormActions
          form={form}
          requestId={requestId}
          requestStatus={requestStatus}
          isBatchRequest={isBatchRequest}
          gsid={gsid}
        />
      </Box>
    </Box>
  );
};

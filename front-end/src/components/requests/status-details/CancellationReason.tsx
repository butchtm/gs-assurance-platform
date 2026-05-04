import { Cancel } from "@mui/icons-material";
import { Grid } from "@mui/material";

import { RequestField } from "../reusable/RequestField";
import { GSRequest } from "../../../common/request/GSRequest";

export const CancellationReason = ({ request }: { request: GSRequest }) => {
  const reasonText = request.requestStatus.code == "CANCELLED" ? "Cancellation Reason" : "Omission Reason";
  return (
    <Grid container>
      <RequestField name={reasonText} value={request.cancellationReason?.description ?? ""} icon={<Cancel />} />
      {request.cancellationReason?.code == "OTHER" && !!request.otherCancellationReason && (
        <RequestField name={`Other ${reasonText}`} value={request.otherCancellationReason} icon={<Cancel />} />
      )}
    </Grid>
  );
};

import { useFormikContext } from "formik";
import { Grid, Typography } from "@mui/material";

import { CreateRequestForm } from "../Request";
import { useReferenceTypes } from "../../../../../common/data-types/DataReferenceTypes";

export const MonitoringPeriodsGenericRequest = () => {
  const { activeReferenceTypes } = useReferenceTypes();
  const { values } = useFormikContext<CreateRequestForm>();
  const GENERIC_REQUEST_ID = activeReferenceTypes.RequestType.find((x) => x.code === "GENERIC")?.id.toString();
  return values.requestType === GENERIC_REQUEST_ID ? (
    <Grid item xs={12}>
      <Typography variant="body2" sx={{ pb: "5px" }}>
        Monitoring periods are optional for Generic requests
      </Typography>
    </Grid>
  ) : (
    <></>
  );
};

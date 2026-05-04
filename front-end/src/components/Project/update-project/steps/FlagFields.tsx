import React from "react";
import { Grid, FormControlLabel, Switch } from "@mui/material";
import { useFormikContext } from "formik";

import { UpdateProjectPayload } from "../../../../common/project/Project";

export const FlagFields: React.FC<{
  migratedProject?: boolean;
  displayNotRequestedStatusRequests?: boolean;
}> = ({ migratedProject, displayNotRequestedStatusRequests }) => {
  const { values, setFieldValue } = useFormikContext<UpdateProjectPayload>();

  if (!migratedProject) {
    return <></>;
  }

  return (
    <Grid container spacing={4}>
      <Grid item xs={12} md={6}>
        <FormControlLabel
          control={
            <Switch
              checked={values.displayNotRequestedStatusRequests ?? false}
              defaultChecked={displayNotRequestedStatusRequests ?? false}
              onChange={(event) => setFieldValue("displayNotRequestedStatusRequests", event.target.checked)}
              aria-label="display-not-requested-status-requests-switch"
            />
          }
          label="Display not requested status requests"
          data-testid="display-not-requested-status-requests-switch"
        />
      </Grid>
    </Grid>
  );
};

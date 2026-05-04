import AddIcon from "@mui/icons-material/Add";
import { Button, FormControl, Grid, Typography } from "@mui/material";
import { useFormikContext } from "formik";
import React, { useEffect, useState } from "react";

import { CreditingPeriod } from "../../../../../common/project/Project";
import { CustomFormSelect } from "../../../../Reusable/FormFields/CustomFormSelect";
import { CreateRequestForm } from "../Request";

import { getMonitoringPeriodOptions, isCreditingPeriodFull } from "./MonitoringPeriods";
import { MonitoringPeriodsGenericRequest } from "./MonitoringPeriodsGenericRequest";
import { Step1NewMonitoringFields } from "./Step1NewMonitoringFields";

export const MonitoringPeriodsHandler: React.FC<{
  creditingPeriods: CreditingPeriod[];
}> = ({ creditingPeriods }) => {
  const { values, setFieldValue } = useFormikContext<CreateRequestForm>();
  const [addNewMonitoringPeriod, setAddNewMonitoringPeriod] = useState<boolean>(values.useNewMonitoringPeriod);
  const [canCreateMonitoringPeriod, setCanCreateMonitoringPeriod] = useState<boolean>(
    values.canCreateNewMonitoringPeriod,
  );
  const [creditingPeriod, setCreditingPeriod] = useState<CreditingPeriod>();

  useEffect(() => {
    const creditingPeriod = creditingPeriods.find((period) => period.periodId == values.creditingPeriodId);
    if (creditingPeriod) {
      setCreditingPeriod(creditingPeriod);
      if (isCreditingPeriodFull(creditingPeriod)) {
        setCanCreateMonitoringPeriod(false);
        setFieldValue("canCreateNewMonitoringPeriod", false);
      } else {
        setCanCreateMonitoringPeriod(true);
        setFieldValue("canCreateNewMonitoringPeriod", true);
      }
    }
  }, [creditingPeriods, values.creditingPeriodId]);

  const handleAddNewMonitoringPeriod = () => {
    setAddNewMonitoringPeriod(true);
    setFieldValue("useNewMonitoringPeriod", true);
    setFieldValue("monitoringPeriodId", null);
    setFieldValue("newMonitoringPeriodStartDate", values.newMonitoringPeriodStartDate);
    setFieldValue("newMonitoringPeriodEndDate", values.newMonitoringPeriodEndDate);
  };

  const handleUseExistingMonitoringPeriod = () => {
    setAddNewMonitoringPeriod(false);
    setFieldValue("useNewMonitoringPeriod", false);
    setFieldValue("monitoringPeriodId", values.monitoringPeriodId);
    setFieldValue("newMonitoringPeriodStartDate", null);
    setFieldValue("newMonitoringPeriodEndDate", null);
  };

  const monitoringPeriodOptions = getMonitoringPeriodOptions(creditingPeriods, values.creditingPeriodId);

  if (!creditingPeriod) {
    return <></>;
  }

  return (
    <>
      {addNewMonitoringPeriod ? (
        <Grid item xs={12}>
          <Step1NewMonitoringFields creditingPeriod={creditingPeriod} />
        </Grid>
      ) : (
        <Grid item xs={12}>
          <FormControl fullWidth>
            <CustomFormSelect
              name="monitoringPeriodId"
              label="Monitoring Period ID"
              options={monitoringPeriodOptions}
              disabled={monitoringPeriodOptions.length === 0}
              required
            />
          </FormControl>
        </Grid>
      )}
      {canCreateMonitoringPeriod ? (
        <Grid item xs={12}>
          {addNewMonitoringPeriod ? (
            <Button variant="outlined" size="small" onClick={handleUseExistingMonitoringPeriod}>
              Use Existing Monitoring Period
            </Button>
          ) : (
            <>
              {monitoringPeriodOptions.length === 0 && (
                <Typography variant="body2" sx={{ pb: "5px" }}>
                  Please create a monitoring period
                </Typography>
              )}
              <Button variant="outlined" size="small" onClick={handleAddNewMonitoringPeriod}>
                <AddIcon />
                Add New Monitoring Period
              </Button>
            </>
          )}
        </Grid>
      ) : (
        <Grid item xs={12}>
          <Typography variant="body2" color="error.main">
            Monitoring period cannot be created because the crediting period has full coverage (every date in the
            crediting period is covered by a monitoring period).
          </Typography>
        </Grid>
      )}
      <MonitoringPeriodsGenericRequest />
    </>
  );
};

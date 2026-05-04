import AddIcon from "@mui/icons-material/Add";
import { Button, FormControl, Grid, Typography } from "@mui/material";
import dayjs from "dayjs";
import { useFormikContext } from "formik";
import React, { useEffect, useState } from "react";

import { CreditingPeriod } from "../../../../../common/project/Project";
import { findFirstVpa, getFirstVpaMonitoringPeriodEndDate } from "../../../../../common/request/GSRequest";
import { CustomFormSelect } from "../../../../Reusable/FormFields/CustomFormSelect";
import { BatchedProjectRequest } from "../../../Requests";
import { CreateRequestForm } from "../Request";

import { getMonitoringPeriodOptions, isCreditingPeriodFull } from "./MonitoringPeriods";
import { MonitoringPeriodsGenericRequest } from "./MonitoringPeriodsGenericRequest";
import { StepBatchedNewMonitoringFields } from "./StepBatchedNewMonitoringFields";

export const MonitoringPeriodsBatchHandler: React.FC<{
  creditingPeriods: CreditingPeriod[];
  batchedProject: BatchedProjectRequest;
  batchedProjects: BatchedProjectRequest[];
  isFixedPeriod: boolean;
}> = ({ creditingPeriods, batchedProject, batchedProjects, isFixedPeriod }) => {
  const { values, setFieldValue } = useFormikContext<CreateRequestForm>();
  const [creditingPeriod, setCreditingPeriod] = useState<CreditingPeriod>();
  const [addNewMonitoringPeriod, setAddNewMonitoringPeriod] = useState<boolean>(
    values.batchStepProjectValues?.batch[batchedProject.id].useNewMonitoringPeriod ?? false,
  );
  const [canCreateMonitoringPeriod, setCanCreateMonitoringPeriod] = useState<boolean>(
    values.batchStepProjectValues?.batch[batchedProject.id].canCreateNewMonitoringPeriod ?? true,
  );

  const currentProjectSelector = `batchStepProjectValues.batch.[${batchedProject.id}]`;
  const firstVpa = findFirstVpa(batchedProjects);

  const isFirstVpa = () => {
    return firstVpa != undefined && firstVpa.id === batchedProject.id;
  };

  const updateCanCreateMonitoringPeriod = (state: boolean) => {
    setCanCreateMonitoringPeriod(state);
    setFieldValue(`${currentProjectSelector}.canCreateNewMonitoringPeriod`, state);
    if (!state) {
      handleUseExistingMonitoringPeriod();
    }
  };

  useEffect(() => {
    const creditingPeriod = creditingPeriods.find(
      (period) => period.periodId == values.batchStepProjectValues?.batch[batchedProject.id]?.creditingPeriodId,
    );
    if (creditingPeriod) {
      setCreditingPeriod(creditingPeriod);
      if (isCreditingPeriodFull(creditingPeriod) || isFixedPeriod) {
        updateCanCreateMonitoringPeriod(false);
        return;
      } else {
        updateCanCreateMonitoringPeriod(true);
      }
      if (firstVpa) {
        const firstVpaEndDate = getFirstVpaMonitoringPeriodEndDate(
          firstVpa,
          values.batchStepProjectValues?.batch[firstVpa.id],
        );
        if (!isFirstVpa()) {
          setFieldValue(`${currentProjectSelector}.newMonitoringPeriodEndDate`, dayjs.utc(firstVpaEndDate) ?? null);
        }
        if (!canCreateMonitoringPeriodInBatch(firstVpaEndDate)) {
          updateCanCreateMonitoringPeriod(false);
        } else {
          updateCanCreateMonitoringPeriod(true);
        }
      }
    }
  }, [isFixedPeriod]);

  const handleAddNewMonitoringPeriod = () => {
    setAddNewMonitoringPeriod(true);
    setFieldValue(`${currentProjectSelector}.useNewMonitoringPeriod`, true);
    setFieldValue(`${currentProjectSelector}.monitoringPeriodId`, null);
  };

  const handleUseExistingMonitoringPeriod = () => {
    setAddNewMonitoringPeriod(false);
    setFieldValue(`${currentProjectSelector}.useNewMonitoringPeriod`, false);
    setFieldValue(
      `${currentProjectSelector}.monitoringPeriodId`,
      values.batchStepProjectValues?.batch[batchedProject.id].monitoringPeriodId,
    );
    setFieldValue(`${currentProjectSelector}.newMonitoringPeriodStartDate`, null);
    setFieldValue(`${currentProjectSelector}.newMonitoringPeriodEndDate`, null);
  };

  const canCreateMonitoringPeriodInBatch = (firstVpaEndDate?: string) => {
    if (firstVpaEndDate && !isFirstVpa()) {
      return (
        getCreditingPeriodForNewEndDate(firstVpaEndDate) && !endDateWithinExistingMonitoringPeriod(firstVpaEndDate)
      );
    }
    return true;
  };

  const getCreditingPeriodForNewEndDate = (endDate: string) => {
    return creditingPeriods.find((period) => dateWithin(period.startDate, period.endDate, endDate));
  };

  const endDateWithinExistingMonitoringPeriod = (endDate: string) => {
    const monitoringPeriods = creditingPeriods.flatMap(({ periodId, monitoringPeriods }) =>
      monitoringPeriods?.map((period) => ({ creditingPeriodId: periodId, ...period })),
    );
    const matchedMonitoringPeriod = monitoringPeriods.find((period) => {
      if (!period) {
        return false;
      }
      return dateWithin(period.startDate, period.endDate, endDate);
    });
    return !!matchedMonitoringPeriod;
  };

  const dateWithin = (start: string, end: string, comparison: string) => {
    const startDate = dayjs.utc(start).startOf("day");
    const endDate = dayjs.utc(end).startOf("day");
    const comparisonDate = dayjs.utc(comparison).startOf("day");
    return (
      (startDate.isBefore(comparisonDate) || startDate.isSame(comparisonDate)) &&
      (endDate.isAfter(comparisonDate) || endDate.isSame(comparisonDate))
    );
  };

  const monitoringPeriodOptions = getMonitoringPeriodOptions(
    creditingPeriods,
    values.batchStepProjectValues?.batch[batchedProject.id]?.creditingPeriodId ?? null,
  );

  const showMonitoringPeriodCannotExistWarning = () => {
    if (!firstVpa) {
      return false;
    }
    const firstVpaEndDate = getFirstVpaMonitoringPeriodEndDate(
      firstVpa,
      values.batchStepProjectValues?.batch[firstVpa.id],
    );
    return !canCreateMonitoringPeriod && !canCreateMonitoringPeriodInBatch(firstVpaEndDate) && !isFixedPeriod;
  };

  if (!creditingPeriod) {
    return <></>;
  }

  return (
    <>
      {addNewMonitoringPeriod ? (
        <Grid item xs={12}>
          <StepBatchedNewMonitoringFields
            creditingPeriod={creditingPeriod}
            project={batchedProject}
            isFixedEndDate={!isFirstVpa()}
          />
        </Grid>
      ) : (
        <Grid item xs={12}>
          <FormControl fullWidth>
            <CustomFormSelect
              name={`${currentProjectSelector}.monitoringPeriodId`}
              label="Monitoring Period ID"
              options={monitoringPeriodOptions}
              disabled={monitoringPeriodOptions.length === 0 || isFixedPeriod || (!isFirstVpa() && !isFixedPeriod)}
            />
          </FormControl>
          {!isFirstVpa() && !isFixedPeriod && (
            <Grid item xs={12}>
              <Typography variant="body2" color="error.main">
                Monitoring period cannot be selected as none have a matching end date for {firstVpa?.gsid}.
              </Typography>
            </Grid>
          )}
        </Grid>
      )}
      {canCreateMonitoringPeriod && (
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
      )}
      {!canCreateMonitoringPeriod && isCreditingPeriodFull(creditingPeriod) && (
        <Grid item xs={12}>
          <Typography variant="body2" color="error.main">
            Monitoring period cannot be created because the crediting period has full coverage (every date in the
            crediting period is covered by a monitoring period).
          </Typography>
        </Grid>
      )}
      {showMonitoringPeriodCannotExistWarning() && (
        <Grid item xs={12}>
          <Typography variant="body2" color="error.main">
            Monitoring period cannot be created because no monitoring period can exist which matches the monitoring
            period end date of {firstVpa?.gsid}.
          </Typography>
        </Grid>
      )}
      <MonitoringPeriodsGenericRequest />
    </>
  );
};

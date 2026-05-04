import AddIcon from "@mui/icons-material/Add";
import { Button, FormControl, Grid } from "@mui/material";
import dayjs from "dayjs";
import { useFormikContext } from "formik";
import React, { useContext, useEffect, useState } from "react";

import { getReferenceValueCodeById, ReferenceTypeContext } from "../../../../common/data-types/DataReferenceTypes";
import { findNewCreditingPeriodStartDate, isPoa, Project, toDefaultEndDate } from "../../../../common/project/Project";
import {
  canRequestTypeCreateCreditingPeriod,
  canRequestTypeCreateMonitoringPeriod,
  canRequestTypeSetFeeSchedule,
  isEmissionReductionRequired,
} from "../../../../common/request/GSRequest";
import { CustomFormSelect } from "../../../Reusable/FormFields/CustomFormSelect";
import { EmissionsReductionInput } from "../../../Reusable/FormFields/EmissionsReductionInput";
import { requestsHelperText } from "../RequestHelpers";

import { CreateRequestForm } from "./Request";
import { Step1NewCreditingFields } from "./crediting-periods/Step1NewCreditingFields";
import { MonitoringPeriodsHandler } from "./monitoring-periods/MonitoringPeriodsHandler";
import { FeeSchedule } from "./FeeSchedule";

interface Step1FieldsProps {
  project: Project;
}

export const Step1Fields: React.FC<Step1FieldsProps> = ({ project }) => {
  const { values, setFieldValue } = useFormikContext<CreateRequestForm>();
  const referenceDataContext = useContext(ReferenceTypeContext);

  const currentRequestTypeCode = getReferenceValueCodeById(
    Number(values.requestType),
    referenceDataContext.allReferenceTypes.RequestType,
  );
  const isFeeScheduleRequestType = currentRequestTypeCode
    ? canRequestTypeSetFeeSchedule(currentRequestTypeCode)
    : false;
  const isEmissionRequired = currentRequestTypeCode ? isEmissionReductionRequired(currentRequestTypeCode) : false;

  const [addNewCreditingPeriod, setAddNewCreditingPeriod] = useState<boolean>(values.useNewCreditingPeriod);
  const [canCreateCreditingPeriod, setCanCreateCreditingPeriod] = useState<boolean>(values.canCreateNewCreditingPeriod);

  useEffect(() => {
    const isMonitoringPeriodRequestType =
      currentRequestTypeCode && canRequestTypeCreateMonitoringPeriod(currentRequestTypeCode);
    setFieldValue("isMonitoringPeriodEnabled", !isPoa(project.projectClassification) && isMonitoringPeriodRequestType);
    const canTypeCreateCreditingPeriod =
      currentRequestTypeCode && canRequestTypeCreateCreditingPeriod(currentRequestTypeCode);

    const hasPeriodWithFutureStartDate: boolean = project.creditingPeriods.some(
      ({ startDate }) => new Date(startDate) > new Date(),
    );

    if (canTypeCreateCreditingPeriod && !hasPeriodWithFutureStartDate) {
      setCanCreateCreditingPeriod(true);
      setFieldValue("canCreateNewCreditingPeriod", true);
    } else {
      setCanCreateCreditingPeriod(false);
      setFieldValue("canCreateNewCreditingPeriod", false);
    }
  }, [values.requestType, project]);

  const handleEmissionReductionChange = (value: number) => {
    setFieldValue("emissionReductionAmount", value);
  };

  const creditingPeriodOptions = (project.creditingPeriods || []).map((period) => ({
    value: period.periodId,
    label: `Period ${period.periodId} - ${dayjs.utc(period.startDate).format("DD/MM/YYYY")} to ${dayjs
      .utc(period.endDate)
      .format("DD/MM/YYYY")}`,
  }));

  const handleAddNewCreditingPeriod = () => {
    setAddNewCreditingPeriod(true);
    setFieldValue("creditingPeriodId", null);
    setFieldValue("useNewCreditingPeriod", true);
    const newStartDate = findNewCreditingPeriodStartDate(project.creditingPeriods);
    setFieldValue("newCreditingPeriodStartDate", newStartDate.toISOString());
    setFieldValue("newCreditingPeriodEndDate", toDefaultEndDate(newStartDate).toISOString());
    setFieldValue("methodologies", []);
  };

  const handleUseExistingCreditingPeriod = () => {
    setAddNewCreditingPeriod(false);
    setFieldValue("newCreditingPeriodStartDate", null);
    setFieldValue("newCreditingPeriodEndDate", null);
    setFieldValue("useNewCreditingPeriod", false);
    setFieldValue("methodologies", []);
  };

  return (
    <>
      <Grid item xs={12}>
        {addNewCreditingPeriod ? (
          <Step1NewCreditingFields creditingPeriods={project.creditingPeriods} />
        ) : (
          <FormControl fullWidth>
            <CustomFormSelect
              name="creditingPeriodId"
              label="Credit Period ID"
              options={creditingPeriodOptions}
              disabled={creditingPeriodOptions.length === 0}
              required
            />
          </FormControl>
        )}
      </Grid>
      {canCreateCreditingPeriod && (
        <Grid item xs={12}>
          {addNewCreditingPeriod ? (
            <Button
              variant="outlined"
              size="small"
              onClick={handleUseExistingCreditingPeriod}
              data-testid="use-existing-period-button"
            >
              Use Existing Crediting Period
            </Button>
          ) : (
            <Button
              variant="outlined"
              size="small"
              onClick={handleAddNewCreditingPeriod}
              data-testid="add-new-period-button"
            >
              <AddIcon />
              Add New Crediting Period
            </Button>
          )}
        </Grid>
      )}
      {values.creditingPeriodId && values.isMonitoringPeriodEnabled && (
        <MonitoringPeriodsHandler creditingPeriods={project.creditingPeriods} />
      )}
      {isFeeScheduleRequestType && values.creditingPeriodId && values.isMonitoringPeriodEnabled && (
        <Grid item xs={12}>
          <FeeSchedule batch={false} />
        </Grid>
      )}
      <Grid item xs={12}>
        <EmissionsReductionInput
          fieldValue={values.emissionReductionAmount}
          helpText={requestsHelperText.emissionsReduction()}
          onChange={handleEmissionReductionChange}
          required={isEmissionRequired}
        />
      </Grid>
    </>
  );
};

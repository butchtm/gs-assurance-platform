import AddIcon from "@mui/icons-material/Add";
import ClearIcon from "@mui/icons-material/Clear";
import { Box, Button, Grid, IconButton, Typography } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import dayjs, { Dayjs } from "dayjs";
import { FieldArray, useFormikContext } from "formik";

import { Options, useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import {
  CreateProjectForm,
  CreditingPeriod,
  isPoa,
  toDefaultEndDate,
  toMaxEndDate,
  toMaxStartDate,
  toMinEndDate,
  toMinStartDate,
  toNewStartDate,
} from "../../../../common/project/Project";
import CustomHelperText, { CustomTooltip } from "../../../Reusable/FormFields/CustomTooltipText";
import { helperTexts } from "../HelperText";

import CreateMethodologies from "./CreateMethodology";
import CreateMonitoringPeriod from "./CreateMonitoringPeriod";

interface CreateCreditingPeriodProps {
  methodologyOptions: Options[];
  isUpdate?: boolean;
}

export const CreateCreditingPeriod: React.FC<CreateCreditingPeriodProps> = ({
  methodologyOptions,
  isUpdate,
}: CreateCreditingPeriodProps) => {
  const { values, setFieldValue } = useFormikContext<CreateProjectForm>();
  const { activeReferenceTypes } = useReferenceTypes();
  const projectClassifications = activeReferenceTypes.ProjectClassification;
  const POA = projectClassifications.find((classification) => isPoa(classification));

  const handleStartDateChange = (index: number, date: Dayjs | null) => {
    const previousPeriodEndDate = index > 0 ? dayjs.utc(values.creditingPeriods[index - 1].endDate) : null;
    const nextPeriodStartDate =
      index < values.creditingPeriods.length - 1 ? dayjs.utc(values.creditingPeriods[index + 1].startDate) : null;

    if (date && previousPeriodEndDate && date.isBefore(previousPeriodEndDate)) {
      setFieldValue(`creditingPeriods[${index}].startDate`, previousPeriodEndDate.add(1, "day"));
    } else if (date && nextPeriodStartDate && date.isAfter(nextPeriodStartDate)) {
      setFieldValue(`creditingPeriods[${index}].startDate`, nextPeriodStartDate.subtract(1, "day"));
    } else {
      setFieldValue(`creditingPeriods[${index}].startDate`, date);
    }

    const currentEndDate = dayjs.utc(values.creditingPeriods[index].endDate);
    if (currentEndDate && date && currentEndDate.isBefore(date)) {
      setFieldValue(`creditingPeriods[${index}].endDate`, date.add(1, "day"));
    }
  };

  const handleEndDateChange = (index: number, date: Dayjs | null) => {
    const startDate = dayjs.utc(values.creditingPeriods[index].startDate);
    const nextPeriodStartDate =
      index < values.creditingPeriods.length - 1 ? dayjs.utc(values.creditingPeriods[index + 1].startDate) : null;

    if (date?.isBefore(startDate)) {
      setFieldValue(`creditingPeriods[${index}].endDate`, startDate.add(1, "day"));
    } else if (date && nextPeriodStartDate && date.isAfter(nextPeriodStartDate)) {
      setFieldValue(`creditingPeriods[${index}].endDate`, nextPeriodStartDate.subtract(1, "day"));
    } else {
      setFieldValue(`creditingPeriods[${index}].endDate`, date);
    }
  };

  const handleAddPeriod = (push: (obj: CreditingPeriod) => void) => {
    const lastPeriod = values.creditingPeriods[values.creditingPeriods.length - 1];
    const newStartDate = toNewStartDate(values.creditingPeriods);
    push({
      periodId: lastPeriod.periodId + 1,
      startDate: newStartDate.toISOString(),
      endDate: toDefaultEndDate(newStartDate).toISOString(),
      methodologies: [],
    });
  };

  const handleRemovePeriod = (remove: (index: number) => void, index: number) => {
    remove(index);
    const updatedPeriods = values.creditingPeriods
      .filter((_, i) => i !== index)
      .map((period, idx) => ({
        ...period,
        periodId: idx + 1,
      }));
    setFieldValue("creditingPeriods", updatedPeriods);
  };

  return (
    <>
      <Grid item xs={12} sx={{ mb: 2 }}>
        <Box display="flex">
          <Typography variant="h5">Crediting Periods</Typography>
          <Box ml={2}>
            <CustomHelperText
              text={
                <>
                  {helperTexts.creditingPeriods()}
                  {helperTexts.projectMethodologies()}
                </>
              }
              type={CustomTooltip.HELP}
            />
          </Box>
        </Box>
      </Grid>
      <FieldArray
        name="creditingPeriods"
        render={({ remove, push }) => (
          <>
            {values.creditingPeriods &&
              values.creditingPeriods.length > 0 &&
              values.creditingPeriods.map((period, index) => (
                <Grid container alignItems="flex-start" spacing={2} key={period.periodId} sx={{ mb: 2 }}>
                  <Grid item>
                    <Typography variant="body1" align="center" sx={{ fontWeight: "bold" }}>
                      {period.periodId}
                    </Typography>
                  </Grid>
                  <Grid container item xs spacing={2} alignItems="center">
                    <Grid item xs>
                      <Box
                        sx={{
                          border: "1px solid #d3d3d3",
                          borderRadius: "5px",
                          p: 2,
                        }}
                      >
                        <Grid container spacing={2} alignItems="center">
                          <Grid item xs={5}>
                            <DatePicker
                              label="Crediting Period Start Date"
                              value={dayjs.utc(period.startDate)}
                              onChange={(date) => handleStartDateChange(index, date)}
                              format="DD/MM/YYYY"
                              minDate={toMinStartDate(values.creditingPeriods, index)}
                              maxDate={toMaxStartDate(values.creditingPeriods, index)}
                              timezone="UTC"
                            />
                          </Grid>
                          <Grid item xs={5}>
                            <DatePicker
                              label="Crediting Period End Date"
                              value={dayjs.utc(period.endDate)}
                              onChange={(date) => handleEndDateChange(index, date)}
                              format="DD/MM/YYYY"
                              minDate={toMinEndDate(period.startDate)}
                              maxDate={toMaxEndDate(values.creditingPeriods, index)}
                              timezone="UTC"
                            />
                          </Grid>
                          {(isUpdate || index === 0) && (
                            <CreateMethodologies
                              currentMethodologies={period.methodologies}
                              creditingPeriodIndex={index}
                              methodologyOptions={methodologyOptions}
                            />
                          )}
                          {isUpdate && values.projectClassificationId !== POA?.id && (
                            <CreateMonitoringPeriod
                              currentMonitoringPeriods={period.monitoringPeriods}
                              creditingPeriodIndex={index}
                            />
                          )}
                        </Grid>
                      </Box>
                    </Grid>
                    <Grid item>
                      {values.creditingPeriods.length > 1 && index === values.creditingPeriods.length - 1 && (
                        <Grid item xs={1}>
                          <IconButton
                            aria-label="Remove Crediting Period"
                            onClick={() => handleRemovePeriod(remove, index)}
                          >
                            <ClearIcon color="error" />
                          </IconButton>
                        </Grid>
                      )}
                    </Grid>
                  </Grid>
                </Grid>
              ))}
            <Grid item xs={12}>
              <Button disabled={values.creditingPeriods?.length > 9} onClick={() => handleAddPeriod(push)}>
                <AddIcon />
                Add Crediting Period
              </Button>
            </Grid>
          </>
        )}
      />
    </>
  );
};

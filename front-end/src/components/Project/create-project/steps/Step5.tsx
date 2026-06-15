import { Box, Grid, Typography } from "@mui/material";
import { Field, useField, useFormikContext } from "formik";
import { TextField } from "formik-mui";

import { useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { ProbablePii } from "../../../../common/piiAnalysis/PiiAnalysis";
import { CreateProjectForm } from "../../../../common/project/Project";
import { CustomFormSelect } from "../../../Reusable/FormFields/CustomFormSelect";
import { CustomMultiAutoComplete } from "../../../Reusable/FormFields/CustomMultiAutoComplete";
import { EmissionsReductionInput } from "../../../Reusable/FormFields/EmissionsReductionInput";
import { PiiWarningPopover } from "../../../Reusable/FormFields/PiiWarningPopover";
import { predefinedGSV } from "../../Project";
import { getWarning } from "../CreateProject";
import { helperTexts } from "../HelperText";

interface Step5Props {
  probablePii?: ProbablePii[];
}

export const Step5 = ({ probablePii = [] }: Step5Props) => {
  const { values, setFieldValue } = useFormikContext<CreateProjectForm>();
  const referenceDataContext = useReferenceTypes();

  const sustainableDevelopmentGoals = referenceDataContext.getCustomReferenceOptions(
    "SustainableDevelopmentGoal",
    values.sustainableDevelopmentGoalIds,
  );
  const feeSchedules = referenceDataContext.getCustomReferenceOptions("FeeSchedule", values.feeScheduleId);

  const handleEmissionReductionChange = (value: number) => {
    setFieldValue("estimatedAnnualUnits", value);
  };

  const validateFeeSchedule = (v: number | null | undefined) => (v == null ? "Fee Schedule is required" : undefined);

  const [, meta] = useField<number | null>("feeScheduleId");
  const feeScheduleError = meta.touched && meta.error ? meta.error : undefined;

  return (
    <Box p={4}>
      <Field name="feeScheduleId" validate={validateFeeSchedule} component={() => null} />
      <Grid container spacing={4}>
        <Grid item xs={12} md={9}>
          <EmissionsReductionInput fieldValue={values.estimatedAnnualUnits} onChange={handleEmissionReductionChange} />
        </Grid>

        <Grid item xs={12} md={3}>
          <Field
            fullWidth
            name="goldStandardVersion"
            component={TextField}
            label="Gold Standard Version"
            InputProps={{ readOnly: true }}
            inputProps={{ "data-testid": "gold-standard-version-input" }}
            value={predefinedGSV}
          />
        </Grid>

        <Grid item xs={12} md={8}>
          {sustainableDevelopmentGoals.length > 0 ? (
            <CustomMultiAutoComplete
              name="sustainableDevelopmentGoalIds"
              label="Sustainable Development Goals"
              options={sustainableDevelopmentGoals}
              helperText={helperTexts.sustainableDevelopmentGoals()}
              warningText={getWarning(
                values.sustainableDevelopmentGoalIds,
                values.masterValues?.sustainableDevelopmentGoalIds,
              )}
            />
          ) : (
            <Typography>Loading sustainableDevelopmentGoals options...</Typography>
          )}
        </Grid>

        <Grid item xs={12} md={4}>
          {feeSchedules.length > 0 ? (
            <CustomFormSelect
              name="feeScheduleId"
              label="Fee Schedule"
              options={feeSchedules}
              required
              helperText={feeScheduleError || helperTexts.feeSchedule()}
            />
          ) : (
            <Typography>Loading feeSchedules options...</Typography>
          )}
        </Grid>

        <Grid item xs={12} md={12}>
          <Box display="flex" alignItems="flex-start">
            <Field
              name="notes"
              fullWidth
              component={TextField}
              label="Notes"
              placeholder="Add some notes.."
              multiline
              rows={5}
              inputProps={{ "data-testid": "project-notes-input" }}
            />
            <Box mt={1}>
              <PiiWarningPopover probablePii={probablePii} field="notes" />
            </Box>
          </Box>
        </Grid>
      </Grid>
    </Box>
  );
};

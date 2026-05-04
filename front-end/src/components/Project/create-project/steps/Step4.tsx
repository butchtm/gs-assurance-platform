import { Box, Grid, Typography } from "@mui/material";
import dayjs from "dayjs";
import { useFormikContext } from "formik";

import { useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { CreateProjectForm } from "../../../../common/project/Project";
import { CustomAutoComplete } from "../../../Reusable/FormFields/CustomAutoComplete";
import { CustomDatePicker } from "../../../Reusable/FormFields/CustomDatePicker";
import { CustomFormSelect } from "../../../Reusable/FormFields/CustomFormSelect";
import { helperTexts } from "../HelperText";

import { OtherSchemeProjectLink } from "./OtherSchemeProjectLink";

export const Step4 = () => {
  const { values, setFieldValue } = useFormikContext<CreateProjectForm>();
  const referenceDataContext = useReferenceTypes();
  const transitionPathways = referenceDataContext.getCustomReferenceOptions(
    "TransitionPathway",
    values.transitionPathwayId,
  );
  const transitionApprovalProcedures = referenceDataContext.getCustomReferenceOptions(
    "TransitionApprovalProcedure",
    values.transitionApprovalProcedureId,
  );
  const transitionFromOtherStandards = referenceDataContext.getCustomReferenceOptions(
    "TransitionFromOtherScheme",
    values.transitionFromOtherSchemeId,
  );

  return (
    <Box p={4}>
      <Grid container spacing={4}>
        <Grid item xs={12} md={6}>
          <CustomFormSelect
            name="transitioningProject"
            label="Transitioning Project"
            options={[
              { value: "yes", label: "Yes" },
              { value: "no", label: "No" },
            ]}
            helperText={helperTexts.transitioningProject()}
            required
          />
        </Grid>
        {values.transitioningProject === "yes" && (
          <>
            <Grid item xs={12} md={6}>
              {transitionFromOtherStandards.length > 0 ? (
                <CustomAutoComplete
                  name="transitionFromOtherSchemeId"
                  label="Other Standard"
                  options={transitionFromOtherStandards}
                  onChange={(otherStandard) => setFieldValue("transitionFromOtherSchemeId", otherStandard)}
                  helperText={helperTexts.transitioningOtherStandard()}
                  required
                />
              ) : (
                <Typography>Loading transitionFromOtherStandards options...</Typography>
              )}
            </Grid>
            <Grid item xs={12} md={6}>
              <CustomDatePicker
                name="creditingPeriodStartDateOtherSchemes"
                label="Other Schemes Crediting Period Start Date"
                required
                minDate={dayjs.utc(new Date(2000, 0, 1))}
                maxDate={
                  values.creditingPeriodEndDateOtherSchemes
                    ? dayjs(values.creditingPeriodEndDateOtherSchemes)
                    : undefined
                }
                helperText={helperTexts.transitioningCreditingPeriodStartEnd()}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <CustomDatePicker
                name="creditingPeriodEndDateOtherSchemes"
                label="Other Schemes Crediting Period End Date"
                required
                defaultValue={
                  dayjs.utc(values.creditingPeriodEndDateOtherSchemes) ||
                  dayjs.utc().add(+7, "year").subtract(-1, "day")
                }
                minDate={
                  values.creditingPeriodStartDateOtherSchemes
                    ? dayjs(values.creditingPeriodStartDateOtherSchemes)
                    : dayjs.utc(new Date(2000, 0, 1))
                }
                helperText={helperTexts.transitioningCreditingPeriodStartEnd()}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              {transitionPathways.length > 0 ? (
                <CustomAutoComplete
                  name="transitionPathwayId"
                  label="Transition Pathway"
                  options={transitionPathways}
                  onChange={(transitionPathway) => setFieldValue("transitionPathwayId", transitionPathway)}
                  helperText={helperTexts.transitioningPathway()}
                  required
                />
              ) : (
                <Typography>Loading transitionPathways options...</Typography>
              )}
            </Grid>
            <Grid item xs={12} md={6}>
              {transitionApprovalProcedures.length > 0 ? (
                <CustomFormSelect
                  name="transitionApprovalProcedureId"
                  label="Transitioning Approval Procedure"
                  options={transitionApprovalProcedures}
                  helperText={helperTexts.transitioningApprovalProcedure()}
                  required
                />
              ) : (
                <Typography>Loading transitionApprovalProcedures options...</Typography>
              )}
            </Grid>
            <Grid item xs={12} md={12}>
              <OtherSchemeProjectLink />
            </Grid>
          </>
        )}
      </Grid>
    </Box>
  );
};

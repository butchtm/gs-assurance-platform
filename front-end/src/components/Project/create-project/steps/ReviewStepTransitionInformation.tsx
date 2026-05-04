import { Box, Button, Grid, Typography } from "@mui/material";
import dayjs from "dayjs";
import { useFormikContext } from "formik";

import { getReferenceValueDescriptionById, useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { CreateProjectForm } from "../../../../common/project/Project";
import { useFormikStepContext } from "../../../Reusable/Formik/FormikStepper";

import { ReviewValueLabelPair } from "./ReviewLabelValuePair";

export const ReviewStepTransitionInformation = () => {
  const { activeReferenceTypes } = useReferenceTypes();
  const { values } = useFormikContext<CreateProjectForm>();
  const { setStep } = useFormikStepContext();

  return (
    <Box border={1} borderColor="grey.500" borderRadius={2} padding={2} marginBottom={2}>
      <Grid container spacing={4}>
        <Grid item xs={12}>
          <Typography variant="body1" align="left">
            <strong>Transitioning Project Information</strong>
            <Button className="edit-button" onClick={() => setStep(3)}>
              Edit
            </Button>
          </Typography>
        </Grid>
        <Grid item xs={12} md={6}>
          <ReviewValueLabelPair
            label="Other Standard:"
            value={getReferenceValueDescriptionById(
              values.transitionFromOtherSchemeId,
              activeReferenceTypes.TransitionFromOtherScheme,
            )}
          />
          <ReviewValueLabelPair
            label="Crediting Period Start Date:"
            value={dayjs.utc(values.creditingPeriodStartDateOtherSchemes).format("DD/MM/YYYY")}
          />
          <ReviewValueLabelPair
            label="Crediting Period End Date:"
            value={dayjs.utc(values.creditingPeriodEndDateOtherSchemes).format("DD/MM/YYYY")}
          />
        </Grid>
        <Grid item xs={12} md={6}>
          <ReviewValueLabelPair
            label="Transition Pathway:"
            value={getReferenceValueDescriptionById(values.transitionPathwayId, activeReferenceTypes.TransitionPathway)}
          />
          <ReviewValueLabelPair
            label="Transition Approval Procedure:"
            value={getReferenceValueDescriptionById(
              values.transitionApprovalProcedureId,
              activeReferenceTypes.TransitionApprovalProcedure,
            )}
          />
          <Typography variant="body2" gutterBottom align="left">
            <strong>Link to Other Scheme:</strong> {values.projectLinkInOtherScheme}
          </Typography>
        </Grid>
      </Grid>
    </Box>
  );
};

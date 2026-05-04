import { Grid, Paper, Typography } from "@mui/material";
import dayjs from "dayjs";
import { useFormikContext } from "formik";
import { useContext, useEffect } from "react";

import { getReferenceValueDescriptionById, useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { analyseText, TextAnalysis } from "../../../../common/piiAnalysis/PiiAnalysis";
import { CreateProjectForm, ReferenceType } from "../../../../common/project/Project";
import { AuthContext } from "../../../Auth/Login/AuthProvider";
import { useFormikStepContext } from "../../../Reusable/Formik/FormikStepper";
import { predefinedGSV } from "../../Project";

import { ReviewValueLabelPair } from "./ReviewLabelValuePair";
import { ReviewStepCreditingPeriods } from "./ReviewStepCreditingPeriods";
import { ReviewStepTransitionInformation } from "./ReviewStepTransitionInformation";

export const ReviewStep = () => {
  const { activeReferenceTypes } = useReferenceTypes();
  const locations = activeReferenceTypes.ProjectLocation;
  const { values, setFieldValue } = useFormikContext<CreateProjectForm>();
  const { setStep } = useFormikStepContext();
  const authContext = useContext(AuthContext);

  const countryLabels =
    values.locationIds
      ?.map((id) => locations.find((location) => location.id === id)?.description)
      .filter((description) => description !== undefined) ?? [];

  const countryLabelString = countryLabels.join(", ");

  const formatMultiValues = (multiValues: number[] | undefined, referenceType: ReferenceType[]) => {
    const formattedValues = multiValues?.map((value) => getReferenceValueDescriptionById(value, referenceType)) ?? [];
    return formattedValues.join(", ");
  };

  const canEditDeveloperAccount = () => {
    return authContext.isAdminUser() || authContext.isArmUser();
  };

  const analyseFreeTextFields = async (toAnalyse: TextAnalysis) => {
    if (toAnalyse != null) {
      setFieldValue("textAnalysisResults", await analyseText(toAnalyse));
    }
  };

  useEffect(() => {
    const textToAnalyse = [];
    if (values.name != "") {
      textToAnalyse.push({ field: "title", text: values.name });
    }
    if (values.description != "") {
      textToAnalyse.push({ field: "description", text: values.description });
    }
    if (values.notes != "") {
      textToAnalyse.push({ field: "notes", text: values.notes });
    }
    const toAnalyse: TextAnalysis = {
      fieldsToAnalyse: textToAnalyse,
    };
    analyseFreeTextFields(toAnalyse);
  }, []);

  return (
    <Paper elevation={3} style={{ padding: "20px", margin: "20px" }}>
      <Typography variant="h5" gutterBottom>
        Review Your Information
      </Typography>

      <Grid container spacing={4} style={{ marginTop: "10px" }}>
        <Grid item xs={12} md={6}>
          <ReviewValueLabelPair
            label="Project Developer"
            value={values?.accountName}
            onEdit={canEditDeveloperAccount() ? () => setStep(0) : undefined}
          />
          <ReviewValueLabelPair
            label="Project Title"
            value={values.name}
            onEdit={() => setStep(0)}
            probablePii={values.textAnalysisResults?.probablePii}
          />
          <ReviewValueLabelPair
            label="Project Scale"
            value={getReferenceValueDescriptionById(values.sizeId, activeReferenceTypes.ProjectSize)}
            onEdit={() => setStep(1)}
          />
          <ReviewValueLabelPair
            label="Certified Products"
            value={formatMultiValues(values.projectAssetIds, activeReferenceTypes.ProjectAsset)}
            onEdit={() => setStep(1)}
          />
          <ReviewValueLabelPair
            label="Transitioning Project"
            value={values.transitioningProject}
            onEdit={() => setStep(3)}
          />
        </Grid>

        <Grid item xs={12} md={6}>
          <ReviewValueLabelPair
            label="Project Start Date"
            value={dayjs.utc(values.startDate).format("DD/MM/YYYY")}
            onEdit={() => setStep(1)}
          />
          <ReviewValueLabelPair
            label="Project Classification"
            value={getReferenceValueDescriptionById(
              values.projectClassificationId,
              activeReferenceTypes.ProjectClassification,
            )}
            onEdit={() => setStep(0)}
          />
          <ReviewValueLabelPair label="Project Country" value={countryLabelString} onEdit={() => setStep(1)} />
          <ReviewValueLabelPair
            label="Registration Type"
            value={getReferenceValueDescriptionById(values.registrationTypeId, activeReferenceTypes.RegistrationType)}
            onEdit={() => setStep(1)}
          />
          <ReviewValueLabelPair
            label="Carbon Stream"
            value={getReferenceValueDescriptionById(values.carbonStreamId, activeReferenceTypes.CarbonStream)}
            onEdit={() => setStep(1)}
          />
        </Grid>

        <Grid item xs={12}>
          {values.transitioningProject === "yes" && <ReviewStepTransitionInformation />}
          <ReviewValueLabelPair
            label="Project Description"
            value={values.description}
            onEdit={() => setStep(1)}
            probablePii={values.textAnalysisResults?.probablePii}
          />
          <ReviewValueLabelPair
            label="Project Types"
            value={formatMultiValues(values.typeIds, activeReferenceTypes.ProjectType)}
            onEdit={() => setStep(1)}
          />
        </Grid>

        <Grid item xs={12} md={6}>
          <ReviewValueLabelPair
            label="Estimated Annual Emission Reduction"
            value={values.estimatedAnnualUnits?.toLocaleString()}
            onEdit={() => setStep(4)}
          />
          <ReviewValueLabelPair label="Gold Standard Version" value={predefinedGSV} />
          <ReviewValueLabelPair
            label="Fee Schedule"
            value={getReferenceValueDescriptionById(values.feeScheduleId, activeReferenceTypes.FeeSchedule)}
            onEdit={() => setStep(4)}
          />
          <ReviewValueLabelPair
            label="Notes"
            value={values.notes}
            onEdit={() => setStep(4)}
            probablePii={values.textAnalysisResults?.probablePii}
          />
        </Grid>

        <Grid item xs={12} md={6}>
          <ReviewValueLabelPair
            label="Project SDG"
            value={formatMultiValues(
              values.sustainableDevelopmentGoalIds,
              activeReferenceTypes.SustainableDevelopmentGoal,
            )}
            onEdit={() => setStep(4)}
          />
          <ReviewStepCreditingPeriods />
        </Grid>
      </Grid>
    </Paper>
  );
};

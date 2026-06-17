import { Box, Card, Container, Typography } from "@mui/material";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import dayjs, { Dayjs } from "dayjs";
import { useContext } from "react";
import { Helmet } from "react-helmet-async";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { CreateProjectForm, CreateProjectPayload, TransitionProjectDetails } from "../../../common/project/Project";
import { AuthContext, AuthContextType } from "../../Auth/Login/AuthProvider";
import { FormikStep } from "../../Reusable/Formik/FormikStep";
import { FormikStepper } from "../../Reusable/Formik/FormikStepper";
import { createProject, getInitialEmptyValues } from "../Project";
import {
  getValidationSchemaStep1,
  validationSchemaStep2,
  validationSchemaStep3,
  validationSchemaStep4,
} from "../ProjectFormValidationSchema";

import { ReviewStep } from "./steps/ReviewStep";
import { Step1 } from "./steps/Step1";
import { Step2 } from "./steps/Step2";
import { Step3 } from "./steps/Step3";
import { Step4 } from "./steps/Step4";
import { Step5 } from "./steps/Step5";

export const warningText = "This value differs from that of the PoA. Ensure this is correct before proceeding.";

export const buildCreateProjectPayload = (
  formValues: CreateProjectForm,
  authContext: Pick<AuthContextType, "isAdminUser" | "isArmUser" | "user">,
): CreateProjectPayload => {
  const transitionProjectDetails: TransitionProjectDetails = {
    projectLinkInOtherScheme: formValues.projectLinkInOtherScheme,
    creditingPeriodStartDateOtherSchemes: formValues.creditingPeriodStartDateOtherSchemes,
    creditingPeriodEndDateOtherSchemes: formValues.creditingPeriodEndDateOtherSchemes,
    transitionApprovalProcedureId: formValues.transitionApprovalProcedureId,
    transitionFromOtherSchemeId: formValues.transitionFromOtherSchemeId,
    transitionPathwayId: formValues.transitionPathwayId,
  };

  const accountId =
    (authContext.isAdminUser() || authContext.isArmUser()) && formValues.accountId
      ? formValues.accountId
      : authContext.user?.account?.id;

  return {
    ...formValues,
    transitionProject: formValues.transitioningProject == "yes" ? transitionProjectDetails : null,
    accountId: Number(accountId),
    currentStandardId: 2, //Default always to Gold Standard Version
  };
};

const currentValueEqualsMasterValue = (a?: number[], b?: number[]) => {
  if (!a && !b) return true;
  if (!a || !b) return false;
  if (a.length !== b.length) return false;
  return a.every((val, idx) => val === b[idx]);
};

export const getWarning = (currentValue: unknown, masterValue: unknown) => {
  const hasCurrentValue =
    currentValue !== null &&
    currentValue !== undefined &&
    currentValue !== "" &&
    !(Array.isArray(currentValue) && currentValue.length === 0);

  const hasMasterValue =
    masterValue !== null &&
    masterValue !== undefined &&
    masterValue !== "" &&
    !(Array.isArray(masterValue) && masterValue.length === 0);

  if (!hasCurrentValue || !hasMasterValue) return undefined;

  if (Array.isArray(currentValue) && Array.isArray(masterValue)) {
    return !currentValueEqualsMasterValue(currentValue, masterValue) ? warningText : undefined;
  }

  return currentValue !== masterValue ? warningText : undefined;
};

export const getWarningForDate = (currentValue: string | Date | Dayjs, masterValue: string | Date | Dayjs) => {
  if (!currentValue || currentValue === "" || !masterValue || masterValue === "") return undefined;

  const currentDate = dayjs.utc(currentValue).startOf("second").toISOString();
  const masterDate = dayjs.utc(masterValue).startOf("second").toISOString();

  return currentDate !== masterDate ? warningText : undefined;
};

export const getWarningForProjectTypes = (currentTypeIds?: number[], masterTypeIds?: number[]) => {
  if (!currentTypeIds?.length || !masterTypeIds?.length) {
    return undefined;
  }
  if (currentTypeIds.some((typeId) => !masterTypeIds.includes(typeId))) {
    return warningText;
  }
  return undefined;
};

export const CreateProjectWizard = () => {
  const navigate = useNavigate();
  const { allReferenceTypes, activeReferenceTypes } = useReferenceTypes();
  const authContext = useContext(AuthContext);

  const handleSubmit = async (formValues: CreateProjectForm): Promise<boolean> => {
    const projectClassifications = activeReferenceTypes.ProjectClassification;
    const REGULAR_VPA = projectClassifications.find((classification) => classification.code === "REGULAR_VPA");
    const REAL_CASE_VPA = projectClassifications.find((classification) => classification.code === "REAL_CASE_VPA");

    if (!formValues.projectClassificationId) {
      toast.error("You must enter a Project Classification");
      return false;
    }

    if (formValues.projectClassificationId === REAL_CASE_VPA?.id) {
      if (!formValues.masterProjectId) {
        toast.error("Real Case VPA: You must enter a POA for this project");
        return false;
      }
    }

    if (formValues.projectClassificationId === REGULAR_VPA?.id) {
      if (!formValues.masterProjectId || !formValues.parentProjectId) {
        toast.error("Regular VPA: You must enter a POA and Real Case VPA for this project");
        return false;
      }
    }

    const payload = buildCreateProjectPayload(formValues, authContext);

    try {
      const project = await createProject(payload);
      if (project) {
        window.scrollTo(0, 0);
        return true;
      }
      return false;
    } catch (_error) {
      return false;
    }
  };

  const getSuccessRoute = () => {
    if (authContext.isProjectDeveloperUser()) {
      return "/project-trees";
    }
    return "/projects";
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <Helmet>
        <title>New Project</title>
      </Helmet>
      <Container maxWidth="md">
        <Card className="wizard-card">
          <Box>
            <Typography variant="h4">New Project</Typography>
            <br></br>
          </Box>
          <FormikStepper
            initialValues={getInitialEmptyValues()}
            onSubmit={async (values) => {
              const submitResult = await handleSubmit(values);
              if (submitResult) {
                toast.success("Project Created Successfully");
                navigate(getSuccessRoute());
              } else {
                toast.error("Error creating Project");
              }
            }}
          >
            <FormikStep label="Start" validationSchema={getValidationSchemaStep1(allReferenceTypes, authContext)}>
              <Step1 />
            </FormikStep>
            <FormikStep label="Details" validationSchema={validationSchemaStep2}>
              <Step2 />
            </FormikStep>
            <FormikStep label="Methodologies" validationSchema={validationSchemaStep3}>
              <Step3 />
            </FormikStep>
            <FormikStep label="Transitioning" validationSchema={validationSchemaStep4}>
              <Step4 />
            </FormikStep>
            <FormikStep label="Goals">
              <Step5 />
            </FormikStep>
            <FormikStep label="Review">
              <ReviewStep />
            </FormikStep>
          </FormikStepper>
        </Card>
      </Container>
    </LocalizationProvider>
  );
};

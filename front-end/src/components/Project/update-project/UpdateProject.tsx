import { ThemeProvider } from "@emotion/react";
import { Box, Button, Card, Container, Typography } from "@mui/material";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { Form, Formik } from "formik";
import React, { useContext, useEffect, useState } from "react";
import { Helmet } from "react-helmet-async";
import { useLocation, useNavigate, type Location } from "react-router-dom";
import { toast } from "react-toastify";
import * as Yup from "yup";

import { ReferenceType, useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import FullScreenMessage from "../../../common/full-screen-message/FullScreenMessage";
import { CreateProjectForm, Project } from "../../../common/project/Project";
import { theme } from "../../../theme";
import { AuthContext } from "../../Auth/Login/AuthProvider";
import { Step1 } from "../create-project/steps/Step1";
import { Step2 } from "../create-project/steps/Step2";
import { Step3 } from "../create-project/steps/Step3";
import { Step4 } from "../create-project/steps/Step4";
import { Step5 } from "../create-project/steps/Step5";
import { getInitialEmptyValues, getProject, updateProject } from "../Project";
import {
  getValidationSchemaStep1,
  validationSchemaStep2,
  validationSchemaStep3,
  validationSchemaStep4,
} from "../ProjectFormValidationSchema";

import { StepUpdateFields } from "./steps/StepUpdateFields";
import {
  buildUpdateProjectPayload,
  getUpdateProjectFieldCapabilities,
  hasAnyUpdateCapability,
} from "./UpdateProjectEditability";
import type { UpdateProjectFieldCapabilities } from "./UpdateProjectEditability";
import { mapProjectDataToFormValues } from "./UpdateProjectHelper";
import { usePiiAnalysis } from "./usePiiAnalysis";

const FormContent: React.FC<{
  values: CreateProjectForm;
  setFieldValue: (field: string, value: unknown) => void;
  project: Project | undefined;
  projectStatus: ReferenceType | undefined;
  capabilities: UpdateProjectFieldCapabilities;
}> = ({ values, project, projectStatus, setFieldValue, capabilities }) => {
  const piiAnalysis = usePiiAnalysis(
    {
      name: values.name,
      description: values.description,
      notes: values.notes,
    },
    project,
  );

  useEffect(() => {
    if (piiAnalysis.probablePii) {
      setFieldValue("textAnalysisResults", {
        probablePii: piiAnalysis.probablePii,
      });
    }
  }, [piiAnalysis.probablePii, setFieldValue]);

  return (
    <Form autoComplete="off">
      <Step1 projectId={project?.id} isUpdate={true} probablePii={piiAnalysis.probablePii} {...capabilities} />
      <Step2 probablePii={piiAnalysis.probablePii} />
      <Step3 isUpdate={true} />
      <Step4 />
      <Step5 probablePii={piiAnalysis.probablePii} />
      <StepUpdateFields
        projectStatus={projectStatus}
        migratedProject={project?.migrated ?? false}
        displayNotRequestedStatusRequests={values.displayNotRequestedStatusRequests ?? false}
      />
      <Box className="wizard-buttons">
        <Button type="submit">Update</Button>
      </Box>
    </Form>
  );
};

const UpdateProject: React.FC = () => {
  const navigate = useNavigate();

  const location: Location<{
    projectId: number;
  }> = useLocation();
  const authContext = useContext(AuthContext);
  const { allReferenceTypes } = useReferenceTypes();
  const projectId: number | undefined = location.state.projectId;
  const [initialValueData, setInitialValueData] = useState<CreateProjectForm>(getInitialEmptyValues());
  const [projectStatus, setProjectStatus] = useState<ReferenceType | undefined>(undefined);
  const [project, setProject] = useState<Project | undefined>(undefined);
  const [isLoading, setIsLoading] = useState(true);

  const combinedValidationSchema = Yup.object().shape({
    ...getValidationSchemaStep1(allReferenceTypes, authContext).fields,
    ...validationSchemaStep2.fields,
    ...validationSchemaStep3.fields,
    ...validationSchemaStep4.fields,
  });

  useEffect(() => {
    const loadProjectData = async () => {
      if (projectId) {
        try {
          setIsLoading(true);

          const project = await getProject(projectId);
          setProject(project);
          setProjectStatus(project.projectStatus);

          const formValues = mapProjectDataToFormValues(project);
          setInitialValueData(formValues);
        } catch (error) {
          console.error("Error loading project:", error);
        } finally {
          setIsLoading(false);
        }
      }
    };

    loadProjectData();
  }, [projectId]);

  if (isLoading) {
    return (
      <Container maxWidth="md">
        <Card className="wizard-card">
          <div>Loading Project...</div>
        </Card>
      </Container>
    );
  }

  const handleSubmit = async (formValues: CreateProjectForm): Promise<boolean> => {
    const payload = buildUpdateProjectPayload(formValues, initialValueData, capabilities);

    try {
      const project = await updateProject(payload, projectId);
      if (project) {
        window.scrollTo(0, 0);
        return true;
      }
      return false;
    } catch (_error) {
      return false;
    }
  };

  const handleFormSubmission = async (values: CreateProjectForm) => {
    const submitResult = await handleSubmit(values);

    if (!submitResult) {
      toast.error("Error updating Project");
      return;
    }

    toast.success("Project Updated Successfully");
    navigate(`/projects/${projectId}`);
  };

  const capabilities = getUpdateProjectFieldCapabilities(initialValueData, authContext);

  if (!hasAnyUpdateCapability(capabilities)) {
    return (
      <FullScreenMessage headline={"Project Update Unavailable"} body={"Incorrect permissions or Project Status"} />
    );
  }

  return (
    <ThemeProvider theme={theme}>
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <Helmet>
          <title>Update Project</title>
        </Helmet>
        <Container maxWidth="md">
          <Card className="wizard-card">
            <Box>
              <Typography variant="h4">Update Project</Typography>
              <br></br>
            </Box>
            <Formik
              initialValues={initialValueData}
              validationSchema={combinedValidationSchema}
              onSubmit={handleFormSubmission}
              enableReinitialize
            >
              {({ values, setFieldValue }) => (
                <FormContent
                  values={values}
                  project={project}
                  projectStatus={projectStatus}
                  setFieldValue={setFieldValue}
                  capabilities={capabilities}
                />
              )}
            </Formik>
          </Card>
        </Container>
      </LocalizationProvider>
    </ThemeProvider>
  );
};

export default UpdateProject;

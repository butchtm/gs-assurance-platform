import { Box, Card, Container, Grid, Typography } from "@mui/material";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import dayjs from "dayjs";
import { useContext, useEffect, useState } from "react";
import { Helmet } from "react-helmet-async";
import { useLocation, useNavigate, type Location } from "react-router-dom";
import { toast } from "react-toastify";

import { useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { isMethodologyUpdateRequestType } from "../../../../common/data-types/MethodologyRequestTypes";
import { Project } from "../../../../common/project/Project";
import { CreateBatchRequestPayload, CreateUpdateRequestPayload } from "../../../../common/request/GSRequest";
import { AuthContext } from "../../../Auth/Login/AuthProvider";
import { getProject } from "../../../Project/Project";
import { FormikStep } from "../../../Reusable/Formik/FormikStep";
import { FormikStepper } from "../../../Reusable/Formik/FormikStepper";
import { BatchedProjectRequest, createBatchRequest, createRequest, FormValues } from "../../Requests";

import { getStepBatchedSchema } from "./CreateBatchedRequestSchema";
import { getRequestStep1Schema, methodologiesSchema } from "./CreateRequestSchema";
import { BatchRequestIdWithProjects, CreateRequestForm } from "./Request";
import { RequiredDocumentsTable } from "./RequiredDocumentsTable";
import { Step1 } from "./Step1";
import { StepBatched } from "./StepBatched";
import { Summary } from "./Summary";
import { MethodologiesHandler } from "./methodologies/MethodologiesHandler";
import { RequestRiskWarningBanner } from "./risk/RequestRiskWarningBanner";

interface LocationState {
  project: Project;
}

function useSubmitRequest() {
  const navigate = useNavigate();
  const location: Location<LocationState> = useLocation();

  const submitStandaloneRequest = async (formValues: FormValues) => {
    const {
      requestType,
      creditingPeriodId,
      emissionReductionAmount,
      methodologyConfigurations,
      newCreditingPeriodStartDate,
      newCreditingPeriodEndDate,
      monitoringPeriodId,
      newMonitoringPeriodStartDate,
      newMonitoringPeriodEndDate,
      feeSchedule,
      paa,
    } = formValues;
    const project = location.state.project;
    const requestTypeId = requestType ? parseInt(requestType) : 1;
    const isNew = Boolean(newCreditingPeriodStartDate) && Boolean(newCreditingPeriodEndDate);
    const newMonitoringPeriodId =
      newMonitoringPeriodStartDate !== null && newMonitoringPeriodEndDate !== null ? null : monitoringPeriodId;

    const commonRequest = {
      requestTypeId,
      emissionReductionAmount,
      manuallyAdded: true,
      startDate: dayjs.utc().toISOString(),
      paa,
    };

    const request: Partial<CreateUpdateRequestPayload> = {
      ...commonRequest,
      creditingPeriod: {
        periodId: isNew ? null : creditingPeriodId,
        startDate: isNew ? newCreditingPeriodStartDate : null,
        endDate: isNew ? newCreditingPeriodEndDate : null,
      },
      methodologies: methodologyConfigurations,
      monitoringPeriod: {
        id: newMonitoringPeriodId,
        startDate: newMonitoringPeriodStartDate?.toISOString() ?? null,
        endDate: newMonitoringPeriodEndDate?.toISOString() ?? null,
        feeSchedule: feeSchedule,
      },
    };

    try {
      await createRequest(request, project.id);
      toast.success("Request created successfully");
      navigate(`/projects/${project.gsid}`);
    } catch (error) {
      console.error("Failed to create request:", error);
      toast.error("Failed to create request");
    }
  };

  const submitBatchRequest = async (formValues: CreateRequestForm) => {
    if (formValues.batchRequestTypeId == null) throw Error("Invalid batchRequestTypeId");

    const project = location.state.project;
    const payload: CreateBatchRequestPayload = {
      requestTypeId: Number(formValues.requestType),
      batchTypeId: formValues.batchRequestTypeId,
      paa: formValues.paa,
      projectRequestInfos: formValues.batchProjectIds.map((batchProjectId) => {
        if (!formValues.batchStepProjectValues) throw Error("Invalid batchStepProjectValues");

        const relevantBatch = formValues.batchStepProjectValues.batch[batchProjectId];
        return {
          projectId: batchProjectId,
          creditingPeriod: {
            periodId: relevantBatch.creditingPeriodId,
            startDate: relevantBatch.newCreditingPeriodStartDate,
            endDate: relevantBatch.newCreditingPeriodEndDate,
          },
          emissionReductionAmount: relevantBatch.emissionReductionAmount,
          methodologies: relevantBatch.methodologies,
          monitoringPeriod: {
            id: relevantBatch.monitoringPeriodId,
            startDate: relevantBatch.newMonitoringPeriodStartDate,
            endDate: relevantBatch.newMonitoringPeriodEndDate,
            feeSchedule:
              relevantBatch.isMonitoringPeriodEnabled ||
              !(relevantBatch.monitoringPeriodId == null || relevantBatch.monitoringPeriodId == undefined)
                ? formValues.feeSchedule
                : undefined,
          },
        };
      }),
    };
    try {
      await createBatchRequest(payload);
      toast.success("Batch request created successfully");
      navigate(`/projects/${project.gsid}`);
    } catch (error) {
      console.error("Failed to create batch request:", error);
      toast.error("Failed to create batch request");
    }
  };

  return async (formValues: CreateRequestForm, isBatched: boolean) => {
    if (isBatched) {
      await submitBatchRequest(formValues);
    } else {
      await submitStandaloneRequest(formValues);
    }
  };
}

function CreateRequestWizard() {
  const initialValues: CreateRequestForm = {
    requestType: "",
    creditingPeriodId: null,
    startDate: null,
    endDate: null,
    emissionReductionAmount: 0,
    batchProjectIds: [],
    requiredDocuments: null,
    createBatchRequest: "",
    useNewCreditingPeriod: false,
    canCreateNewCreditingPeriod: false,
    batchRequestTypeId: null,
    batchStepProjectValues: null,
    methodologyConfigurations: [],
    newCreditingPeriodStartDate: null,
    newCreditingPeriodEndDate: null,
    isMonitoringPeriodEnabled: false,
    canCreateNewMonitoringPeriod: false,
    useNewMonitoringPeriod: false,
    monitoringPeriodId: null,
    newMonitoringPeriodStartDate: null,
    newMonitoringPeriodEndDate: null,
    feeSchedule: null,
    paa: false,
  };
  const { activeReferenceTypes } = useReferenceTypes();
  const location: Location<LocationState> = useLocation();
  const project = location.state.project;
  const [isBatched, setIsBatched] = useState<boolean>(false);
  const [batchedRequest, setBatchedRequest] = useState<BatchRequestIdWithProjects | null>(null);
  const [batchedProjects, setBatchedProjects] = useState<BatchedProjectRequest[] | null>(null);
  const [requestTypeId, setRequestTypeId] = useState<number>();
  const [changeStep, setChangeStep] = useState<number>();
  const authContext = useContext(AuthContext);
  const isArmOrAdmin = authContext.isAdminUser() || authContext.isArmUser();

  const submitRequest = useSubmitRequest();

  async function processBatchRequest(batchRequest: BatchRequestIdWithProjects): Promise<Project[]> {
    const projectPromises = batchRequest.projects.map((projectId) =>
      getProject(projectId).catch((error) => {
        console.error(`Failed to get project ${projectId}:`, error);
        return null;
      }),
    );

    const projects = await Promise.all(projectPromises);

    return projects.filter((proj): proj is Project => proj !== null);
  }

  const changeFormikStep = (step: number) => {
    setChangeStep(step);
  };

  useEffect(() => {
    const getBatchedProjects = async () => {
      if (batchedRequest) {
        const projectsToBatch = await processBatchRequest(batchedRequest);
        if (projectsToBatch) {
          const projectBatches = projectsToBatch.map(
            ({ id, gsid, projectClassification, creditingPeriods, name, methodologies, currentRiskSummary }) => ({
              id,
              gsid,
              classification: projectClassification.description,
              creditingPeriods,
              name,
              methodologies,
              currentRiskSummary,
            }),
          );
          setBatchedProjects(projectBatches);
        }
      }
    };
    getBatchedProjects();
  }, [batchedRequest]);

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <Helmet>
        <title>New Request</title>
      </Helmet>
      {isArmOrAdmin && !isBatched && project.currentRiskSummary?.risk && (
        <RequestRiskWarningBanner risk={project.currentRiskSummary} gsid={project.gsid} />
      )}
      <Container maxWidth="md">
        <Card className="wizard-card">
          <Box>
            <Typography variant="h4">New Request</Typography>
            <br></br>
          </Box>
          <FormikStepper
            initialValues={initialValues}
            onSubmit={async (values) => {
              await submitRequest(values, isBatched);
            }}
            changeStep={changeStep}
          >
            <FormikStep
              label="Start"
              validationSchema={getRequestStep1Schema(activeReferenceTypes.RequestType, project.creditingPeriods)}
            >
              <Step1
                project={project}
                setIsBatched={setIsBatched}
                setBatchedRequest={setBatchedRequest}
                setRequestTypeId={setRequestTypeId}
              />
            </FormikStep>
            {!isBatched && isMethodologyUpdateRequestType(requestTypeId, activeReferenceTypes.RequestType) && (
              <FormikStep label="Methodologies" validationSchema={methodologiesSchema}>
                <Box sx={{ display: "flex", flexDirection: "column", alignItems: "center", margin: "0 10%", p: 2 }}>
                  <MethodologiesHandler creditingPeriods={project.creditingPeriods} showPrefillMessage={true} />
                </Box>
              </FormikStep>
            )}
            <FormikStep label="Required Documents">
              <Box sx={{ display: "flex", flexDirection: "column", alignItems: "center", margin: "0 10%", p: 2 }}>
                <Grid container spacing={2}>
                  <Grid item xs={12}>
                    <RequiredDocumentsTable />
                  </Grid>
                </Grid>
              </Box>
            </FormikStep>
            {isBatched &&
              batchedRequest &&
              batchedProjects &&
              batchedProjects.length > 0 && [
                batchedProjects.map((batchedProject) => (
                  <FormikStep
                    label={batchedProject.gsid}
                    key={batchedProject.id}
                    validationSchema={getStepBatchedSchema(
                      batchedProjects,
                      batchedProject.id,
                      batchedRequest.requestTypeId,
                      activeReferenceTypes.RequestType,
                    )}
                  >
                    {isArmOrAdmin && batchedProject.currentRiskSummary?.risk && (
                      <RequestRiskWarningBanner risk={batchedProject.currentRiskSummary} gsid={batchedProject.gsid} />
                    )}
                    <StepBatched
                      requestTypeId={batchedRequest.requestTypeId}
                      project={batchedProject}
                      batchedProjects={batchedProjects}
                    />
                  </FormikStep>
                )),
              ]}
            {isBatched && batchedProjects && batchedProjects.length > 0 && (
              <FormikStep label="Summary">
                <Summary
                  batchedProjects={batchedProjects}
                  setChangeStep={changeFormikStep}
                  isMethodologyUpdateRequestType={isMethodologyUpdateRequestType(
                    requestTypeId,
                    activeReferenceTypes.RequestType,
                  )}
                />
              </FormikStep>
            )}
          </FormikStepper>
        </Card>
      </Container>
    </LocalizationProvider>
  );
}

export default CreateRequestWizard;

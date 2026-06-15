import { Box, Card, Container, Typography } from "@mui/material";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import dayjs from "dayjs";
import React, { useEffect, useState } from "react";
import { Helmet } from "react-helmet-async";
import { useLocation, type Location } from "react-router-dom";
import * as Yup from "yup";

import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { isMethodologyUpdateRequestType } from "../../../common/data-types/MethodologyRequestTypes";
import { Project } from "../../../common/project/Project";
import {
  GSRequest,
  hasDraftCreditingPeriodValues,
  hasNewMonitoringPeriodValues,
} from "../../../common/request/GSRequest";
import { getProject } from "../../Project/Project";
import { FormikStep } from "../../Reusable/Formik/FormikStep";
import { FormikStepper } from "../../Reusable/Formik/FormikStepper";
import { getStepBatchedSchema } from "../create-request/Wizard/CreateBatchedRequestSchema";
import { MethodologiesHandler } from "../create-request/Wizard/methodologies/MethodologiesHandler";
import { CreateRequestForm, ParentRequestWithBatchedRequests } from "../create-request/Wizard/Request";
import { Step1 } from "../create-request/Wizard/Step1";
import { StepBatched } from "../create-request/Wizard/StepBatched";
import { Summary } from "../create-request/Wizard/Summary";
import { BatchedProjectRequest, BatchedProjectRequests, getRequestWithId } from "../Requests";
import { getStandaloneFeeScheduleValue } from "../create-request/Wizard/FeeSchedule";

import { transformToParentRequestWithBatchedRequests, useUpdateRequest } from "./UpdateRequestHelpers";

const UpdateRequestWizard: React.FC = () => {
  const locationState: Location<{
    project: Project;
    request: GSRequest;
    requestBatches: BatchedProjectRequests;
  }> = useLocation();
  const { activeReferenceTypes } = useReferenceTypes();
  const { request, requestBatches } = locationState.state || {};
  const [project, setProject] = useState<Project | null>(null);
  const [batchedProjects, setBatchedProjects] = useState<BatchedProjectRequest[]>([]);
  const [batchProjectIds, setBatchProjectIds] = useState<number[]>([]);
  const [loading, setLoading] = useState(true);
  const [changeStep, setChangeStep] = useState<number>();
  const [lastStep, setLastStep] = useState<boolean>(false);

  const [parentRequestWithBatchedRequests, setParentRequestWithBatchedRequests] =
    useState<ParentRequestWithBatchedRequests>();

  const createRequestStep1Schema = Yup.object().shape({
    requestType: Yup.string().required("Please choose a request type"),
    createBatchRequest: Yup.string().required("Please selected whether this request is a batch"),
    batchRequestTypeId: Yup.string().when("createBatchRequest", {
      is: (val: string) => val === "yes",
      then: (schema) => schema.required("Please select a batch type"),
      otherwise: (schema) => schema.notRequired(),
    }),
    batchProjectIds: Yup.array().when("createBatchRequest", {
      is: (val: string) => val === "yes",
      then: (schema) => schema.min(2, "Please select at least two batch projects to batch together"),
      otherwise: (schema) => schema.notRequired(),
    }),
  });

  const methodologyShapeSchema = Yup.object({
    methodologyVersion: Yup.number().required(),
    methodologyId: Yup.number().required(),
  });

  const methodologiesSchema = Yup.object().shape({
    methodologyConfigurations: Yup.array().of(methodologyShapeSchema).min(1, "Add at least one methodology"),
  });

  useEffect(() => {
    const fetchData = async () => {
      if (request.project?.id) {
        const projectResponse = await getProject(request.project.id);
        setProject(projectResponse || null);
      }
    };

    fetchData();
  }, []);

  useEffect(() => {
    const fetchBatchData = async () => {
      try {
        if (requestBatches) {
          setLastStep(true);
          const batchIds = [requestBatches.parent.id, ...requestBatches.children.map((child) => child.id)];
          setBatchProjectIds(batchIds);

          setBatchedProjects([requestBatches.parent, ...requestBatches.children]);

          const requestIds = [
            requestBatches.parent.requestId,
            ...requestBatches.children.map((child) => child.requestId),
          ];

          const requestResponses = await Promise.all(
            requestIds.map((id) => {
              return id ? getRequestWithId(id) : undefined;
            }),
          );

          const validResponses = requestResponses.filter((response): response is GSRequest => response !== undefined);

          if (validResponses && requestBatches) {
            setParentRequestWithBatchedRequests(
              transformToParentRequestWithBatchedRequests(validResponses, requestBatches),
            );
          }
        }
      } catch {
        console.error("error fetching batch data");
      } finally {
        setLoading(false);
      }
    };
    fetchBatchData();
  }, [changeStep]);

  const hasDraftCreditingPeriod = hasDraftCreditingPeriodValues(request);
  const hasNewMonitoringPeriod = hasNewMonitoringPeriodValues(request);
  const initialValues: CreateRequestForm = {
    requestType: request.requestType.id.toString(),
    creditingPeriodId: request.creditingPeriod?.periodId,
    startDate: request.startDate,
    endDate: request.endDate,
    emissionReductionAmount: request.emissionReductionAmount,
    batchProjectIds: batchProjectIds ?? [],
    requiredDocuments: null,
    createBatchRequest: request.batchId ? "yes" : "no",
    useNewCreditingPeriod: hasDraftCreditingPeriod,
    canCreateNewCreditingPeriod: hasDraftCreditingPeriod,
    batchRequestTypeId: request.batchTypeId,
    batchStepProjectValues: parentRequestWithBatchedRequests ?? null,
    methodologyConfigurations: request.methodologies,
    newCreditingPeriodStartDate: hasDraftCreditingPeriod ? request.creditingPeriod?.startDate : null,
    newCreditingPeriodEndDate: hasDraftCreditingPeriod ? request.creditingPeriod?.endDate : null,
    monitoringPeriodId: request.monitoringPeriod?.id,
    useNewMonitoringPeriod: hasNewMonitoringPeriod,
    canCreateNewMonitoringPeriod: hasNewMonitoringPeriod,
    isMonitoringPeriodEnabled: false,
    newMonitoringPeriodStartDate: hasNewMonitoringPeriod ? dayjs.utc(request.monitoringPeriod.startDate) : null,
    newMonitoringPeriodEndDate: hasNewMonitoringPeriod ? dayjs.utc(request.monitoringPeriod.endDate) : null,
    feeSchedule: request.batchId
      ? (parentRequestWithBatchedRequests?.feeSchedule ?? null)
      : getStandaloneFeeScheduleValue(request),
    paa: request.paa,
  };

  const updateRequestWrapper = useUpdateRequest();

  const changeFormikStep = (step: number) => {
    setLastStep(false);
    setChangeStep(step);
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <Helmet>
        <title>Update Request</title>
      </Helmet>
      <Container maxWidth="md">
        <Card className="wizard-card">
          <Box>
            <Typography variant="h4">Update Request</Typography>
            <br></br>
          </Box>
          <FormikStepper
            initialValues={initialValues}
            onSubmit={async (values) => {
              await updateRequestWrapper(values);
            }}
            changeStep={changeStep}
            setToLastStep={lastStep}
            enableAll={true}
          >
            {requestBatches
              ? [
                  <FormikStep label="Start" key="step1" validationSchema={createRequestStep1Schema}>
                    {project && <Step1 project={project} requestEditId={request.id} />}
                  </FormikStep>,
                  ...batchedProjects.map((batchProj) => (
                    <FormikStep
                      label={batchProj.gsid}
                      key={batchProj.id}
                      validationSchema={getStepBatchedSchema(
                        batchedProjects,
                        batchProj.id,
                        request.requestType.id,
                        activeReferenceTypes.RequestType,
                      )}
                    >
                      {project && (
                        <StepBatched
                          requestTypeId={request.requestType.id}
                          project={batchProj}
                          batchedProjects={batchedProjects}
                        />
                      )}
                    </FormikStep>
                  )),
                  <FormikStep label="Summary" key="summary">
                    {project && (
                      <Summary
                        batchedProjects={batchedProjects}
                        setChangeStep={changeFormikStep}
                        isMethodologyUpdateRequestType={isMethodologyUpdateRequestType(
                          request.requestType.id,
                          activeReferenceTypes.RequestType,
                        )}
                        summaryUpdating={true}
                      />
                    )}
                  </FormikStep>,
                ]
              : [
                  <FormikStep label="Start" key="step1" validationSchema={createRequestStep1Schema}>
                    {project && <Step1 project={project} requestEditId={request.id} />}
                  </FormikStep>,
                  isMethodologyUpdateRequestType(request.requestType.id, activeReferenceTypes.RequestType) && (
                    <FormikStep label="Methodologies" key="methodologies" validationSchema={methodologiesSchema}>
                      <Box
                        sx={{ display: "flex", flexDirection: "column", alignItems: "center", margin: "0 10%", p: 2 }}
                      >
                        <MethodologiesHandler creditingPeriods={project?.creditingPeriods ?? []} />
                      </Box>
                    </FormikStep>
                  ),
                ]}
          </FormikStepper>
        </Card>
      </Container>
    </LocalizationProvider>
  );
};

export default UpdateRequestWizard;

import AddIcon from "@mui/icons-material/Add";
import { Alert, Box, Button, FormControl, Grid, SelectChangeEvent, TextField, Typography } from "@mui/material";
import { useFormikContext } from "formik";
import React, { useContext, useEffect, useState } from "react";

import {
  ReferenceTypeContext,
  getReferenceValueCodeById,
  useReferenceTypes,
} from "../../../../common/data-types/DataReferenceTypes";
import { isMethodologyUpdateRequestType } from "../../../../common/data-types/MethodologyRequestTypes";
import {
  CreditingPeriod,
  findNewCreditingPeriodStartDate,
  methodologiesExactlyTheSame,
  toDefaultEndDate,
} from "../../../../common/project/Project";
import {
  canRequestTypeCreateCreditingPeriod,
  canRequestTypeCreateMonitoringPeriod,
  findFirstVpa,
  findMonitoringPeriodWithMatchingEndDate,
} from "../../../../common/request/GSRequest";
import { EmissionsReductionInput } from "../../../Reusable/FormFields/EmissionsReductionInput";
import { BatchedProjectRequest } from "../../Requests";
import { requestsHelperText } from "../RequestHelpers";

import { CreateRequestForm } from "./Request";
import { CustomBatchedCreditingPeriodSelect } from "./crediting-periods/CustomBatchedCreditingPeriodSelect";
import { StepBatchedNewCreditingFields } from "./crediting-periods/StepBatchedNewCreditingFields";
import { MethodologiesBatchHandler } from "./methodologies/MethodologiesBatchHandler";
import { MonitoringPeriodsBatchHandler } from "./monitoring-periods/MonitoringPeriodsBatchHandler";

const INCOMPATIBLE_METHODOLOGIES_STRING = " - Methodologies Incompatible with ";

interface StepBatchedProps {
  requestTypeId: number;
  project: BatchedProjectRequest;
  batchedProjects: BatchedProjectRequest[];
}

export const StepBatched: React.FC<StepBatchedProps> = ({ requestTypeId, project, batchedProjects }) => {
  const { values, setFieldValue, submitCount, errors } = useFormikContext<CreateRequestForm>();
  const { activeReferenceTypes } = useReferenceTypes();
  const currentRequestTypeDescription = activeReferenceTypes.RequestType.find(
    (type) => type.id === requestTypeId,
  )?.description;
  const shouldDisablePeriods = !isMethodologyUpdateRequestType(requestTypeId, activeReferenceTypes.RequestType);
  const referenceDataContext = useContext(ReferenceTypeContext);
  const currentRequestTypeCode = getReferenceValueCodeById(
    Number(values.requestType),
    referenceDataContext.allReferenceTypes.RequestType,
  );
  const [canCreateCreditingPeriod, setCanCreateCreditingPeriod] = useState<boolean>(
    values.batchStepProjectValues?.batch[project.id]?.canCreateNewCreditingPeriod ?? false,
  );
  const [addNewCreditingPeriod, setAddNewCreditingPeriod] = useState<boolean>(
    values.batchStepProjectValues?.batch[project.id]?.useNewCreditingPeriod ?? false,
  );
  const [areSelectionsFixed, setAreSelectionsFixed] = useState<boolean>(false);

  const currentProjectSelector = `batchStepProjectValues.batch.${project.id}`;

  const setCanCreateCreditingPeriodOnCurrentProject = (canCreateNewPeriod: boolean) => {
    setCanCreateCreditingPeriod(canCreateNewPeriod);
    setFieldValue(`${currentProjectSelector}.canCreateNewCreditingPeriod`, canCreateNewPeriod);
  };

  const isNewCreditingPeriodAllowed = () => {
    const requestAllowsNewPeriod =
      currentRequestTypeCode && canRequestTypeCreateCreditingPeriod(currentRequestTypeCode);

    const hasPeriodWithFutureStartDate: boolean | undefined = project.creditingPeriods?.some(
      ({ startDate }) => new Date(startDate) > new Date(),
    );

    return Boolean(requestAllowsNewPeriod && !hasPeriodWithFutureStartDate);
  };

  const isPoA = project.classification === "Programme of Activities";
  const isMonitoringPeriodRequestType =
    currentRequestTypeCode && canRequestTypeCreateMonitoringPeriod(currentRequestTypeCode);
  const firstVpa = findFirstVpa(batchedProjects);

  const isFirstVpa = () => {
    return firstVpa != undefined && firstVpa.id === project.id;
  };

  const projectIds = batchedProjects.map((p) => p.id);
  const isLastProjectInBatch = projectIds.at(-1) === project.id;

  useEffect(() => {
    if (!values.batchStepProjectValues) {
      setFieldValue("batchStepProjectValues", {
        requestTypeId: values.requestType,
        creditingPeriodId: values.creditingPeriodId,
        startDate: values.startDate,
        emissionReductionAmount: values.emissionReductionAmount,
        batch: {
          batchTypeId: values.batchRequestTypeId,
          [project.id]: {
            methodologies: [],
            newCreditingPeriodStartDate: null,
            newCreditingPeriodEndDate: null,
            useNewCreditingPeriod: false,
            creditingPeriodId: null,
          },
        },
        feeSchedule: null,
      });
    }
    setCanCreateCreditingPeriodOnCurrentProject(isNewCreditingPeriodAllowed());
    setFieldValue(`${currentProjectSelector}.isMonitoringPeriodEnabled`, !isPoA && isMonitoringPeriodRequestType);
    if (!isPoA && isMonitoringPeriodRequestType && !isFirstVpa() && firstVpa) {
      const matchedMonitoringPeriod = findMonitoringPeriodWithMatchingEndDate(
        firstVpa,
        project,
        values.batchStepProjectValues?.batch[firstVpa?.id],
      );
      if (matchedMonitoringPeriod) {
        setPeriodValue(project.id, matchedMonitoringPeriod.creditingPeriodId);
        setFieldValue(`${currentProjectSelector}.monitoringPeriodId`, matchedMonitoringPeriod.id);
        setAreSelectionsFixed(true);
      } else {
        setFieldValue(`${currentProjectSelector}.monitoringPeriodId`, null);
        setAreSelectionsFixed(false);
      }
    }
  }, []);

  useEffect(() => {
    if (
      isLastProjectInBatch &&
      values.batchStepProjectValues &&
      values.batchStepProjectValues.finalProjectInitialSubmitCount === undefined
    ) {
      setFieldValue("batchStepProjectValues.finalProjectInitialSubmitCount", submitCount);
    }
  }, [isLastProjectInBatch, submitCount, setFieldValue, values.batchStepProjectValues]);

  const isCreditingPeriodDisabled = (toComparePeriod: CreditingPeriod) => {
    if (shouldNotDisablePeriods()) {
      return false;
    }
    if (!isFirstVpa()) {
      const firstVpaMethodologies = getFirstVpaProjectMethodologies();
      const toCompareMethodologies = toComparePeriod.methodologies;
      return methodologiesExactlyTheSame(firstVpaMethodologies, toCompareMethodologies);
    }
  };

  const shouldNotDisablePeriods = () => {
    return project.classification == "Programme of Activities" || !shouldDisablePeriods;
  };

  const getFirstVpaProjectMethodologies = () => {
    return firstVpa?.creditingPeriods?.find(
      (period) => period.periodId === values.batchStepProjectValues?.batch[firstVpa.id]?.creditingPeriodId,
    )?.methodologies;
  };

  const handlePeriodIdChange = (event: SelectChangeEvent<string | number>) => {
    const value = event.target.value;
    if (value) {
      const newCreditingPeriodId = Number(value);
      setPeriodValue(project.id, newCreditingPeriodId);
    }
  };

  const setPeriodValue = (projectId: number, newCreditingPeriodId: number) => {
    let batchValues = values.batchStepProjectValues;
    if (isFirstVpa() && shouldDisablePeriods) {
      batchValues = null;
    }
    const updatedBatchStepProjectValues = {
      batchValues,
      batchTypeIds: values?.batchRequestTypeId,
      batch: {
        ...batchValues?.batch,
        [batchedProjects[0].id]: { ...values.batchStepProjectValues?.batch[batchedProjects[0].id] },
        [projectId]: {
          ...values.batchStepProjectValues?.batch[projectId],
          creditingPeriodId: newCreditingPeriodId,
        },
      },
    };
    setFieldValue("batchStepProjectValues", updatedBatchStepProjectValues);
  };

  const handleEmissionReductionChange = (value: number) => {
    setFieldValue(`${currentProjectSelector}.emissionReductionAmount`, value);
  };

  const resetCurrentProjectMethodologies = () => {
    setFieldValue(`${currentProjectSelector}.methodologies`, []);
  };

  const handleAddNew = () => {
    setAddNewCreditingPeriod(true);
    const newStartDate = findNewCreditingPeriodStartDate(project?.creditingPeriods ?? []);
    setFieldValue(`${currentProjectSelector}.creditingPeriodId`, null);
    setFieldValue(`${currentProjectSelector}.useNewCreditingPeriod`, true);
    setFieldValue(`${currentProjectSelector}.newCreditingPeriodStartDate`, newStartDate.toISOString());
    setFieldValue(`${currentProjectSelector}.newCreditingPeriodEndDate`, toDefaultEndDate(newStartDate).toISOString());
    resetCurrentProjectMethodologies();
  };

  const handleUseExisting = () => {
    setAddNewCreditingPeriod(false);
    setFieldValue(`${currentProjectSelector}.useNewCreditingPeriod`, false);
    setFieldValue(`${currentProjectSelector}.newCreditingPeriodStartDate`, null);
    setFieldValue(`${currentProjectSelector}.newCreditingPeriodEndDate`, null);
    resetCurrentProjectMethodologies();
  };

  if (!values) {
    return (
      <Box sx={{ display: "flex", flexDirection: "column", alignItems: "center", margin: "0 10%", p: 2 }}>
        <Typography variant="h6" gutterBottom>
          Form expired - please go back and try again.
        </Typography>
        <Button variant="contained" color="primary" onClick={() => history.back()}>
          Go Back
        </Button>
      </Box>
    );
  }

  // Determine if any batched project has validation errors (used only on last project step)
  let hasAnyProjectErrors = false;
  const finalProjectInitialSubmitCount = values.batchStepProjectValues?.finalProjectInitialSubmitCount;
  const hasAttemptedSubmitOnFinalProject =
    finalProjectInitialSubmitCount !== undefined && submitCount > finalProjectInitialSubmitCount;

  if (isLastProjectInBatch) {
    const batchErrors = (
      errors as unknown as {
        batchStepProjectValues?: { batch?: Record<string, unknown> };
      }
    )?.batchStepProjectValues?.batch;
    if (batchErrors) {
      hasAnyProjectErrors = Object.values(batchErrors).some(
        (projErr) => !!projErr && Object.keys(projErr as object).length > 0,
      );
    }
  }

  return (
    <Box sx={{ display: "flex", flexDirection: "column", alignItems: "center", margin: "0 10%", p: 2 }}>
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <Grid container spacing={2}>
            {isLastProjectInBatch && hasAnyProjectErrors && hasAttemptedSubmitOnFinalProject && (
              <Grid item xs={12}>
                <Alert severity="error" data-testid="batch-projects-validation-error" sx={{ mb: 1 }}>
                  There is a validation error on one or more projects in the batch, please go back and review these
                  projects.
                </Alert>
              </Grid>
            )}
            <Grid item xs={12}>
              <FormControl fullWidth variant="outlined">
                <TextField
                  label="Request Type"
                  value={currentRequestTypeDescription}
                  variant="outlined"
                  fullWidth
                  disabled
                  data-testid="request-type-input"
                />
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl fullWidth variant="outlined">
                <TextField
                  label="Project"
                  value={project.gsid + " - " + project.name}
                  variant="outlined"
                  fullWidth
                  disabled
                  data-testid="project-name-input"
                />
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl fullWidth variant="outlined">
                <TextField
                  label="Project Class"
                  value={project.classification}
                  variant="outlined"
                  fullWidth
                  disabled
                  data-testid="project-class-input"
                />{" "}
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              {addNewCreditingPeriod ? (
                <StepBatchedNewCreditingFields batchedProject={project} />
              ) : (
                <CustomBatchedCreditingPeriodSelect
                  project={project}
                  areSelectionsFixed={areSelectionsFixed}
                  onPeriodChange={handlePeriodIdChange}
                  isCreditingPeriodDisabled={isCreditingPeriodDisabled}
                  incompatibleMethodologiesString={INCOMPATIBLE_METHODOLOGIES_STRING}
                  firstVpaGsid={firstVpa?.gsid}
                />
              )}
              {canCreateCreditingPeriod && (
                <Grid item xs={12} sx={{ mt: 2 }}>
                  {addNewCreditingPeriod ? (
                    <Button variant="outlined" size="small" onClick={handleUseExisting}>
                      Use Existing Crediting Period
                    </Button>
                  ) : (
                    <Button variant="outlined" size="small" onClick={handleAddNew} data-testid="add-new-period-button">
                      <AddIcon />
                      Add New Crediting Period
                    </Button>
                  )}
                </Grid>
              )}
              <Grid>
                {isMethodologyUpdateRequestType(requestTypeId, activeReferenceTypes.RequestType) &&
                  (addNewCreditingPeriod ||
                    !!values.batchStepProjectValues?.batch?.[project.id]?.creditingPeriodId) && (
                    <Box sx={{ paddingTop: "10px" }}>
                      <MethodologiesBatchHandler
                        showPrefillMessage={true}
                        creditingPeriods={project.creditingPeriods ?? []}
                        batchedProject={project}
                        batchedProjects={batchedProjects}
                      />
                    </Box>
                  )}
              </Grid>
            </Grid>
            {project.creditingPeriods &&
              !!values.batchStepProjectValues?.batch[project.id]?.creditingPeriodId &&
              !isPoA &&
              isMonitoringPeriodRequestType && (
                <MonitoringPeriodsBatchHandler
                  creditingPeriods={project.creditingPeriods}
                  batchedProject={project}
                  batchedProjects={batchedProjects}
                  isFixedPeriod={areSelectionsFixed}
                />
              )}
            <Grid item xs={12}>
              <EmissionsReductionInput
                fieldValue={values.batchStepProjectValues?.batch?.[project.id]?.emissionReductionAmount}
                helpText={requestsHelperText.emissionsReduction()}
                onChange={handleEmissionReductionChange}
                name={`${currentProjectSelector}.emissionReductionAmount`}
                deferUntilFinalSubmit
              />
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    </Box>
  );
};

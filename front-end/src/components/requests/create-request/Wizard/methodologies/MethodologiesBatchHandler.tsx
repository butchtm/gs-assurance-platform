import { Box, Typography } from "@mui/material";
import { useField, useFormikContext } from "formik";
import React, { useEffect, useState } from "react";

import { useReferenceTypes } from "../../../../../common/data-types/DataReferenceTypes";
import { computeDeferredBatchError } from "../../../../../common/form/deferredBatchErrors";
import { CreditingPeriod, Methodology } from "../../../../../common/project/Project";
import { helperTexts } from "../../../../Project/create-project/HelperText";
import CustomTooltipText, { CustomTooltip } from "../../../../Reusable/FormFields/CustomTooltipText";
import { BatchedProjectRequest } from "../../../Requests";
import { CreateRequestForm, MethodologyConfiguration } from "../Request";

import { MethodologiesPicker } from "./MethodologiesPicker";
import {
  FIRST_VPA_MESSAGE,
  MULTIPLE_CHILD_VPA_MESSAGE,
  NO_METHODOLOGIES_MESSAGE,
  prefillMethodologiesFromProject,
  toAllowedMethodologyOptions,
  toMethodologyIds,
  VPA_DESCRIPTIONS_LOWER_CASE,
} from "./Methodology";

export const MethodologiesBatchHandler: React.FC<{
  creditingPeriods: CreditingPeriod[];
  showPrefillMessage?: boolean;
  batchedProject: BatchedProjectRequest;
  batchedProjects: BatchedProjectRequest[];
}> = ({ creditingPeriods, showPrefillMessage, batchedProject, batchedProjects }) => {
  const { values, setFieldValue, submitCount } = useFormikContext<CreateRequestForm>();
  const [creditingPeriodId, setCreditingPeriodId] = useState<number | null>(null);
  const [prefilledMessage, setPrefilledMessage] = useState<string>(NO_METHODOLOGIES_MESSAGE);
  const { referenceMethodologies, getCustomReferenceOptions } = useReferenceTypes();
  const [existingMethodologyIds] = useState<number[]>(
    toMethodologyIds(values.batchStepProjectValues?.batch?.[batchedProject.id].methodologies),
  );

  const allowedMethodologyOptions = toAllowedMethodologyOptions(
    referenceMethodologies,
    existingMethodologyIds,
    getCustomReferenceOptions("Methodology", []),
  );

  const setPrefilledMethodologies = (methodologies: Methodology[], creditingPeriodId?: number | null) => {
    const formattedMethodologies: MethodologyConfiguration[] = methodologies.map((methodology) => {
      return {
        methodologyId: methodology.id ?? methodology.methodologyId,
        methodologyVersion: methodology.version ?? 0,
        creditingPeriodId: creditingPeriodId ?? null,
      };
    });
    updateBatchStepProjectValues(formattedMethodologies);
  };

  const currentProjectSelector = `batchStepProjectValues.batch.${batchedProject.id}`;

  const updateBatchStepProjectValues = (methodologies: MethodologyConfiguration[]) => {
    setFieldValue(`${currentProjectSelector}.methodologies`, methodologies);
  };

  const isFirstVpaOfBatch = () => {
    const isVpa = VPA_DESCRIPTIONS_LOWER_CASE.includes(batchedProject.classification.toLowerCase());
    const firstVpaIndex = batchedProjects?.findIndex((proj) =>
      VPA_DESCRIPTIONS_LOWER_CASE.includes(proj.classification.toLowerCase()),
    );
    const currentIndex = values.batchProjectIds.findIndex((x) => x == batchedProject.id);
    return isVpa && firstVpaIndex == currentIndex;
  };

  const isFirstOfManyBatchVpas = () => {
    const vpaCount = batchedProjects?.filter((proj) =>
      VPA_DESCRIPTIONS_LOWER_CASE.includes(proj.classification.toLowerCase()),
    ).length;
    return isFirstVpaOfBatch() && vpaCount > 1;
  };

  const isPoaOrFirstVpaOfBatch = () => {
    const isPoa = batchedProject.classification === "Programme of Activities";
    return isPoa || isFirstVpaOfBatch();
  };

  useEffect(() => {
    const currentProject = values.batchStepProjectValues?.batch?.[batchedProject.id];
    const isNewCreditingPeriod = currentProject?.useNewCreditingPeriod;
    const selectedCreditingPeriodId = currentProject?.creditingPeriodId;
    setCreditingPeriodId(selectedCreditingPeriodId ?? null);

    const creditingPeriod = creditingPeriods.find((x) => x.periodId == selectedCreditingPeriodId);
    if (!creditingPeriod && !isNewCreditingPeriod) {
      setPrefilledMessage(NO_METHODOLOGIES_MESSAGE);
      return;
    }

    const shouldPrefillMethodologies =
      isPoaOrFirstVpaOfBatch() &&
      ((currentProject?.creditingPeriodId !== null && !currentProject?.methodologies) ||
        currentProject?.methodologies.filter((x) => x.creditingPeriodId == selectedCreditingPeriodId).length == 0);
    if (shouldPrefillMethodologies) {
      prefillMethodologiesFromProject(
        creditingPeriod,
        creditingPeriods,
        setPrefilledMethodologies,
        setPrefilledMessage,
      );
    }

    if (!isPoaOrFirstVpaOfBatch()) {
      const firstVpa = batchedProjects?.find((proj) =>
        VPA_DESCRIPTIONS_LOWER_CASE.includes(proj.classification.toLowerCase()),
      );
      if (firstVpa) {
        const firstVpaValues = values.batchStepProjectValues?.batch[firstVpa.id]?.methodologies;
        const firstVpaMethodologies: Methodology[] = firstVpaValues
          ? firstVpaValues.map((methodology) => {
              return { id: Number(methodology.methodologyId), version: methodology.methodologyVersion };
            })
          : [];
        setPrefilledMethodologies(firstVpaMethodologies, currentProject?.creditingPeriodId);
        setPrefilledMessage(MULTIPLE_CHILD_VPA_MESSAGE);
      }
    }
  }, [values.batchStepProjectValues?.batch?.[batchedProject.id].creditingPeriodId]);

  const fieldPath = `${currentProjectSelector}.methodologies`;
  const [field, meta] = useField<MethodologyConfiguration[] | undefined>({ name: fieldPath });

  const getDeferredMethodologyError = () => {
    const methodologies = field.value || [];
    const missingMethodologies = methodologies.length === 0;
    const missingVersionOrId = methodologies.some((m) => {
      if (!m) return true;
      const idInvalid = m.methodologyId === "" || m.methodologyId == null;
      const versionInvalid = m.methodologyVersion == null || m.methodologyVersion <= 0;
      return idInvalid || versionInvalid;
    });
    const isMissing = missingMethodologies || missingVersionOrId;

    const { showError, errorMessage } = computeDeferredBatchError({
      meta: typeof meta.error === "string" ? meta : { ...meta, error: undefined },
      formValues: values,
      submitCount,
      required: true,
      isMissing,
      requiredMessage: missingVersionOrId
        ? "Add at least one methodology with a version"
        : "Add at least one methodology",
      deferUntilFinal: true,
    });
    return { showError, errorMessage, missingVersionOrId };
  };

  const { showError, errorMessage, missingVersionOrId } = getDeferredMethodologyError();

  return (
    <>
      <Box display="flex" alignItems="center" sx={{ mb: 2 }}>
        <Typography variant="h6">Methodologies</Typography>
        <Box ml={2}>
          <CustomTooltipText text={helperTexts.methodologyVersionNumber()} type={CustomTooltip.HELP} />
        </Box>
      </Box>
      {showPrefillMessage && (
        <Typography
          variant="body2"
          sx={{ paddingBottom: isFirstOfManyBatchVpas() ? "10px" : "15px", color: "var(--color-text-muted-dark)" }}
        >
          {prefilledMessage}
        </Typography>
      )}
      {showPrefillMessage && isFirstOfManyBatchVpas() && (
        <Typography variant="body2" sx={{ paddingBottom: "15px", color: "var(--color-text-muted-dark)" }}>
          {FIRST_VPA_MESSAGE}
        </Typography>
      )}
      {(creditingPeriodId ||
        (values.batchStepProjectValues?.batch?.[batchedProject.id].newCreditingPeriodStartDate &&
          values.batchStepProjectValues?.batch?.[batchedProject.id].newCreditingPeriodEndDate)) && (
        <MethodologiesPicker
          methodologies={values.batchStepProjectValues?.batch?.[batchedProject.id].methodologies}
          methodologyOptions={allowedMethodologyOptions}
          allDisabled={!isPoaOrFirstVpaOfBatch()}
          creditingPeriodId={creditingPeriodId}
          fieldName={fieldPath}
        />
      )}
      {showError && (
        <Typography variant="caption" color="error" sx={{ display: "block", mt: 1 }} data-testid="methodologies-error">
          {errorMessage ||
            (missingVersionOrId ? "Add at least one methodology with a version" : "Add at least one methodology")}
        </Typography>
      )}
    </>
  );
};

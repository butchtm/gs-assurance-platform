import { Box, Typography } from "@mui/material";
import { useFormikContext } from "formik";
import React, { useEffect, useState } from "react";

import { useReferenceTypes } from "../../../../../common/data-types/DataReferenceTypes";
import { CreditingPeriod, Methodology } from "../../../../../common/project/Project";
import { helperTexts } from "../../../../Project/create-project/HelperText";
import CustomTooltipText, { CustomTooltip } from "../../../../Reusable/FormFields/CustomTooltipText";
import { CreateRequestForm, MethodologyConfiguration } from "../Request";

import { CreditingPeriodDates } from "./CreditingPeriodDates";
import { MethodologiesPicker } from "./MethodologiesPicker";
import {
  NO_METHODOLOGIES_MESSAGE,
  prefillMethodologiesFromProject,
  toAllowedMethodologyOptions,
  toMethodologyIds,
} from "./Methodology";

export const MethodologiesHandler: React.FC<{
  creditingPeriods: CreditingPeriod[];
  showPrefillMessage?: boolean;
}> = ({ creditingPeriods, showPrefillMessage }) => {
  const { values, setFieldValue } = useFormikContext<CreateRequestForm>();
  const [creditingPeriodId, setCreditingPeriodId] = useState<number | null>(null);
  const [prefilledMessage, setPrefilledMessage] = useState<string>(NO_METHODOLOGIES_MESSAGE);
  const { referenceMethodologies, getCustomReferenceOptions } = useReferenceTypes();
  const [existingMethodologyIds] = useState<number[]>(toMethodologyIds(values.methodologyConfigurations));

  const allowedMethodologyOptions = toAllowedMethodologyOptions(
    referenceMethodologies,
    existingMethodologyIds,
    getCustomReferenceOptions("Methodology", []),
  );

  const setPrefilledMethodologies = (methodologies: Methodology[], creditingPeriodId?: number) => {
    const formattedMethodologies: MethodologyConfiguration[] = methodologies.map((methodology) => {
      return {
        methodologyId: methodology.id,
        methodologyVersion: methodology.version ?? 0,
        creditingPeriodId: creditingPeriodId ?? 0,
      };
    });
    setFieldValue("methodologyConfigurations", formattedMethodologies);
  };

  const isNewCreditingPeriod = values.newCreditingPeriodStartDate && values.newCreditingPeriodEndDate;

  useEffect(() => {
    const selectedCreditingPeriodId = values.creditingPeriodId;
    setCreditingPeriodId(selectedCreditingPeriodId);

    const creditingPeriod = creditingPeriods.find((x) => x.periodId == selectedCreditingPeriodId);
    if (!creditingPeriod && !isNewCreditingPeriod) {
      return;
    }

    const shouldPrefillMethodologiesFromProject =
      (creditingPeriodId !== null && !values.methodologyConfigurations) ||
      values.methodologyConfigurations.filter((x) => x.creditingPeriodId == selectedCreditingPeriodId).length == 0;

    if (shouldPrefillMethodologiesFromProject) {
      prefillMethodologiesFromProject(
        creditingPeriod,
        creditingPeriods,
        setPrefilledMethodologies,
        setPrefilledMessage,
      );
    }
  }, [values.creditingPeriodId]);

  return (
    <>
      <Box display="flex" alignItems="center" sx={{ mb: 2 }}>
        <Typography variant="h6">Methodologies</Typography>
        <Box ml={2}>
          <CustomTooltipText text={helperTexts.methodologyVersionNumber()} type={CustomTooltip.HELP} />
        </Box>
      </Box>
      <CreditingPeriodDates creditingPeriodId={creditingPeriodId} creditingPeriods={creditingPeriods} />

      {showPrefillMessage && (
        <Typography variant="body2" sx={{ paddingBottom: "15px", color: "var(--color-text-muted-dark)" }}>
          {prefilledMessage}
        </Typography>
      )}
      {(creditingPeriodId || isNewCreditingPeriod) && (
        <MethodologiesPicker
          methodologies={values.methodologyConfigurations}
          methodologyOptions={allowedMethodologyOptions}
          allDisabled={false}
          creditingPeriodId={creditingPeriodId}
          fieldName={"methodologyConfigurations"}
        />
      )}
    </>
  );
};

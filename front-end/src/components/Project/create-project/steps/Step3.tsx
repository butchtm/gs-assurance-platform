import { Box, Grid } from "@mui/material";
import { useFormikContext } from "formik";
import { useEffect, useState } from "react";

import { Options, useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { CreateProjectForm, findAllMethodologyIds } from "../../../../common/project/Project";
import { getCandidateParentDetails } from "../../Project";

import { CreateCreditingPeriod } from "./CreateCreditingPeriod";

interface Step3Props {
  isUpdate?: boolean;
}

export const Step3 = ({ isUpdate }: Step3Props) => {
  const { values } = useFormikContext<CreateProjectForm>();
  const [methodologyOptions, setMethodologyOptions] = useState<Options[]>([]);
  const { referenceMethodologies, getCustomReferenceOptions } = useReferenceTypes();
  const allMethodologyOptions = getCustomReferenceOptions("Methodology", []);

  useEffect(() => {
    let ignore = false;

    const getParentProjectMethodologies = async (parentProjectId: number, existingMethodologyIds: number[]) => {
      if (values.accountId == null || values.projectClassificationId == null) {
        return;
      }
      const parentProject = await getCandidateParentDetails({
        accountId: values.accountId,
        classificationId: values.projectClassificationId,
        candidateProjectId: parentProjectId,
        masterProjectId: values.masterProjectId,
      });
      if (ignore) {
        return;
      }
      const parentProjectMethodologyIds = findAllMethodologyIds(parentProject.creditingPeriods);

      const parentProjectMethodologyIdsActive = referenceMethodologies
        .filter((m) => m.active && parentProjectMethodologyIds.includes(Number(m.id)))
        .map((m) => m.id);

      const allowedMethodologyIds = [...new Set([...existingMethodologyIds, ...parentProjectMethodologyIdsActive])];

      setMethodologyOptions(
        allMethodologyOptions.filter((option) => allowedMethodologyIds.includes(Number(option.value))),
      );
    };

    if (!allMethodologyOptions) {
      return;
    }
    const existingMethodologyIds = findAllMethodologyIds(values.creditingPeriods);
    if (values.parentProjectId) {
      getParentProjectMethodologies(values.parentProjectId, existingMethodologyIds);
    } else {
      const activeMethodologyIds = referenceMethodologies.filter((m) => m.active).map((m) => m.id);
      const allowedMethodologyIds = [...new Set([...existingMethodologyIds, ...activeMethodologyIds])];
      setMethodologyOptions(
        allMethodologyOptions.filter((option) => allowedMethodologyIds.includes(Number(option.value))),
      );
    }

    return () => {
      ignore = true;
    };
  }, [values.accountId, values.masterProjectId, values.parentProjectId, values.projectClassificationId]);

  return (
    <Box p={4}>
      <Grid container spacing={4}>
        <CreateCreditingPeriod methodologyOptions={methodologyOptions} isUpdate={isUpdate} />
      </Grid>
    </Box>
  );
};

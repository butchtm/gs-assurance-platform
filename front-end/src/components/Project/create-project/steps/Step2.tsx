import { Box, Grid, Typography } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import dayjs from "dayjs";
import { useFormikContext } from "formik";
import { useContext, useEffect, useState } from "react";

import { Options, ReferenceTypeContext, useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { ProbablePii } from "../../../../common/piiAnalysis/PiiAnalysis";
import { CreateProjectForm } from "../../../../common/project/Project";
import { CustomCountrySelect } from "../../../Reusable/FormFields/CustomCountrySelect";
import { CustomFormSelect } from "../../../Reusable/FormFields/CustomFormSelect";
import { CustomMultiAutoComplete } from "../../../Reusable/FormFields/CustomMultiAutoComplete";
import CustomHelperText, { CustomTooltip } from "../../../Reusable/FormFields/CustomTooltipText";
import { getProject } from "../../Project";
import { getWarning, getWarningForDate, getWarningForProjectTypes, warningText } from "../CreateProject";
import { helperTexts } from "../HelperText";

import { ProjectDescription } from "./ProjectDescription";

interface Step2Props {
  probablePii?: ProbablePii[];
}

export const Step2 = ({ probablePii = [] }: Step2Props) => {
  const { values, setFieldValue } = useFormikContext<CreateProjectForm>();
  const referenceDataContext = useReferenceTypes();
  const [scaleOptions, setScaleOptions] = useState<Options[]>([]);
  const { activeReferenceTypes } = useContext(ReferenceTypeContext);

  useEffect(() => {
    if (!values.startDate) {
      setFieldValue("startDate", dayjs.utc().toISOString());
    }
  });

  const filterScaleOptions = (allOptions: Options[], id: number) => {
    return allOptions.filter((option) => {
      return Number(option.value) <= id;
    });
  };

  useEffect(() => {
    const fetchScaleOptions = async () => {
      const allScaleOptions = referenceDataContext.getCustomReferenceOptions("ProjectSize", null);
      const projectId = values.parentProjectId || values.masterProjectId;
      if (projectId === null || projectId === undefined) {
        setScaleOptions(allScaleOptions);
        return;
      }
      const project = await getProject(projectId);
      const sizeId = project?.projectSize?.id;
      if (sizeId === null || sizeId === undefined) {
        setScaleOptions(allScaleOptions);
        return;
      }
      setScaleOptions(filterScaleOptions(allScaleOptions, sizeId));
    };
    fetchScaleOptions();
  }, [values.masterProjectId, values.parentProjectId]);

  const typeOptions = referenceDataContext.getCustomReferenceOptions("ProjectType", values.typeIds);
  const carbonStreamOptions = referenceDataContext.getCustomReferenceOptions("CarbonStream", values.carbonStreamId);
  const registrationTypeOptions = referenceDataContext.getCustomReferenceOptions(
    "RegistrationType",
    values.registrationTypeId,
  );
  const certifiedProductionOptions = referenceDataContext.getCustomReferenceOptions(
    "ProjectAsset",
    values.projectAssetIds,
  );

  let projectScaleHelperText = helperTexts.projectScale();

  const poa = activeReferenceTypes.ProjectClassification.find((x) => x.description === "Programme of Activities");
  const realCaseVpa = activeReferenceTypes.ProjectClassification.find((x) => x.description === "Real Case VPA");
  const regularCaseVpa = activeReferenceTypes.ProjectClassification.find((x) => x.description === "Regular Case VPA");

  if (values.projectClassificationId === poa?.id) {
    projectScaleHelperText = helperTexts.poaTooltip();
  } else if (values.projectClassificationId === realCaseVpa?.id) {
    projectScaleHelperText = helperTexts.realCaseVpaTooltip();
  } else if (values.projectClassificationId === regularCaseVpa?.id) {
    projectScaleHelperText = helperTexts.regularCaseVpaTooltip();
  }

  return (
    <Box p={4}>
      <Grid container spacing={4}>
        <Grid item xs={12}>
          <ProjectDescription probablePii={probablePii} />
        </Grid>

        <Grid item xs={12} md={7}>
          <CustomCountrySelect
            name="locationIds"
            label="Project Location"
            helperText={helperTexts.projectCountry()}
            warningText={getWarning(values.locationIds, values.masterValues?.locationIds)}
          />
        </Grid>
        <Grid item xs={12} md={5}>
          {scaleOptions.length > 0 ? (
            <CustomFormSelect
              name="sizeId"
              label="Project Scale"
              options={scaleOptions}
              helperText={projectScaleHelperText}
              warningText={getWarning(values.sizeId, values.masterValues?.sizeId)}
            />
          ) : (
            <Typography>Loading scale options...</Typography>
          )}
        </Grid>
        <Grid item xs={12} md={12}>
          {typeOptions.length > 0 ? (
            <CustomMultiAutoComplete
              name="typeIds"
              label="Project Type(s)"
              options={typeOptions}
              helperText={helperTexts.projectType()}
              required
              warningText={getWarningForProjectTypes(values.typeIds, values.masterValues?.typeIds)}
            />
          ) : (
            <Typography>Loading type options...</Typography>
          )}
        </Grid>
        <Grid item xs={12} md={6}>
          <Box display="flex" alignItems="center">
            <DatePicker
              label="Project Start Date"
              value={dayjs.utc(values.startDate)}
              onChange={(date) => setFieldValue("startDate", date?.toISOString())}
              format="DD/MM/YYYY"
              defaultValue={dayjs.utc()}
              timezone="UTC"
            />

            {helperTexts.projectStartDate() && (
              <Box ml={2}>
                <CustomHelperText text={helperTexts.projectStartDate()} type={CustomTooltip.HELP} />
              </Box>
            )}
            {values.startDate &&
              values.masterValues?.startDate &&
              getWarningForDate(values.startDate, values.masterValues?.startDate) && (
                <Box ml={1}>
                  <CustomHelperText text={warningText} type={CustomTooltip.WARNING} />
                </Box>
              )}
          </Box>
        </Grid>
        <Grid item xs={12} md={6}>
          {registrationTypeOptions.length > 0 ? (
            <CustomFormSelect
              name="registrationTypeId"
              label="Registration Type"
              options={registrationTypeOptions}
              helperText={helperTexts.registrationType()}
            />
          ) : (
            <Typography>Loading registration types...</Typography>
          )}
        </Grid>
        <Grid item xs={12} md={6}>
          {certifiedProductionOptions.length > 0 ? (
            <CustomMultiAutoComplete
              name="projectAssetIds"
              label="Certified Products"
              options={certifiedProductionOptions}
              helperText={helperTexts.certifiedProduct()}
              warningText={getWarning(values.projectAssetIds, values.masterValues?.projectAssetIds)}
            />
          ) : (
            <Typography>Loading product types...</Typography>
          )}
        </Grid>
        <Grid item xs={12} md={6}>
          {carbonStreamOptions.length > 0 ? (
            <CustomFormSelect
              name="carbonStreamId"
              label="Carbon Stream"
              options={carbonStreamOptions}
              warningText={getWarning(values.carbonStreamId, values.masterValues?.carbonStreamId)}
            />
          ) : (
            <Typography>Loading carbon stream types...</Typography>
          )}
        </Grid>
      </Grid>
    </Box>
  );
};

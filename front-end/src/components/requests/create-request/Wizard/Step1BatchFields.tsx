import { AutocompleteRenderGetTagProps, Chip, Grid } from "@mui/material";
import { useFormikContext } from "formik";
import React, { useEffect, useState } from "react";

import {
  getReferenceValueCodeById,
  Options,
  ReferenceType,
  useReferenceTypes,
} from "../../../../common/data-types/DataReferenceTypes";
import { Project } from "../../../../common/project/Project";
import {
  canRequestTypeCreateMonitoringPeriod,
  canRequestTypeSetFeeSchedule,
} from "../../../../common/request/GSRequest";
import { CustomFormSelect } from "../../../Reusable/FormFields/CustomFormSelect";
import { CustomMultiAutoComplete } from "../../../Reusable/FormFields/CustomMultiAutoComplete";
import { getBatchableProjects } from "../../Requests";

import { FeeSchedule } from "./FeeSchedule";
import { CreateRequestForm } from "./Request";
import { HighRiskProjectChip } from "./risk/HighRiskProjectChip";

interface Step1BatchFieldsProps {
  project: Project;
  batchRequestTypes: ReferenceType[];
  update: boolean;
}

export const Step1BatchFields: React.FC<Step1BatchFieldsProps> = ({ project, batchRequestTypes, update }) => {
  const { values, setFieldValue, setFieldTouched } = useFormikContext<CreateRequestForm>();
  const { allReferenceTypes } = useReferenceTypes();
  const [batchableProjects, setBatchableProjects] = useState<Project[]>([]);
  const [projectOptions, setProjectOptions] = useState<Options[]>([]);

  const currentRequestTypeCode = getReferenceValueCodeById(Number(values.requestType), allReferenceTypes.RequestType);
  const isFeeScheduleRequestType = currentRequestTypeCode
    ? canRequestTypeSetFeeSchedule(currentRequestTypeCode)
    : false;
  const isMonitoringPeriodRequestType =
    currentRequestTypeCode && canRequestTypeCreateMonitoringPeriod(currentRequestTypeCode);

  useEffect(() => {
    const fetchBatchableProjects = async () => {
      if (values.batchRequestTypeId != undefined) {
        const projects = await getBatchableProjects(Number(values.requestType), values.batchRequestTypeId, project.id);
        setBatchableProjects(projects);
      }
    };
    fetchBatchableProjects();
  }, [values.batchRequestTypeId]);

  useEffect(() => {
    const newOptions: Options[] = [
      { value: project.id, label: project.gsid + " - " + project.name },
      ...batchableProjects.map((x) => {
        return { value: x.id, label: x.gsid + " - " + x.name };
      }),
    ];
    setProjectOptions(newOptions);
  }, [batchableProjects]);

  useEffect(() => {
    if (projectOptions.length > 0 && (!values.batchProjectIds || values.batchProjectIds.length === 0)) {
      setFieldValue("batchProjectIds", [project.id]);
      setFieldTouched("batchProjectIds", true, true);
    }
  }, [projectOptions, values.batchRequestTypeId, project.id]);

  const getProjectDetail = (projectId: number) => {
    if (projectId == project.id) {
      return project;
    }
    return batchableProjects.find((x) => x.id == projectId);
  };

  const renderBatchChoiceChips = (options: Options[], getItemProps: AutocompleteRenderGetTagProps) => {
    return options.map((option: Options, index: number) => {
      const { key, ...itemProps } = getItemProps({ index });
      const projectOption = getProjectDetail(Number(option.value));
      if (projectOption?.currentRiskSummary?.risk) {
        return <HighRiskProjectChip key={key} label={option.label} itemProps={itemProps} />;
      }
      return <Chip key={key} label={option.label} {...itemProps} />;
    });
  };

  return (
    <>
      <Grid item xs={12}>
        <CustomFormSelect
          name="batchRequestTypeId"
          label="Batch Request Type"
          options={batchRequestTypes.map((x) => {
            return { value: x.id, label: x.description };
          })}
          disabled={update}
          required
          aria-required
        />
      </Grid>
      {values.batchRequestTypeId !== null && (
        <Grid item xs={12}>
          <CustomMultiAutoComplete
            name="batchProjectIds"
            label="Choose Projects to batch this request with"
            options={projectOptions}
            helperText={
              "If no options are displayed, please ensure you have projects with the appropriate classification for this batch type linked to your account."
            }
            preserveFirstValue={true}
            disabled={update}
            customRenderChips={renderBatchChoiceChips}
            showErrorAfterSubmit
            required
            aria-required
          />
        </Grid>
      )}
      {isFeeScheduleRequestType && isMonitoringPeriodRequestType && (
        <Grid item xs={12}>
          <FeeSchedule batch={true} />
        </Grid>
      )}
    </>
  );
};

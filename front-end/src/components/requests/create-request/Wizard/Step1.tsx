import { Autocomplete, Box, FormControl, Grid, TextField } from "@mui/material";
import { useFormikContext } from "formik";
import React, { useContext, useEffect, useState } from "react";

import { ReferenceType, useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { Project } from "../../../../common/project/Project";
import { AuthContext } from "../../../Auth/Login/AuthProvider";
import { helperTexts } from "../../../Project/create-project/HelperText";
import { CustomFormSelect } from "../../../Reusable/FormFields/CustomFormSelect";
import CustomHelperText, { CustomTooltip } from "../../../Reusable/FormFields/CustomTooltipText";
import { getBatchedRequestTypeMappings, getRequiredDocumentsForRequest } from "../../Requests";

import { BatchRequestIdWithProjects, CreateRequestForm } from "./Request";
import { Step1BatchChoice } from "./Step1BatchChoice";
import { Step1BatchFields } from "./Step1BatchFields";
import { Step1Fields } from "./Step1Fields";

interface Step1Props {
  project: Project;
  setIsBatched?: (isBatched: boolean) => void;
  setBatchedRequest?: (batchedProjects: BatchRequestIdWithProjects) => void;
  setRequestTypeId?: (requestTypeId: number) => void;
  requestEditId?: number;
}

export const Step1: React.FC<Step1Props> = ({
  project,
  setIsBatched,
  setBatchedRequest,
  setRequestTypeId,
  requestEditId,
}) => {
  const { values, setFieldValue, touched, errors, resetForm } = useFormikContext<CreateRequestForm>();
  const { activeRequestTypes } = useReferenceTypes();

  const [requestType, setRequestType] = useState<number | null>(null);
  const [batchRequestTypes, setBatchRequestTypes] = useState<ReferenceType[]>([]);

  useEffect(() => {
    const getRequiredDocuments = async () => {
      const requestTypeId = Number(values.requestType);
      if (values.requestType && !isNaN(requestTypeId)) {
        const requiredDocumentsResponse = await getRequiredDocumentsForRequest(requestTypeId);
        setFieldValue("requiredDocuments", requiredDocumentsResponse.REQUIRED);
      } else {
        setFieldValue("requiredDocuments", []);
      }
    };

    const getBatchRequestTypes = async () => {
      if (requestType || values.requestType) {
        const requestTypeValue = requestType ?? values.requestType;

        const batchRequestTypesResponse = await getBatchedRequestTypeMappings(Number(requestTypeValue), project.id);
        setBatchRequestTypes(batchRequestTypesResponse);
        if (!batchRequestTypesResponse || batchRequestTypesResponse.length === 0) {
          setFieldValue("createBatchRequest", "no");
        }
      }
    };

    getBatchRequestTypes();
    getRequiredDocuments();
    if (setRequestTypeId) {
      setRequestTypeId(Number(values.requestType));
    }
  }, [values.requestType]);

  useEffect(() => {
    if (setIsBatched) {
      setIsBatched(values.createBatchRequest === "yes");
    }
  }, [values.createBatchRequest]);

  useEffect(() => {
    if (values.creditingPeriodId === null) {
      setFieldValue("creditingPeriodId", null);
      setFieldValue("startDate", null);
      setFieldValue("endDate", null);
    }
  }, [values.creditingPeriodId, setFieldValue]);

  useEffect(() => {
    if (values.createBatchRequest === "no") {
      setFieldValue("batchRequestTypeId", null);
      setFieldValue("batchProjectIds", null);
    }
  }, [values.createBatchRequest]);

  useEffect(() => {
    if (values.batchProjectIds && setBatchedRequest) {
      setBatchedRequest({
        requestTypeId: Number(values.requestType),
        creditingPeriodId: Number(values.creditingPeriodId),
        emissionReductionAmount: Number(values.emissionReductionAmount),
        batchRequestTypeId: Number(values.batchRequestTypeId),
        projects: values.batchProjectIds,
      });
    }
  }, [values.batchProjectIds]);

  const onRequestTypeUpdate = (selectedRequestType: ReferenceType | null) => {
    if (values.requestType !== null) {
      resetForm();
    }
    setRequestType(selectedRequestType ? selectedRequestType.id : null);
    setFieldValue("requestType", selectedRequestType ? selectedRequestType.id.toString() : "");
  };

  const authContext = useContext(AuthContext);
  const isDeveloper = authContext.isProjectDeveloperUser();

  const filteredRequestTypes = isDeveloper
    ? activeRequestTypes.filter((type) => type.code !== "ISSUANCE")
    : activeRequestTypes;

  const gridItemProps = requestEditId ? { xs: 11 } : { xs: 12 };

  const autocompleteValue = activeRequestTypes.find(
    (requestTypeOption) => requestTypeOption.id == Number(values.requestType),
  );

  return (
    <Box sx={{ display: "flex", flexDirection: "column", alignItems: "center", margin: "0 10%", p: 2 }}>
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <FormControl fullWidth variant="outlined">
                <Grid container alignItems="center">
                  <Grid item {...gridItemProps}>
                    <Autocomplete
                      options={filteredRequestTypes}
                      getOptionLabel={(option) => option.description}
                      value={autocompleteValue}
                      onChange={(_, selectedRequestType) => {
                        onRequestTypeUpdate(selectedRequestType);
                      }}
                      renderInput={(params) => (
                        <TextField
                          {...params}
                          label={
                            <>
                              Request Type<span aria-hidden="true">*</span>
                            </>
                          }
                          variant="outlined"
                          data-testid="request-type-select"
                          error={Boolean(touched.requestType && errors.requestType)}
                          helperText={touched.requestType && errors.requestType}
                        />
                      )}
                      disabled={requestEditId !== undefined}
                    />
                  </Grid>
                  {requestEditId !== undefined && (
                    <Grid item xs={1}>
                      <Box ml={2}>
                        <CustomHelperText text={helperTexts.requestType()} type={CustomTooltip.HELP} />
                      </Box>
                    </Grid>
                  )}
                </Grid>
              </FormControl>
            </Grid>
            {autocompleteValue?.supportsPAA && (
              <Grid item xs={12}>
                <CustomFormSelect
                  name="paa"
                  label="Is a PAA request?"
                  options={[
                    { value: "true", label: "Yes", flag: true },
                    { value: "false", label: "No", flag: false },
                  ]}
                  required
                />
              </Grid>
            )}
            <Grid item xs={12}>
              {(!requestEditId || values.createBatchRequest == "yes") && (
                <Step1BatchChoice batchRequestTypes={batchRequestTypes} update={!!requestEditId}></Step1BatchChoice>
              )}
            </Grid>
            {values.createBatchRequest === "yes" && (
              <Step1BatchFields
                project={project}
                batchRequestTypes={batchRequestTypes}
                update={requestEditId !== undefined}
              ></Step1BatchFields>
            )}
            {values.createBatchRequest === "no" && <Step1Fields project={project}></Step1Fields>}
          </Grid>
        </Grid>
      </Grid>
    </Box>
  );
};

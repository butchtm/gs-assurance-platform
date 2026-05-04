import ClearIcon from "@mui/icons-material/Clear";
import { Box, Button, Grid, IconButton, Typography } from "@mui/material";
import { FieldArray, useFormikContext } from "formik";
import React from "react";

import { Options } from "../../../../common/data-types/DataReferenceTypes";
import { CreateMethodology, CreateProjectForm } from "../../../../common/project/Project";
import CustomTooltipText, { CustomTooltip } from "../../../Reusable/FormFields/CustomTooltipText";
import { helperTexts } from "../HelperText";

import { CreateMethodologySelect } from "./CreateMethodologySelect";
import { CreateMethodologyVersion } from "./CreateMethodologyVersion";

interface CreateMethodologiesProps {
  currentMethodologies: CreateMethodology[];
  creditingPeriodIndex: number;
  methodologyOptions: Options[];
}

const CreateMethodologies: React.FC<CreateMethodologiesProps> = ({
  currentMethodologies,
  creditingPeriodIndex,
  methodologyOptions,
}) => {
  const { values } = useFormikContext<CreateProjectForm>();
  const isAddDisabled = () => {
    const methodologies: CreateMethodology[] = values.creditingPeriods[creditingPeriodIndex].methodologies;
    if (methodologies.length === 0) return false;
    const lastMethodologyId = methodologies[methodologies.length - 1].id;
    return lastMethodologyId === undefined || lastMethodologyId === null || lastMethodologyId === -1;
  };

  return (
    <Grid container item xs={12} sx={{ ml: 3 }}>
      <Grid item xs={12} sx={{ mt: 2, mb: 2 }}>
        <Box display="flex" alignItems="center">
          <Typography variant="h6" display="flex">
            Methodologies
          </Typography>
          <Box ml={2}>
            <CustomTooltipText text={helperTexts.methodologyVersionNumber()} type={CustomTooltip.HELP} />
          </Box>
        </Box>
      </Grid>
      <FieldArray name={`creditingPeriods[${creditingPeriodIndex}].methodologies`}>
        {({ push, remove }) => {
          return (
            <Grid container item xs={12} spacing={2} alignItems="top">
              <Grid item xs>
                {currentMethodologies.map((methodology: CreateMethodology, methodologyIndex: number) => (
                  <Grid
                    container
                    item
                    xs={12}
                    spacing={2}
                    alignItems="top"
                    key={"methodology-" + methodology.id}
                    sx={{ mb: 2 }}
                  >
                    <Grid item xs={6} sx={{ ml: 3 }}>
                      <CreateMethodologySelect
                        currentMethodologies={currentMethodologies}
                        creditingPeriodIndex={creditingPeriodIndex}
                        methodologyOptions={methodologyOptions}
                        methodologyIndex={methodologyIndex}
                      />
                    </Grid>
                    <Grid item xs={4}>
                      <CreateMethodologyVersion
                        creditingPeriodIndex={creditingPeriodIndex}
                        methodologyIndex={methodologyIndex}
                      />
                    </Grid>
                    <Grid item xs={1} alignContent={"center"}>
                      <IconButton
                        aria-label="Remove Methodology"
                        onClick={() => remove(methodologyIndex)}
                        data-testid={`remove-methodology-${methodology.id}`}
                      >
                        <ClearIcon color="error" />
                      </IconButton>
                    </Grid>
                  </Grid>
                ))}
                {currentMethodologies.length === 0 && (
                  <Grid item margin={"auto"} color="error.main">
                    <Typography variant="body2" textAlign={"center"}>
                      Please add a methodology
                    </Typography>
                  </Grid>
                )}
                <Grid item xs={12}>
                  <Button
                    variant="outlined"
                    disabled={isAddDisabled()}
                    onClick={() =>
                      push({
                        id: null,
                        version: undefined,
                      })
                    }
                  >
                    Add Methodology
                  </Button>
                </Grid>
              </Grid>
            </Grid>
          );
        }}
      </FieldArray>
    </Grid>
  );
};

export default CreateMethodologies;

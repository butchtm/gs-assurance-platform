import {
  Autocomplete,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  TextField,
} from "@mui/material";
import React, { useContext, useEffect, useState } from "react";
import { toast } from "react-toastify";

import { AccountTypeIds } from "../../../AccountType";
import { Options, ReferenceTypeContext } from "../../../common/data-types/DataReferenceTypes";
import ConfirmCloseDialog from "../../../common/dialog/ConfirmCloseDialog";
import { Project } from "../../../common/project/Project";
import { getAllProjects } from "../../Project/Project";
import { createAccountCompetencies, getAccountCompetencies, updateAccountCompetencies } from "../PlatformAccount";
import {
  getRefDataNameForCompetencyKey,
  PlatformAccount,
  UpdateCreateCompetenciesPayload,
} from "../PlatformAccountTypes";

export const EditAccountCompetenciesModal: React.FC<{
  isOpen: boolean;
  onClose: () => void;
  account: PlatformAccount;
  accounts: PlatformAccount[];
}> = ({ isOpen, onClose, account, accounts }) => {
  const referenceDataContext = useContext(ReferenceTypeContext);
  const [openConfirmDialog, setOpenConfirmDialog] = useState(false);
  const [pdAccounts, setPdAccounts] = useState<PlatformAccount[]>([]);
  const [vvbAccounts, setVvbAccounts] = useState<PlatformAccount[]>([]);
  const [projects, setProjects] = useState<Project[]>([]);
  const [hasCompetencies, setHasCompetencies] = useState<boolean>(false);
  const [competencyConfig, setCompetencyConfig] = useState<UpdateCreateCompetenciesPayload>({
    estimatedAnnualEmissionReductionAmount: 0,
    projectGsids: [],
    projectTypeIds: [],
    projectLocationIds: [],
    projectScaleIds: [],
    methodologyIds: [],
    requestTypeIds: [],
    certifiedProductIds: [],
    vvbAssignedIds: [],
    projectDeveloperIds: [],
    sustainableDevelopmentGoalIds: [],
  });

  const handleCloseClick = () => {
    setOpenConfirmDialog(true);
  };

  const handleConfirmClose = () => {
    setOpenConfirmDialog(false);
    onClose();
  };

  const handleCancelClose = () => {
    setOpenConfirmDialog(false);
  };

  const handleSubmit = async () => {
    try {
      await handleCompetencySubmission(competencyConfig, account.id);
      onClose();
    } catch (error) {
      console.error("Error submitting account form:", error);
      toast.error("Failed to submit competencies. Please try again.");
    }
  };

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        setProjects(await getAllProjects());
      } catch (error) {
        console.error("Error fetching projects:", error);
      }
    };

    fetchProjects();
  }, []);

  useEffect(() => {
    const setAccounts = () => {
      if (accounts) {
        setPdAccounts(accounts.filter((x) => x.accountType.code === "PROJECT-DEVELOPER"));
        setVvbAccounts(accounts.filter((x) => x.accountType.code === "VVB"));
      }
    };

    setAccounts();
  }, [accounts]);

  useEffect(() => {
    const fetchCompetencies = async () => {
      if (account.id) {
        const fetchedCompetencies = await getAccountCompetencies(account.id);
        if (fetchedCompetencies) {
          setHasCompetencies(true);
          setCompetencyConfig(fetchedCompetencies);
        }
      }
    };

    fetchCompetencies();
  }, [account]);

  const handleCompetencySubmission = async (
    competenciesPayload: UpdateCreateCompetenciesPayload,
    accountId: number,
  ) => {
    if (hasCompetencies) {
      await updateAccountCompetencies(competenciesPayload, accountId);
      toast.success("Competencies Updated Successfully!");
    } else {
      await createAccountCompetencies(competenciesPayload, accountId);
      toast.success("Competencies Created Successfully!");
    }
  };

  const buildRefDataCompetencyOptions = (): JSX.Element[] => {
    const competencyTypesToDisplay: (keyof UpdateCreateCompetenciesPayload)[] = [
      "projectTypeIds",
      "projectLocationIds",
      "projectScaleIds",
      "methodologyIds",
      "requestTypeIds",
      "certifiedProductIds",
      "sustainableDevelopmentGoalIds",
    ];

    const getRefDataCurrentValue = (competencyType: keyof UpdateCreateCompetenciesPayload) => {
      const currentValue = competencyConfig[competencyType];

      const isArrayOfNumbers =
        Array.isArray(currentValue) && Array.from(currentValue as unknown[]).every((item) => typeof item == "number");
      if (typeof currentValue !== "number" && !isArrayOfNumbers) {
        if (!Array.isArray(currentValue)) {
          throw Error(
            `Invalid competency property (${competencyType}) type used in RefData builder, expected number or number[] but was ${typeof currentValue}`,
          );
        }
        throw Error(
          `Invalid competency property (${competencyType}) type used in RefData builder, expected number or number[] but was ${typeof Array.from(
            currentValue as unknown[],
          )[0]}`,
        );
      }
      return currentValue as number | number[];
    };

    return competencyTypesToDisplay.map((competencyType) =>
      buildRefDataAutocomplete(
        competencyType,
        referenceDataContext.getCustomReferenceOptions(
          getRefDataNameForCompetencyKey(competencyType),
          getRefDataCurrentValue(competencyType),
        ),
      ),
    );
  };

  const buildRefDataAutocomplete = (competencyType: keyof UpdateCreateCompetenciesPayload, options: Options[]) => {
    const formatType = (competencyType: keyof UpdateCreateCompetenciesPayload): string => {
      const splitCamelCaseWithSpaces = competencyType.replace(/([A-Z])/g, " $1");
      const capitalisedFirstLetter =
        splitCamelCaseWithSpaces.charAt(0).toUpperCase() + splitCamelCaseWithSpaces.slice(1);
      if (capitalisedFirstLetter.toLowerCase().endsWith(" ids")) {
        return capitalisedFirstLetter.slice(0, -4);
      }
      return capitalisedFirstLetter;
    };

    return (
      <Autocomplete
        key={competencyType}
        multiple
        options={options}
        getOptionLabel={(option) => option.label}
        value={options.filter((option) => {
          const idValues = competencyConfig[competencyType] as number[];
          return idValues?.includes(Number(option.value as number));
        })}
        onChange={(_, values) => {
          const newValues = values.map((v) => v.value);
          setCompetencyConfig((prevState) => ({
            ...prevState,
            [competencyType]: newValues,
          }));
        }}
        renderInput={(params) => (
          <TextField
            {...params}
            label={formatType(competencyType)}
            margin="normal"
            data-testid="competency-type-select"
          />
        )}
      />
    );
  };

  return (
    <>
      <Dialog maxWidth="lg" open={isOpen} onClose={handleCloseClick}>
        <DialogTitle>Edit Competencies</DialogTitle>
        <DialogContent sx={{ minWidth: "875px" }}>
          <Grid container spacing={3}>
            <Grid item xs={12} md={6}>
              {buildRefDataCompetencyOptions()}
            </Grid>
            <Grid item xs={12} md={6}>
              <Autocomplete
                multiple
                options={pdAccounts}
                getOptionLabel={(option) => option.name}
                value={pdAccounts.filter((x) => competencyConfig.projectDeveloperIds?.includes(x.id))}
                onChange={(_, values) => {
                  setCompetencyConfig((prevState) => ({
                    ...prevState,
                    projectDeveloperIds: values.map((x) => x.id),
                  }));
                }}
                renderInput={(params) => (
                  <TextField {...params} label="Project Developers" margin="normal" data-testid="pd-accounts-select" />
                )}
              />
              {account.accountType.id !== AccountTypeIds.TAC && (
                <Autocomplete
                  multiple
                  options={vvbAccounts}
                  getOptionLabel={(option) => option.name}
                  value={vvbAccounts.filter((x) => competencyConfig.vvbAssignedIds?.includes(x.id))}
                  onChange={(_, values) => {
                    setCompetencyConfig((prevState) => ({
                      ...prevState,
                      vvbAssignedIds: values.map((x) => x.id),
                    }));
                  }}
                  renderInput={(params) => (
                    <TextField {...params} label="Assigned VVBs" margin="normal" data-testid="vvb-account-select" />
                  )}
                />
              )}
              {account.accountType.id !== AccountTypeIds.TAC && (
                <Autocomplete
                  multiple
                  options={projects.map((x) => x.gsid)}
                  value={projects.filter((x) => competencyConfig.projectGsids?.includes(x.gsid)).map((x) => x.gsid)}
                  onChange={(_, values) => {
                    setCompetencyConfig((prevState) => ({
                      ...prevState,
                      projectGsids: values,
                    }));
                  }}
                  renderInput={(params) => (
                    <TextField {...params} label="Project GSIDs" margin="normal" data-testid="gsid-select" />
                  )}
                />
              )}
              <TextField
                label="Emission Range (>=)"
                value={competencyConfig.estimatedAnnualEmissionReductionAmount}
                onChange={(e) => {
                  setCompetencyConfig((prevState) => ({
                    ...prevState,
                    estimatedAnnualEmissionReductionAmount: Number(e.target.value),
                  }));
                }}
                fullWidth
                margin="normal"
                data-testid="emission-reduction-input"
              />
            </Grid>
          </Grid>
        </DialogContent>

        <DialogActions>
          <Button onClick={onClose}>Cancel</Button>
          <Button onClick={handleSubmit} variant="contained">
            Save
          </Button>
        </DialogActions>
      </Dialog>
      <ConfirmCloseDialog isOpen={openConfirmDialog} onConfirm={handleConfirmClose} onCancel={handleCancelClose} />
    </>
  );
};

import { Box, Button, Grid, InputLabel, Typography } from "@mui/material";
import dayjs from "dayjs";
import { Field, useFormikContext } from "formik";
import { TextField } from "formik-mui";
import { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { AccountTypeIds } from "../../../../AccountType";
import {
  getReferenceValueCodeById,
  Options,
  useReferenceTypes,
} from "../../../../common/data-types/DataReferenceTypes";
import { ProbablePii } from "../../../../common/piiAnalysis/PiiAnalysis";
import { CreateProjectForm, Project, ReferenceType } from "../../../../common/project/Project";
import { PermissionGroup } from "../../../../PermissionGroup";
import ProtectedComponent from "../../../../ProtectedComponent";
import { AuthContext } from "../../../Auth/Login/AuthProvider";
import { Account } from "../../../PlatformAccounts/PlatformAccountTypes";
import { CustomAccountSearchAutoComplete } from "../../../Reusable/FormFields/CustomAccountSearch";
import { CustomAutoComplete } from "../../../Reusable/FormFields/CustomAutoComplete";
import { getCandidatePOAs, getCandidateRealCaseVPAs, getProject } from "../../Project";
import { helperTexts } from "../HelperText";

import { ProjectTitle } from "./ProjectTitle";

interface Step1Props {
  projectId?: number;
  isUpdate?: boolean;
  probablePii?: ProbablePii[];
}

export const Step1 = ({ projectId, isUpdate, probablePii = [] }: Step1Props) => {
  const navigate = useNavigate();
  const [accountName, setAccountName] = useState<string>("");
  const [candidatePOAs, setCandidatePOAs] = useState<Project[]>([]);
  const [candidateRealCaseVPAs, setCandidateRealCaseVPAs] = useState<Project[]>([]);
  const { values, setFieldValue } = useFormikContext<CreateProjectForm>();
  const [pdAccountId, setPdAccountId] = useState<number | null>(values.accountId ?? null);
  const authContext = useContext(AuthContext);
  const { getCustomReferenceOptions, allReferenceTypes, activeReferenceTypes } = useReferenceTypes();

  const classificationId = values?.projectClassificationId ?? null;
  const classificationOptions = getCustomReferenceOptions("ProjectClassification", classificationId);

  const [selectedMasterProjectId, setSelectedMasterProjectId] = useState<number | null>(null);

  const projectClassificationCode = getReferenceValueCodeById(
    values.projectClassificationId,
    allReferenceTypes?.ProjectClassification,
  );

  const fetchCandidateRealCases = async (masterProjectId: number | null) => {
    if (pdAccountId != null && masterProjectId) {
      setCandidateRealCaseVPAs(await getCandidateRealCaseVPAs(pdAccountId, masterProjectId));
    }
  };

  const handleMasterProjectChange = (masterProjectId: number | null) => {
    setSelectedMasterProjectId(masterProjectId);

    if (projectClassificationCode === "REAL_CASE_VPA" || projectClassificationCode === "VPA_CPA") {
      setFieldValue("parentProjectId", masterProjectId);
    }
    fetchCandidateRealCases(masterProjectId);
  };

  useEffect(() => {
    const fetchCandidatePOAs = async () => {
      if (pdAccountId != null && classificationId) {
        setCandidatePOAs(await getCandidatePOAs(pdAccountId, classificationId));
      }
    };

    Promise.all([fetchCandidatePOAs(), fetchCandidateRealCases(values.masterProjectId)]);
  }, [pdAccountId, classificationId]);

  useEffect(() => {
    if (selectedMasterProjectId !== null) {
      updateMasterProjectInformation(selectedMasterProjectId);
    }
  }, [selectedMasterProjectId]);

  const updateMasterProjectInformation = async (masterProjectId: number) => {
    try {
      const masterProject = await getProject(masterProjectId);
      if (masterProject) {
        populateFormFields(masterProject);
      }
    } catch (error) {
      console.error("Error updating VPA with POA information:", error);
    }
  };

  const populateFormFields = (masterProject: Project) => {
    const masterLocationIds = masterProject.projectLocations?.map((location: ReferenceType) => location.id);
    const masterSDGs = masterProject.sustainableDevelopmentGoals?.map((goal: ReferenceType) => goal.id);
    const masterAssetIds = masterProject.projectAssets?.map((asset: ReferenceType) => asset.id);
    const masterProjectTypeIds = masterProject.projectTypes?.map((type: ReferenceType) => type.id);

    setFieldValue("locationIds", masterLocationIds);
    setFieldValue("carbonStreamId", masterProject.carbonStream?.id);
    setFieldValue("sustainableDevelopmentGoalIds", masterSDGs);
    setFieldValue("sizeId", masterProject.projectSize?.id);
    setFieldValue("typeIds", masterProjectTypeIds);
    setFieldValue("startDate", dayjs.utc(masterProject.projectStartDate));
    setFieldValue("projectAssetIds", masterAssetIds);

    setFieldValue("masterValues", {
      locationIds: masterLocationIds,
      carbonStreamId: masterProject.carbonStream?.id,
      sustainableDevelopmentGoalIds: masterSDGs,
      sizeId: masterProject.projectSize?.id,
      startDate: dayjs.utc(masterProject.projectStartDate).toISOString(),
      projectAssetIds: masterAssetIds,
      typeIds: masterProjectTypeIds,
    });
  };

  useEffect(() => {
    if (authContext.isProjectDeveloperUser() && authContext.user?.account) {
      populateAccountData(authContext.user.account);
    }
  }, []);

  const populateAccountData = (account: Account) => {
    setFieldValue("accountName", account.name);
    setPdAccountId(account.id);
    setAccountName(account.name);
  };

  const handleAccountChange = (account: Options | null) => {
    if (account) {
      setPdAccountId(Number(account.value));
      setAccountName(account.label);
      setFieldValue("accountName", account.label);
    }
  };

  const handleUpdateProjectStatus = () => {
    navigate("/update-project-status", {
      state: {
        status: activeReferenceTypes.ProjectState.find((x) => x.id === values.projectStatusId),
        projectId: projectId,
      },
    });
  };

  const toOptions = (projects: Project[]) => {
    return projects.map(({ id, name, gsid }) => ({ value: id, label: `${name} (${gsid})` }));
  };

  const shouldDisplayPOAInput = () => {
    return (
      projectClassificationCode === "REGULAR_VPA" ||
      projectClassificationCode === "REAL_CASE_VPA" ||
      projectClassificationCode === "VPA_CPA"
    );
  };

  const shouldDisplayRealCaseInput = () => {
    return projectClassificationCode === "REGULAR_VPA";
  };

  const canSelectDeveloperAccount = () => {
    return authContext.isAdminUser() || authContext.isArmUser();
  };

  const getParentPoa = (): Project | undefined => {
    return candidatePOAs?.find((poa) => poa.id == selectedMasterProjectId);
  };

  const getParentRealCase = (): Project | undefined => {
    if (!shouldDisplayRealCaseInput()) {
      return;
    }
    return candidateRealCaseVPAs?.find((vpa) => vpa.id == values.parentProjectId);
  };

  return (
    <Box p={4}>
      <Grid container spacing={4}>
        <Grid item xs={12} md={6}>
          {canSelectDeveloperAccount() && (
            <CustomAccountSearchAutoComplete
              name="accountId"
              label="Project Developer Account"
              onChange={handleAccountChange}
              accountTypeId={AccountTypeIds.PROJECT_DEVELOPER}
              required
            />
          )}
          {!canSelectDeveloperAccount() && (
            <Field
              fullWidth
              label="Developer Account"
              name="projectDeveloper"
              value={accountName}
              component={TextField}
              InputProps={{
                readOnly: true,
                "data-testid": "project-developer-account",
              }}
            />
          )}
        </Grid>
        <Grid item xs={12} md={6}>
          <CustomAutoComplete
            name="projectClassificationId"
            label="Project Classification"
            options={classificationOptions}
            onChange={(projectClassificationId) => setFieldValue("projectClassificationId", projectClassificationId)}
            helperText={helperTexts.projectClassification()}
            required
          />
        </Grid>
        {shouldDisplayPOAInput() && (
          <Grid item xs={12} md={6}>
            <CustomAutoComplete
              name="masterProjectId"
              label="Programme of Activities"
              options={toOptions(candidatePOAs)}
              onChange={handleMasterProjectChange}
              helperText={helperTexts.programmeOfActivities()}
              required
            />
          </Grid>
        )}
        {shouldDisplayRealCaseInput() && (
          <Grid item xs={12} md={6}>
            <CustomAutoComplete
              name="parentProjectId"
              label="Real Case VPA"
              options={toOptions(candidateRealCaseVPAs)}
              helperText={helperTexts.realCaseVPA()}
              required
            />
          </Grid>
        )}
        <Grid item xs={12} md={6}>
          <ProjectTitle
            probablePii={probablePii}
            parentPoa={getParentPoa()}
            parentRealCase={getParentRealCase()}
            isUpdate={isUpdate}
          />
        </Grid>
        <Grid item xs={12} md={6} display="flex" justifyContent="space-between">
          <div>
            <InputLabel id="status-label" style={{ fontSize: "12px", top: "-8px", textAlign: "left" }}>
              Project Status
            </InputLabel>
            <Typography variant="body1" component="div">
              {activeReferenceTypes.ProjectState.find((x) => x.id === values.projectStatusId)?.description}
            </Typography>
          </div>
          {isUpdate && (
            <ProtectedComponent roles={[PermissionGroup.GS_ADMIN]}>
              <Box className="wizard-buttons" style={{ margin: "auto 0" }}>
                <Button onClick={handleUpdateProjectStatus} data-testid="project-status-update-btn">
                  Update
                </Button>
              </Box>
            </ProtectedComponent>
          )}
        </Grid>
      </Grid>
    </Box>
  );
};

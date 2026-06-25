import { Box, Button, Grid, InputLabel, Typography } from "@mui/material";
import dayjs from "dayjs";
import { Field, useFormikContext } from "formik";
import { TextField } from "formik-mui";
import { useContext, useEffect, useRef, useState } from "react";
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
import {
  CandidateParentProjectDetails,
  getCandidatePOAs,
  getCandidateParentDetails,
  getCandidateRealCaseVPAs,
  getInitialEmptyValues,
} from "../../Project";
import {
  canonicalProjectClassificationCode,
  projectClassificationCodeRequiresPoa,
  projectClassificationCodeRequiresRealCaseVpa,
} from "../../ProjectClassificationSemantics";
import { helperTexts } from "../HelperText";

import { ProjectTitle } from "./ProjectTitle";

interface Step1Props {
  projectId?: number;
  isUpdate?: boolean;
  probablePii?: ProbablePii[];
  canEditDeveloperAccount?: boolean;
  canEditProjectClassification?: boolean;
  canEditProjectTitle?: boolean;
  canEditParentHierarchy?: boolean;
}

type ProjectOption = Pick<Project, "id" | "gsid" | "name">;

export const Step1 = ({
  projectId,
  isUpdate,
  probablePii = [],
  canEditDeveloperAccount = true,
  canEditProjectClassification = true,
  canEditProjectTitle = true,
  canEditParentHierarchy = true,
}: Step1Props) => {
  const navigate = useNavigate();
  const [accountName, setAccountName] = useState<string>("");
  const [candidatePOAs, setCandidatePOAs] = useState<ProjectOption[]>([]);
  const [candidateRealCaseVPAs, setCandidateRealCaseVPAs] = useState<ProjectOption[]>([]);
  const { values, setFieldValue } = useFormikContext<CreateProjectForm>();
  const [pdAccountId, setPdAccountId] = useState<number | null>(values.accountId ?? null);
  const candidateRealCasesRequestId = useRef(0);
  const authContext = useContext(AuthContext);
  const { getCustomReferenceOptions, allReferenceTypes, activeReferenceTypes } = useReferenceTypes();

  const classificationId = values?.projectClassificationId ?? null;
  const projectClassifications = allReferenceTypes?.ProjectClassification ?? [];
  const rawProjectClassificationCode = getReferenceValueCodeById(classificationId, projectClassifications);
  const projectClassificationCode = canonicalProjectClassificationCode(rawProjectClassificationCode);
  const classificationOptions = getCustomReferenceOptions("ProjectClassification", classificationId);

  const shouldDisplayPOAInput = projectClassificationCodeRequiresPoa(projectClassificationCode);
  const shouldDisplayRealCaseInput = projectClassificationCodeRequiresRealCaseVpa(projectClassificationCode);
  const masterProjectDetailsRequestId = useRef(0);

  const fetchCandidateParentDetail = (
    candidateProjectId: number,
    masterProjectId?: number | null,
  ): Promise<CandidateParentProjectDetails | null> => {
    if (pdAccountId == null || classificationId == null) {
      return Promise.resolve(null);
    }
    return getCandidateParentDetails({
      accountId: pdAccountId,
      classificationId,
      candidateProjectId,
      masterProjectId,
    });
  };

  const includeSelectedProject = async (
    projects: ProjectOption[],
    projectId: number | null | undefined,
    masterProjectId?: number | null,
  ) => {
    if (
      !isUpdate ||
      projectId === null ||
      projectId === undefined ||
      projects.some((project) => project.id === projectId)
    ) {
      return projects;
    }
    const project = await fetchCandidateParentDetail(projectId, masterProjectId);
    return project ? [...projects, project] : projects;
  };

  const clearInheritedMasterValues = () => {
    setFieldValue("locationIds", []);
    setFieldValue("carbonStreamId", null);
    setFieldValue("sustainableDevelopmentGoalIds", []);
    setFieldValue("sizeId", null);
    setFieldValue("typeIds", []);
    setFieldValue("startDate", null);
    setFieldValue("projectAssetIds", []);
    setFieldValue("masterValues", undefined);
  };

  const resetCreditingPeriods = () => {
    setFieldValue("creditingPeriods", getInitialEmptyValues().creditingPeriods);
  };

  const clearParentProjectState = () => {
    candidateRealCasesRequestId.current += 1;
    masterProjectDetailsRequestId.current += 1;
    setCandidatePOAs([]);
    setCandidateRealCaseVPAs([]);
    setFieldValue("masterProjectId", null);
    setFieldValue("parentProjectId", null);
    resetCreditingPeriods();
    clearInheritedMasterValues();
  };

  const fetchCandidateRealCases = async (
    masterProjectId: number | null,
    selectedParentProjectId: number | null | undefined = values.parentProjectId,
  ) => {
    const requestId = candidateRealCasesRequestId.current + 1;
    candidateRealCasesRequestId.current = requestId;

    if (pdAccountId == null || !masterProjectId) {
      if (requestId === candidateRealCasesRequestId.current) {
        setCandidateRealCaseVPAs([]);
      }
      return;
    }

    const realCaseVPAs = await getCandidateRealCaseVPAs(pdAccountId, masterProjectId, classificationId);
    const realCaseVPAsWithSelected = await includeSelectedProject(
      realCaseVPAs,
      selectedParentProjectId,
      masterProjectId,
    );
    if (requestId === candidateRealCasesRequestId.current) {
      setCandidateRealCaseVPAs(realCaseVPAsWithSelected);
    }
  };

  const handleMasterProjectChange = async (masterProjectId: number | null) => {
    const detailsRequestId = masterProjectDetailsRequestId.current + 1;
    masterProjectDetailsRequestId.current = detailsRequestId;
    resetCreditingPeriods();

    if (projectClassificationCode === "REAL_CASE_VPA" || projectClassificationCode === "VPA_CPA") {
      setFieldValue("parentProjectId", masterProjectId);
      candidateRealCasesRequestId.current += 1;
      setCandidateRealCaseVPAs([]);
    } else if (projectClassificationCodeRequiresRealCaseVpa(projectClassificationCode)) {
      setFieldValue("parentProjectId", null);
      setCandidateRealCaseVPAs([]);
      fetchCandidateRealCases(masterProjectId, null);
    } else {
      setFieldValue("parentProjectId", null);
      candidateRealCasesRequestId.current += 1;
      setCandidateRealCaseVPAs([]);
    }

    if (masterProjectId === null) {
      return;
    }

    try {
      const masterProject = await fetchCandidateParentDetail(masterProjectId);
      if (detailsRequestId === masterProjectDetailsRequestId.current && masterProject) {
        populateFormFields(masterProject);
      }
    } catch (error) {
      console.error("Error updating VPA with POA information:", error);
    }
  };

  const handleParentProjectChange = (parentProjectId: number | null) => {
    setFieldValue("parentProjectId", parentProjectId);
    resetCreditingPeriods();
  };

  useEffect(() => {
    let ignore = false;

    const fetchCandidatePOAs = async () => {
      if (pdAccountId == null || !classificationId || !shouldDisplayPOAInput) {
        if (!ignore) {
          setCandidatePOAs([]);
        }
        return;
      }

      const poas = await getCandidatePOAs(pdAccountId, classificationId);
      const poasWithSelected = await includeSelectedProject(poas, values.masterProjectId);
      if (!ignore) {
        setCandidatePOAs(poasWithSelected);
      }
    };

    Promise.all([fetchCandidatePOAs(), fetchCandidateRealCases(values.masterProjectId)]);

    return () => {
      ignore = true;
      candidateRealCasesRequestId.current += 1;
    };
  }, [pdAccountId, classificationId, shouldDisplayPOAInput]);

  const populateFormFields = (masterProject: CandidateParentProjectDetails) => {
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
    setFieldValue("accountId", account.id);
    setFieldValue("accountName", account.name);
    setPdAccountId(account.id);
    setAccountName(account.name);
  };

  const handleAccountChange = (account: Options | null) => {
    const nextAccountId = account ? Number(account.value) : null;
    if (nextAccountId !== pdAccountId) {
      clearParentProjectState();
    }

    setPdAccountId(nextAccountId);
    setAccountName(account?.label ?? "");
    setFieldValue("accountId", nextAccountId);
    setFieldValue("accountName", account?.label ?? null);
  };

  const handleClassificationChange = (projectClassificationId: number | null) => {
    if (projectClassificationId !== classificationId && canEditParentHierarchy) {
      clearParentProjectState();
    }
    setFieldValue("projectClassificationId", projectClassificationId);
  };

  const handleUpdateProjectStatus = () => {
    navigate("/update-project-status", {
      state: {
        status: activeReferenceTypes.ProjectState.find((x) => x.id === values.projectStatusId),
        projectId: projectId,
      },
    });
  };

  const toOptions = (projects: ProjectOption[]) => {
    return projects.map(({ id, name, gsid }) => ({ value: id, label: `${name} (${gsid})` }));
  };

  const canSelectDeveloperAccount = () => {
    return authContext.isAdminUser() || authContext.isArmUser();
  };

  const getParentPoa = (): ProjectOption | undefined => {
    return candidatePOAs?.find((poa) => poa.id == values.masterProjectId);
  };

  const getParentRealCase = (): ProjectOption | undefined => {
    if (!shouldDisplayRealCaseInput) {
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
              disabled={!canEditDeveloperAccount}
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
            onChange={handleClassificationChange}
            helperText={helperTexts.projectClassification()}
            required
            disabled={!canEditProjectClassification}
          />
        </Grid>
        {shouldDisplayPOAInput && (
          <Grid item xs={12} md={6}>
            <CustomAutoComplete
              name="masterProjectId"
              label="Programme of Activities"
              options={toOptions(candidatePOAs)}
              onChange={handleMasterProjectChange}
              helperText={helperTexts.programmeOfActivities()}
              required
              disabled={!canEditParentHierarchy}
            />
          </Grid>
        )}
        {shouldDisplayRealCaseInput && (
          <Grid item xs={12} md={6}>
            <CustomAutoComplete
              name="parentProjectId"
              label="Real Case VPA"
              options={toOptions(candidateRealCaseVPAs)}
              onChange={handleParentProjectChange}
              helperText={helperTexts.realCaseVPA()}
              required
              disabled={!canEditParentHierarchy}
            />
          </Grid>
        )}
        <Grid item xs={12} md={6}>
          <ProjectTitle
            probablePii={probablePii}
            parentPoa={getParentPoa()}
            parentRealCase={getParentRealCase()}
            isUpdate={isUpdate}
            disabled={!canEditProjectTitle}
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

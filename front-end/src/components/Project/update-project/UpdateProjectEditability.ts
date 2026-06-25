import type {
  CreateProjectForm,
  TransitionProjectDetails,
  UpdateProjectPayload,
} from "../../../common/project/Project";
import type { AuthContextType } from "../../Auth/Login/AuthProvider";

const NEW_PROJECT_STATUS_ID = 1;

export interface UpdateProjectFieldCapabilities {
  canEditDeveloperAccount: boolean;
  canEditProjectClassification: boolean;
  canEditProjectTitle: boolean;
  canEditParentHierarchy: boolean;
}

const NO_UPDATE_CAPABILITIES: UpdateProjectFieldCapabilities = {
  canEditDeveloperAccount: false,
  canEditProjectClassification: false,
  canEditProjectTitle: false,
  canEditParentHierarchy: false,
};

const FULL_UPDATE_CAPABILITIES: UpdateProjectFieldCapabilities = {
  canEditDeveloperAccount: true,
  canEditProjectClassification: true,
  canEditProjectTitle: true,
  canEditParentHierarchy: true,
};

const PD_NEW_UPDATE_CAPABILITIES: UpdateProjectFieldCapabilities = {
  canEditDeveloperAccount: false,
  canEditProjectClassification: true,
  canEditProjectTitle: true,
  canEditParentHierarchy: false,
};

type UpdateProjectAuthContext = Pick<AuthContextType, "isAdminUser" | "isArmUser" | "isUserOwnerOf">;

export const getUpdateProjectFieldCapabilities = (
  formValues: CreateProjectForm,
  authContext: UpdateProjectAuthContext,
): UpdateProjectFieldCapabilities => {
  if (authContext.isAdminUser() || authContext.isArmUser()) {
    return FULL_UPDATE_CAPABILITIES;
  }

  if (
    formValues.accountId &&
    authContext.isUserOwnerOf(formValues.accountId) &&
    formValues.projectStatusId === NEW_PROJECT_STATUS_ID
  ) {
    return PD_NEW_UPDATE_CAPABILITIES;
  }

  return NO_UPDATE_CAPABILITIES;
};

export const hasAnyUpdateCapability = (capabilities: UpdateProjectFieldCapabilities): boolean => {
  return Object.values(capabilities).some(Boolean);
};

const isAtLeastOneDetailDefined = (details: TransitionProjectDetails) => {
  return Object.values(details).some((value) => value !== undefined && value !== null && value !== "");
};

const getTransitionProjectDetails = (formValues: CreateProjectForm): TransitionProjectDetails => {
  return {
    projectLinkInOtherScheme: formValues.projectLinkInOtherScheme,
    creditingPeriodStartDateOtherSchemes: formValues.creditingPeriodStartDateOtherSchemes,
    creditingPeriodEndDateOtherSchemes: formValues.creditingPeriodEndDateOtherSchemes,
    transitionApprovalProcedureId: formValues.transitionApprovalProcedureId,
    transitionFromOtherSchemeId: formValues.transitionFromOtherSchemeId,
    transitionPathwayId: formValues.transitionPathwayId,
  };
};

export const buildUpdateProjectPayload = (
  formValues: CreateProjectForm,
  originalValues: CreateProjectForm,
  capabilities: UpdateProjectFieldCapabilities,
): UpdateProjectPayload => {
  const canEditFullProjectDetails =
    capabilities.canEditDeveloperAccount &&
    capabilities.canEditParentHierarchy &&
    capabilities.canEditProjectClassification &&
    capabilities.canEditProjectTitle;
  const sourceValues = canEditFullProjectDetails ? formValues : originalValues;
  const permittedValues = {
    ...sourceValues,
    accountId: capabilities.canEditDeveloperAccount ? formValues.accountId : originalValues.accountId,
    projectClassificationId: capabilities.canEditProjectClassification
      ? formValues.projectClassificationId
      : originalValues.projectClassificationId,
    masterProjectId:
      (capabilities.canEditParentHierarchy ? formValues.masterProjectId : originalValues.masterProjectId) ?? null,
    parentProjectId:
      (capabilities.canEditParentHierarchy ? formValues.parentProjectId : originalValues.parentProjectId) ?? null,
    name: capabilities.canEditProjectTitle ? formValues.name : originalValues.name,
  };
  const transitionProjectDetails = getTransitionProjectDetails(permittedValues);

  return {
    ...permittedValues,
    transitionProject: isAtLeastOneDetailDefined(transitionProjectDetails) ? transitionProjectDetails : null,
  };
};

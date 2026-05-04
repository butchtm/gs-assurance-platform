import { User } from "../User/User";

export interface AccountType {
  id: number | undefined;
  code: string;
  description: string;
  isActive?: boolean;
}

export interface Account {
  id: number;
  name: string;
  emailAddress: string | undefined;
  active: boolean;
  address: string;
  accountType: AccountType;
}

export interface PlatformAccount {
  id: number;
  accountType: AccountType;
  name: string;
  emailAddress: string | undefined;
  address: string;
  users: User[];
  active: boolean;
}

export interface UpdateCreateCompetenciesPayload {
  accountId?: number;
  estimatedAnnualEmissionReductionAmount?: number;
  projectGsids?: string[];
  projectTypeIds: number[];
  projectLocationIds: number[];
  projectScaleIds: number[];
  methodologyIds: number[];
  requestTypeIds: number[];
  certifiedProductIds: number[];
  vvbAssignedIds?: number[];
  projectDeveloperIds?: number[];
  sustainableDevelopmentGoalIds: number[];
}

export interface CompetenciesDisplay {
  accountId?: number;
  estimatedAnnualEmissionReductionAmount?: number | null;
  projectGsids?: string[];
  projectTypeIds: string[];
  projectLocationIds: string[];
  projectScaleIds: string[];
  methodologyIds: string[];
  requestTypeIds: string[];
  certifiedProductIds: string[];
  vvbAssignedIds?: string[];
  projectDeveloperIds?: string[];
  sustainableDevelopmentGoalIds: string[];
}

export const getRefDataNameForCompetencyKey = (key: keyof UpdateCreateCompetenciesPayload): string => {
  const competencyTypeRefDataNames: {
    [K in keyof UpdateCreateCompetenciesPayload]: string;
  } = {
    accountId: "AccountId",
    estimatedAnnualEmissionReductionAmount: "EstimatedAnnualEmissionReductionAmount",
    projectGsids: "ProjectGsids",
    projectTypeIds: "ProjectType",
    projectLocationIds: "ProjectLocation",
    projectScaleIds: "ProjectSize",
    methodologyIds: "Methodology",
    requestTypeIds: "RequestType",
    certifiedProductIds: "ProjectAsset",
    vvbAssignedIds: "VvbAssignedIds",
    projectDeveloperIds: "ProjectDeveloperIds",
    sustainableDevelopmentGoalIds: "SustainableDevelopmentGoal",
  };

  const refDataName = competencyTypeRefDataNames[key];
  if (!refDataName) throw Error(`No RefData name for competency key: ${key}`);
  return refDataName;
};

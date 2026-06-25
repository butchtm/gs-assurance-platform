import { ReferenceType } from "../../common/data-types/DataReferenceTypes";

const CANONICAL_PROJECT_CLASSIFICATION_CODES: Record<string, string> = {
  REAL_CASE_VPA_CROSS_LINKAGE: "REAL_CASE_VPA",
  REGULAR_VPA_CROSS_LINKAGE: "REGULAR_VPA",
};

const POA_CLASSIFICATION_CODES = new Set(["PROGRAMME_OF_ACTIVITIES", "POA"]);
const PROJECT_CLASSIFICATION_REQUIRES_POA_CODES = new Set([
  "REGULAR_VPA",
  "REGULAR_CASE_VPA",
  "REAL_CASE_VPA",
  "REAL_CASE_VPA_LEGACY",
  "VPA_CPA",
]);
const PROJECT_CLASSIFICATION_REQUIRES_REAL_CASE_VPA_CODES = new Set(["REGULAR_VPA", "REGULAR_CASE_VPA"]);

export const canonicalProjectClassificationCode = (code: string | undefined): string | undefined => {
  if (!code) {
    return code;
  }
  return CANONICAL_PROJECT_CLASSIFICATION_CODES[code] ?? code;
};

export const getCanonicalProjectClassificationCodeById = (
  classificationId: number | string | null | undefined,
  projectClassifications: ReferenceType[] = [],
): string | undefined => {
  if (classificationId === null || classificationId === undefined || classificationId === "") {
    return undefined;
  }

  const rawCode = projectClassifications.find((classification) => classification.id === Number(classificationId))?.code;
  return canonicalProjectClassificationCode(rawCode);
};

export const getCanonicalProjectClassificationDescriptionById = (
  classificationId: number | string | null | undefined,
  projectClassifications: ReferenceType[] = [],
): string | undefined => {
  if (classificationId === null || classificationId === undefined || classificationId === "") {
    return undefined;
  }

  const canonicalCode = getCanonicalProjectClassificationCodeById(classificationId, projectClassifications);
  const rawClassificationId = Number(classificationId);
  return (
    projectClassifications.find((classification) => classification.code === canonicalCode)?.description ??
    projectClassifications.find((classification) => classification.id === rawClassificationId)?.description
  );
};

export const isPoaProjectClassification = (code: string | undefined): boolean => {
  return POA_CLASSIFICATION_CODES.has(canonicalProjectClassificationCode(code) ?? "");
};

export const projectClassificationCodeRequiresPoa = (code: string | undefined): boolean => {
  return PROJECT_CLASSIFICATION_REQUIRES_POA_CODES.has(canonicalProjectClassificationCode(code) ?? "");
};

export const projectClassificationRequiresPoa = (
  classificationId: number | string | null | undefined,
  projectClassifications: ReferenceType[] = [],
): boolean => {
  return projectClassificationCodeRequiresPoa(
    getCanonicalProjectClassificationCodeById(classificationId, projectClassifications),
  );
};

export const projectClassificationCodeRequiresRealCaseVpa = (code: string | undefined): boolean => {
  return PROJECT_CLASSIFICATION_REQUIRES_REAL_CASE_VPA_CODES.has(canonicalProjectClassificationCode(code) ?? "");
};

export const projectClassificationRequiresRealCaseVpa = (
  classificationId: number | string | null | undefined,
  projectClassifications: ReferenceType[] = [],
): boolean => {
  return projectClassificationCodeRequiresRealCaseVpa(
    getCanonicalProjectClassificationCodeById(classificationId, projectClassifications),
  );
};

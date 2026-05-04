import { CreditingPeriod, findLatestCreditingPeriod, Methodology } from "../../../../../common/project/Project";
import { MethodologyConfiguration } from "../Request";
import { ReferenceType, Options } from "../../../../../common/data-types/DataReferenceTypes";

export const NO_METHODOLOGIES_MESSAGE = "Please enter methodologies and their versions for this crediting period";
const SELECTED_PERIOD_MESSAGE =
  "Methodologies and their versions have been prepopulated from the selected crediting period";
const PREVIOUS_PERIOD_MESSAGE =
  "Methodologies and their versions have been prepopulated from the previous crediting period";
const LATEST_PERIOD_MESSAGE =
  "Methodologies and their versions have been prepopulated from the latest crediting period";
export const FIRST_VPA_MESSAGE = "The methodologies and versions of this VPA will be applied to all VPAs in this batch";
export const MULTIPLE_CHILD_VPA_MESSAGE =
  "All VPAs in this batched request must have the same methodologies and versions";
const VPA_DESCRIPTIONS = ["Real Case VPA", "Regular VPA"];
export const VPA_DESCRIPTIONS_LOWER_CASE = VPA_DESCRIPTIONS.map((desc) => desc.toLowerCase());

export const toMethodologyIds = (existingMethodologies?: MethodologyConfiguration[]) => {
  if (!existingMethodologies) {
    return [];
  }
  return existingMethodologies
    .map((m) => m.methodologyId)
    .filter((id) => id !== "")
    .map((id) => Number(id));
};

export const toAllowedMethodologyOptions = (
  referenceMethodologies: ReferenceType[],
  existingMethodologyIds: number[],
  allMethodologyOptions: Options[],
) => {
  const activeMethodologyIds = referenceMethodologies.filter((m) => m.active).map((m) => m.id);
  const allowedMethodologyIds = [...new Set([...activeMethodologyIds, ...existingMethodologyIds])];
  return allMethodologyOptions.filter((option) => allowedMethodologyIds.includes(Number(option.value)));
};

export const prefillMethodologiesFromProject = (
  creditingPeriod: CreditingPeriod | undefined,
  creditingPeriods: CreditingPeriod[],
  setPrefilledMethodologies: (methodologies: Methodology[], creditingPeriodId?: number) => void,
  setPrefilledMessage: (message: string) => void,
) => {
  if (creditingPeriod?.methodologies && creditingPeriod?.methodologies.length > 0) {
    setPrefilledMethodologies(creditingPeriod?.methodologies, creditingPeriod?.periodId);
    setPrefilledMessage(SELECTED_PERIOD_MESSAGE);
    return;
  }

  const previousCreditingPeriod = creditingPeriods.find((x) => x.periodId == (creditingPeriod?.periodId ?? 0) - 1);
  if (previousCreditingPeriod && previousCreditingPeriod.methodologies.length > 0) {
    setPrefilledMethodologies(previousCreditingPeriod.methodologies, previousCreditingPeriod.periodId);
    setPrefilledMessage(PREVIOUS_PERIOD_MESSAGE);
    return;
  }

  const latestCreditingPeriod = findLatestCreditingPeriod(creditingPeriods);
  if (latestCreditingPeriod && latestCreditingPeriod.methodologies.length > 0) {
    setPrefilledMethodologies(latestCreditingPeriod.methodologies, latestCreditingPeriod.periodId);
    setPrefilledMessage(LATEST_PERIOD_MESSAGE);
    return;
  }

  setPrefilledMessage(NO_METHODOLOGIES_MESSAGE);
  setPrefilledMethodologies([]);
};

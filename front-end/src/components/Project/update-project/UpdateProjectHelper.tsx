import { CreateProjectForm, Project, TransitionDetail } from "../../../common/project/Project";

interface IdentifiableType {
  id: number;
}

const toIdArray = (items?: IdentifiableType[] | null) => {
  return items?.map((item) => item.id) || [];
};

const toId = (item: IdentifiableType | null) => {
  return item?.id ?? null;
};

const toYesOrNo = (value?: TransitionDetail | null | undefined) => {
  return value ? "yes" : "no";
};

const populateTransitionDetail = (form: CreateProjectForm, detail: TransitionDetail | null | undefined) => {
  form.transitioningProject = toYesOrNo(detail);
  if (!detail) {
    return form;
  }
  form.transitionFromOtherSchemeId = detail.transitionFromOtherScheme?.id ?? null;
  form.creditingPeriodStartDateOtherSchemes = detail.creditingPeriodStartDateOtherSchemes ?? null;
  form.creditingPeriodEndDateOtherSchemes = detail.creditingPeriodEndDateOtherSchemes ?? null;
  form.transitionPathwayId = detail.transitionPathway?.id ?? null;
  form.transitionApprovalProcedureId = detail.transitionApprovalProcedure?.id ?? null;
  form.projectLinkInOtherScheme = detail.projectLinkInOtherScheme;
  return form;
};

export const mapProjectDataToFormValues = (project: Project): CreateProjectForm => {
  try {
    const form = {
      accountId: project.accountId,
      accountName: null,
      projectClassificationId: project.projectClassification?.id,
      masterProjectId: project.masterProject?.id,
      parentProjectId: project.parentProject?.id,
      locationIds: toIdArray(project.countries),
      description: project.description,
      sizeId: toId(project.projectSize),
      typeIds: toIdArray(project.projectTypes),
      name: project.name,
      startDate: project.projectStartDate,
      registrationTypeId: toId(project.registrationType),
      projectAssetIds: toIdArray(project.projectAssets),
      carbonStreamId: toId(project.carbonStream),
      transitioningProject: "no",
      transitionFromOtherSchemeId: null,
      creditingPeriodStartDateOtherSchemes: null,
      creditingPeriodEndDateOtherSchemes: null,
      transitionPathwayId: null,
      transitionApprovalProcedureId: null,
      projectLinkInOtherScheme: undefined,
      creditingPeriods: project.creditingPeriods,
      estimatedAnnualUnits: project.estimatedAnnualUnits,
      sustainableDevelopmentGoalIds: toIdArray(project.sustainableDevelopmentGoals),
      feeScheduleId: toId(project.feeSchedule),
      notes: project.notes,
      currentStandardId: 2, // hardcoded as it should always be this for now
      designCertificationDate: project.designCertificationDate,
      certifiedProjectDate: project.certifiedProjectDate,
      lastDesignCertificationRenewalDate: project.lastDesignCertificationRenewalDate,
      lastPerformanceReviewDate: project.lastPerformanceReviewDate,
      projectStatusId: toId(project.projectStatus),
      listingDate: project.listingDate,
      listingDateManualSet: project.listingDateManualSet,
      textAnalysisResults: null,
      displayNotRequestedStatusRequests: project.displayNotRequestedStatusRequests,
    };
    return populateTransitionDetail(form, project.transitionDetail);
  } catch (error) {
    console.error("Error fetching and mapping project data:", error);
    throw error;
  }
};

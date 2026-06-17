import * as Yup from "yup";

import { ReferenceTypes } from "../../common/data-types/DataReferenceTypes";
import { AuthContextType } from "../Auth/Login/AuthProvider";

import {
  projectClassificationRequiresPoa,
  projectClassificationRequiresRealCaseVpa,
} from "./ProjectClassificationSemantics";

export const getValidationSchemaStep1 = (allReferenceTypes: ReferenceTypes, authContext: AuthContextType) => {
  const noneClassification = allReferenceTypes.ProjectClassification.find((x) => x.code === "NONE");
  const canSelectDeveloperAccount = authContext.isAdminUser() || authContext.isArmUser();
  return Yup.object().shape({
    accountId: canSelectDeveloperAccount
      ? Yup.string().required("Project developer account is required")
      : Yup.string().notRequired(),
    projectClassificationId: Yup.string()
      .required("Project classification is required")
      .notOneOf([noneClassification?.id.toString()], "Project classification is required"),
    name: Yup.string().required("Project title is required").max(255, "Project title must be 255 characters or fewer"),
    masterProjectId: Yup.string().when("projectClassificationId", {
      is: (val: string) => projectClassificationRequiresPoa(val, allReferenceTypes.ProjectClassification),
      then: (schema) => schema.required("Programme of Activities is required"),
      otherwise: (schema) => schema.notRequired(),
    }),
    parentProjectId: Yup.string().when("projectClassificationId", {
      is: (val: string) => projectClassificationRequiresRealCaseVpa(val, allReferenceTypes.ProjectClassification),
      then: (schema) => schema.required("Real Case VPA is required"),
      otherwise: (schema) => schema.notRequired(),
    }),
  });
};

export const validationSchemaStep2 = Yup.object().shape({
  description: Yup.string().max(9000, "Project description must be 9000 characters or fewer"),
  typeIds: Yup.array().of(Yup.number()).min(1, "At least one project type is required"),
});

const methodologySchema = Yup.object().shape({
  id: Yup.number().notOneOf([-1], "Methodology is required").required("Methodology is required"),
  version: Yup.number().required("Version is required"),
});

const creditingPeriodSchema = Yup.object().shape({
  periodId: Yup.string().required("Period ID is required"),
  startDate: Yup.date().required("Start date is required"),
  endDate: Yup.date().required("End date is required"),
  methodologies: Yup.array()
    .of(methodologySchema)
    .when("periodId", {
      is: (val: string) => val === "1",
      then: (sch) => sch.min(1, "At least one methodology is required"),
      otherwise: (sch) => sch.notRequired(),
    }),
});

export const validationSchemaStep3 = Yup.object().shape({
  creditingPeriods: Yup.array().min(1, "At least one crediting period is required").of(creditingPeriodSchema),
});

export const validationSchemaStep4 = Yup.object().shape({
  transitioningProject: Yup.string().required("Transitioning project is required"),
  transitionFromOtherSchemeId: Yup.string().when("transitioningProject", {
    is: "yes",
    then: (schema) => schema.required("Other standard is required"),
    otherwise: (schema) => schema.notRequired(),
  }),
  creditingPeriodStartDateOtherSchemes: Yup.date().when("transitioningProject", {
    is: "yes",
    then: (schema) =>
      schema
        .required("Other schemes crediting period start date is required")
        .test("start-before-end", "Start date must be before end date", function (value) {
          const { creditingPeriodEndDateOtherSchemes } = this.parent;
          if (!value || !creditingPeriodEndDateOtherSchemes) return true;
          return value <= creditingPeriodEndDateOtherSchemes;
        }),
    otherwise: (schema) => schema.nullable(),
  }),

  creditingPeriodEndDateOtherSchemes: Yup.date().when("transitioningProject", {
    is: "yes",
    then: (schema) =>
      schema
        .required("Other schemes crediting period end date is required")
        .test("end-after-start", "End date must be after start date", function (value) {
          const { creditingPeriodStartDateOtherSchemes } = this.parent;
          if (!value || !creditingPeriodStartDateOtherSchemes) return true;
          return value >= creditingPeriodStartDateOtherSchemes;
        }),
    otherwise: (schema) => schema.nullable(),
  }),
  transitionPathwayId: Yup.string().when("transitioningProject", {
    is: "yes",
    then: (schema) => schema.required("Transition pathway is required"),
    otherwise: (schema) => schema.notRequired(),
  }),
  transitionApprovalProcedureId: Yup.string().when("transitioningProject", {
    is: "yes",
    then: (schema) => schema.required("Transitioning approval procedure is required"),
    otherwise: (schema) => schema.notRequired(),
  }),
  projectLinkInOtherScheme: Yup.string().when("transitioningProject", {
    is: "yes",
    then: (schema) => schema.required("Project link in other scheme is required"),
    otherwise: (schema) => schema.notRequired(),
  }),
});

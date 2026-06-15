import { mockAssignmentStatus, mockAssignmentTypes } from "../assignments/DebugAssignments";

export const mockActiveReferenceTypes = {
  ProjectLocation: [
    { id: 1, code: "ProjectLocationCode1", description: "ProjectLocation1", active: true },
    { id: 2, code: "ProjectLocationCode2", description: "ProjectLocation2", active: true },
  ],
  TransitionFromOtherScheme: [
    { id: 1, code: "TransitionFromOtherSchemeCode1", description: "TransitionFromOtherScheme1", active: true },
    { id: 2, code: "TransitionFromOtherSchemeCode2", description: "TransitionFromOtherScheme2", active: true },
  ],
  TransitionPathway: [
    { id: 1, code: "TransitionPathwayCode1", description: "TransitionPathway1", active: true },
    { id: 2, code: "TransitionPathwayCode2", description: "TransitionPathway2", active: true },
  ],
  TransitionApprovalProcedure: [
    { id: 1, code: "TransitionApprovalProcedureCode1", description: "TransitionApprovalProcedure1", active: true },
    { id: 2, code: "TransitionApprovalProcedureCode2", description: "TransitionApprovalProcedure2", active: true },
  ],
  ProjectAsset: [
    { id: 1, code: "ProjectAssetCode1", description: "ProjectAsset1", active: true },
    { id: 2, code: "ProjectAssetCode2", description: "ProjectAsset2", active: true },
  ],
  ProjectClassification: [
    { id: 1, code: "ProjectClassificationCode1", description: "ProjectClassification1", active: true },
    { id: 2, code: "ProjectClassificationCode2", description: "ProjectClassification2", active: true },
    { id: 7, code: "PROGRAMME_OF_ACTIVITIES", description: "Programme of Activities", active: true },
    { id: 8, code: "REAL_CASE_VPA", description: "REAL_CASE_VPA", active: true },
    { id: 9, code: "REGULAR_VPA", description: "REGULAR_VPA", active: true },
  ],
  ProjectState: [
    { id: 1, code: "NEW", description: "New", active: true },
    { id: 2, code: "LISTED", description: "Listed", active: true },
    { id: 3, code: "DESIGN_CERTIFIED", description: "Design Certified", active: true },
    { id: 4, code: "ISSUED", description: "Issued", active: true },
  ],
  ProjectSize: [
    { id: 0, code: "MICROSCALE", description: "Micro Scale", active: true },
    { id: 1, code: "SMALL_SCALE", description: "Small Scale", active: true },
    { id: 2, code: "LARGE_SCALE", description: "Large Scale", active: true },
  ],
  RegistrationType: [
    { id: 1, code: "REGULAR", description: "Regular", active: true },
    { id: 2, code: "RETROACTIVE", description: "Retroactive", active: true },
  ],
  CarbonStream: [
    { id: 1, code: "CarbonStreamCode1", description: "CarbonStream1", active: true },
    { id: 2, code: "CarbonStreamCode2", description: "CarbonStream2", active: true },
  ],
  ProjectType: [
    { id: 1, code: "ProjectTypeCode1", description: "ProjectType1", active: true },
    { id: 2, code: "ProjectTypeCode2", description: "ProjectType2", active: true },
  ],
  Methodology: [
    { id: 1, code: "MethodologyCode1", description: "Methodology1", active: true, paa: true },
    { id: 2, code: "MethodologyCode2", description: "Methodology2", active: true, paa: true },
    { id: 3, code: "MethodologyCode3", description: "Methodology3", active: true, paa: true },
  ],
  FeeSchedule: [
    { id: 1, code: "FeeScheduleCode1", description: "Cash per credit", active: true },
    { id: 2, code: "FeeScheduleCode2", description: "Share of proceeds", active: true },
  ],
  SustainableDevelopmentGoal: [
    { id: 1, code: "SustainableDevelopmentGoalCode1", description: "SustainableDevelopmentGoal1", active: true },
    { id: 2, code: "SustainableDevelopmentGoalCode2", description: "SustainableDevelopmentGoal2", active: true },
  ],
  RequestType: [
    { id: 1, code: "TYPE1", description: "Type1", active: true },
    { id: 2, code: "TYPE2", description: "Type2", active: true },
  ],
  RequestStatus: [
    { id: 0, code: "NOT_REQUESTED", description: "Not requested", active: true },
    { id: 1, code: "BOOKED", description: "Booked", active: true },
    { id: 2, code: "REVIEW_PLANNED_TO_START", description: "Planned To Start", active: true },
    { id: 3, code: "TODO: UNKNOWN STATUS", description: "TODO: UNKNOWN STATUS", active: true },
    { id: 4, code: "COMPLETED_APPROVED", description: "Completed - Approved", active: true },
    { id: 5, code: "COMPLETED_REJECTED", description: "Completed - Rejected", active: true },
    { id: 6, code: "IN_PREPARATION", description: "In Preparation", active: true },
    { id: 7, code: "OMITTED", description: "Omitted", active: true },
    { id: 8, code: "CANCELLED", description: "Cancelled", active: true },
    { id: 9, code: "COMPLETENESS_CHECK", description: "Completeness Check", active: true },
    { id: 10, code: "IN_PROGRESS", description: "In Progress", active: true },
    { id: 11, code: "IN_AUDIT", description: "In Audit", active: true },
    { id: 12, code: "IN_ASSIGNMENT", description: "In Assignment", active: true },
    { id: 13, code: "IN_ROUND_COMPLETION", description: "In Round Completion", active: true },
  ],
  AssignmentType: mockAssignmentTypes,
  AssignmentStatus: mockAssignmentStatus,
  DeletionReason: [
    { id: 1, code: "REASON_1", description: "Test Reason" },
    { id: 2, code: "REASON_2", description: "Test Reason 2" },
  ],
  SupressionReason: [
    { id: 1, code: "REASON_1", description: "Test Reason" },
    { id: 2, code: "REASON_2", description: "Test Reason 2" },
  ],
  BatchType: [
    { id: 1, code: "BATCH_REQ_TYPE_1", description: "Batch Request Type 1" },
    { id: 2, code: "BATCH_REQ_TYPE_2", description: "Batch Request Type 2" },
  ],
  DocumentType: [
    { id: 1, code: "DOCUMENT_TYPE_1", description: "Doc Type 1" },
    { id: 2, code: "DOCUMENT_TYPE_2", description: "Doc Type 2" },
    { id: 3, code: "OTHER_DOC_TYPE_1", description: "Other Document Type 1" },
    { id: 4, code: "OTHER_DOC_TYPE_2", description: "Other Document Type 2" },
    { id: 1017, code: "REVIEW_FEEDBACK_FORM", description: "Review Feedback Form", active: true },
  ],
  AccountType: [
    { id: 1, code: "ACCOUNT_TYPE_1", description: "Account Type 1" },
    { id: 1, code: "ACCOUNT_TYPE_1", description: "Account Type 1" },
  ],
  InvoiceStatus: [
    { id: 1, code: "INVOICE_STATUS_1", description: "Invoice status 1" },
    { id: 2, code: "INVOICE_STATUS_2", description: "Invoice status 2" },
  ],
  RequestRoundStatus: [
    { id: 1, code: "OPEN", description: "Open" },
    { id: 2, code: "CLOSED", description: "Closed" },
  ],
  PiiAction: [
    {
      id: 2,
      code: "ACCEPTED",
      description: "Accepted",
      active: true,
    },
    {
      id: 7,
      code: "DOCUMENT_REMOVED",
      description: "Document Removed",
      active: true,
    },
    {
      id: 0,
      code: "NEW",
      description: "New",
      active: true,
    },
    {
      id: 8,
      code: "NO_PII_FOUND",
      description: "No PII Found",
      active: true,
    },
    {
      id: 6,
      code: "PII_NOT_SUITABLE",
      description: "PII not suitable for the public",
      active: true,
    },
    {
      id: 4,
      code: "PARTIALLY_REDACTED",
      description: "Partially Redacted",
      active: true,
    },
    {
      id: 3,
      code: "REDACTED",
      description: "Redacted",
      active: true,
    },
    {
      id: 5,
      code: "REMOVED",
      description: "Removed",
      active: true,
    },
    {
      id: 1,
      code: "UNDER_INVESTIGATION",
      description: "Under Investigation",
      active: true,
    },
  ],
  ConversationStatus: [
    {
      id: 1,
      code: "OPEN",
      description: "Open",
    },
    {
      id: 2,
      code: "CLOSED",
      description: "Closed",
    },
  ],
  RequestCancellationReason: [
    { id: 0, code: "REASON1", description: "Reason 1" },
    { id: 1, code: "REASON2", description: "Reason 2" },
    { id: 2, code: "REASON3", description: "Reason 3" },
    { id: 3, code: "OTHER", description: "Other" },
  ],
  DigitalFormType: [{ id: 0, code: "ANNUAL_REPORT", description: "Annual Report" }],
  RequestFormStatus: [
    { id: 0, code: "DRAFT", description: "Draft" },
    { id: 1, code: "SUBMITTED", description: "Submitted" },
  ],
};

export const mockInactiveReferenceTypes = {
  ProjectClassification: [
    { id: 3, code: "ProjectClassificationLegacyCode1", description: "ProjectClassificationLegacy1", active: false },
    { id: 4, code: "ProjectClassificationLegacyCode2", description: "ProjectClassificationLegacy2", active: false },
  ],
  FeeSchedule: [
    { id: 1, code: "FeeScheduleLegacyCode1", description: "FeeScheduleLegacy1", active: false },
    { id: 2, code: "FeeScheduleLegacyCode2", description: "FeeScheduleLegacy2", active: false },
  ],
  RequestType: [
    { id: 1, code: "RequestTypeLegacyCode1", description: "RequestTypeLegacy1", active: false },
    { id: 2, code: "RequestTypeLegacyCode2", description: "RequestTypeLegacy2", active: false },
  ],
};

export const mockAllReferenceTypes = {
  ...mockActiveReferenceTypes,
  ...mockInactiveReferenceTypes,
};

export const mockPiiActions = [
  {
    value: 1,
    code: "UNDER_INVESTIGATION",
    label: "Under Investigation",
  },
];

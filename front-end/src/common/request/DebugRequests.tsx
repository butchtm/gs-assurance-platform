import dayjs from "dayjs";
import _ from "lodash";

import { RequestComment } from "../../components/requests/comments/RequestComments";
import { CreateRequestForm } from "../../components/requests/create-request/Wizard/Request";
import {
  CommonDashboardRequestResponse,
  RequestStatusResponse,
  TacRequestsResponse,
} from "../../components/requests/Requests";
import { ReferenceType, RequestType } from "../data-types/DataReferenceTypes";
import { mockActiveReferenceTypes } from "../data-types/DebugReferenceTypes";
import { PiiActionCode } from "../piiAnalysis/PiiAction";

import { GSRequest, RequestConsultation, TimelineActivity } from "./GSRequest";

const BASE_CREDITING_PERIOD = {
  periodId: null,
  startDate: null,
  endDate: null,
};

const BASE_MONITORING_PERIOD = {
  id: null,
  startDate: null,
  endDate: null,
  feeSchedule: null,
};

export const BASE_REQUEST_TYPE = (code: string, id: number = 1, overrides: Partial<RequestType> = {}): RequestType => ({
  id,
  code,
  description: _.startCase(code.toLowerCase()).replace(/\s/g, ""),
  supportsPAA: false,
  isActive: true,
  ...overrides,
});

const createDebugRequest = (overrides: Partial<GSRequest>) =>
  ({
    startDate: "",
    endDate: "",
    bookingDate: "",
    creditingPeriod: BASE_CREDITING_PERIOD,
    monitoringPeriod: BASE_MONITORING_PERIOD,
    manuallyAdded: false,
    requestStatusHistory: [],
    currentRoundIndex: null,
    requestRounds: [
      {
        roundIndex: 0,
        startDate: dayjs.utc().toISOString(),
        endDate: dayjs.utc().toISOString(),
        statusId: 0,
      },
    ],
    inStakeholderConsultation: false,
    emissionReductionAmount: 10000,
    batchId: 5,
    batchTypeId: 1,
    methodologies: [{ methodologyId: 0, methodologyVersion: 2.4, creditingPeriodId: 0 }],
    migrated: true,
    ...overrides,
  }) as GSRequest;

export const debugRequests: GSRequest[] = [
  createDebugRequest({
    id: 35562,
    index: 1,
    reviewName: "Request 1",
    requestType: BASE_REQUEST_TYPE("TYPE1", 1),
    projectId: 586,
    project: { gsid: "1000", id: 1, name: "project 1", programmeOfActivitiesId: 1 },
    requestStatus: { id: 9, code: "COMPLETENESS_CHECK", description: "GS for completeness check" },
    inStakeholderConsultation: true,
    currentConsultationStartDate: dayjs.utc().toISOString(),
    currentConsultationEndDate: dayjs.utc().add(1, "day").toString(),
    paa: true,
  }),
  createDebugRequest({
    id: 1,
    index: 1,
    reviewName: "Request 2",
    requestType: BASE_REQUEST_TYPE("TYPE2", 1),
    projectId: 3,
    project: { gsid: "3000", id: 3, name: "project 3", programmeOfActivitiesId: 2 },
    requestStatus: { id: 6, code: "IN_PREPARATION", description: "Project developer for preparation" },
    invoiceId: 2,
  }),
];

const defaultRequestStatusRequestInfo = createDebugRequest({
  id: 5,
  index: 1,
  invoiceId: 3,
  reviewName: "Request 3",
  sameReviewTypeCount: 1,
  reviewDocumentStatus: 123,
  requestType: BASE_REQUEST_TYPE("GENERIC", 1013),
  projectId: 1,
  project: { gsid: "1000", id: 1, name: "project 1", programmeOfActivitiesId: 1 },
  requestStatus: { id: 6, code: "IN_AUDIT", description: "In audit" },
  currentRoundIndex: 2,
  requestRounds: [
    { roundIndex: 2, startDate: dayjs.utc().toISOString(), endDate: dayjs.utc().toISOString(), statusId: 0 },
  ],
});

export const debugInAuditRequests: GSRequest[] = [
  createDebugRequest({
    ...defaultRequestStatusRequestInfo,
    requestStatus: { id: 11, code: "IN_AUDIT", description: "In audit" },
    currentRoundIndex: 1,
    requestStatusHistory: [{ id: 0, previousStatusId: 6, nextStatusId: 11, changedAt: "2024-10-2" }],
    requestRounds: [
      { roundIndex: 1, startDate: dayjs.utc().toISOString(), endDate: dayjs.utc().toISOString(), statusId: 0 },
    ],
  }),
  createDebugRequest({
    ...defaultRequestStatusRequestInfo,
    requestStatus: { id: 11, code: "IN_AUDIT", description: "In audit" },
    currentRoundIndex: 2,
    requestStatusHistory: [
      { previousStatusId: 9, nextStatusId: 12, id: 1, changedAt: "" },
      { previousStatusId: 13, nextStatusId: 11, id: 1, changedAt: "" },
    ],
    requestRounds: [
      { roundIndex: 2, startDate: dayjs.utc().toISOString(), endDate: dayjs.utc().toISOString(), statusId: 0 },
    ],
  }),
  createDebugRequest({
    ...defaultRequestStatusRequestInfo,
    requestStatus: { id: 11, code: "IN_AUDIT", description: "In audit" },
    currentRoundIndex: 2,
    requestStatusHistory: [
      { previousStatusId: 11, nextStatusId: 10, id: 1, changedAt: "" },
      { previousStatusId: 9, nextStatusId: 4, id: 1, changedAt: "" },
      { previousStatusId: 13, nextStatusId: 6, id: 1, changedAt: "" },
    ],
    requestRounds: [
      { roundIndex: 2, startDate: dayjs.utc().toISOString(), endDate: dayjs.utc().toISOString(), statusId: 1 },
    ],
  }),
];

export const debugInPreparationRequests: GSRequest[] = [
  createDebugRequest({
    ...defaultRequestStatusRequestInfo,
    currentRoundIndex: 0,
    requestStatus: { id: 6, code: "IN_PREPARATION", description: "In Preparation" },
    requestRounds: [],
  }),
  createDebugRequest({
    ...defaultRequestStatusRequestInfo,
    currentRoundIndex: 2,
    requestStatus: { id: 6, code: "IN_PREPARATION", description: "In Preparation" },
    requestStatusHistory: [
      { previousStatusId: 6, nextStatusId: 11, changedAt: "", id: 1 },
      { previousStatusId: 10, nextStatusId: 13, changedAt: "", id: 1 },
    ],
  }),
  createDebugRequest({
    ...defaultRequestStatusRequestInfo,
    currentRoundIndex: 2,
    requestStatus: { id: 6, code: "IN_PREPARATION", description: "In Preparation" },
    requestStatusHistory: [{ previousStatusId: 5, nextStatusId: 4, changedAt: "", id: 1 }],
  }),
  createDebugRequest({
    ...defaultRequestStatusRequestInfo,
    currentRoundIndex: 2,
    requestStatus: { id: 6, code: "IN_PREPARATION", description: "In Preparation" },
    requestStatusHistory: [{ previousStatusId: 5, nextStatusId: 4, changedAt: "", id: 1 }],
    fastTracked: true,
  }),
  createDebugRequest({
    ...defaultRequestStatusRequestInfo,
    currentRoundIndex: 0,
    requestStatus: { id: 6, code: "IN_PREPARATION", description: "In Preparation" },
    requestStatusHistory: [{ previousStatusId: 6, changedAt: "", id: 1, nextStatusId: 10 }],
    requestRounds: [],
  }),
  createDebugRequest({
    ...defaultRequestStatusRequestInfo,
    currentRoundIndex: null,
    requestStatus: { id: 6, code: "IN_PREPARATION", description: "In Preparation" },
    requestStatusHistory: [
      { previousStatusId: 6, changedAt: "", id: 1, nextStatusId: 9 },
      { previousStatusId: 9, changedAt: "", id: 2, nextStatusId: 6 },
    ],
    requestRounds: [],
  }),
  createDebugRequest({
    ...defaultRequestStatusRequestInfo,
    currentRoundIndex: 2,
    requestStatus: { id: 6, code: "IN_PREPARATION", description: "In Preparation" },
    requestStatusHistory: [
      { previousStatusId: 6, changedAt: "", id: 1, nextStatusId: 10 },
      { previousStatusId: 10, changedAt: "", id: 2, nextStatusId: 6 },
    ],
    requestRounds: [
      { roundIndex: 2, startDate: dayjs.utc().toISOString(), endDate: dayjs.utc().toISOString(), statusId: 0 },
    ],
  }),
];

export const debugInAssignmentRequests: GSRequest[] = [
  createDebugRequest({
    ...defaultRequestStatusRequestInfo,
    requestStatus: { id: 12, code: "IN_ASSIGNMENT", description: "In Assignment" },
  }),
  createDebugRequest({
    ...defaultRequestStatusRequestInfo,
    requestStatus: { id: 12, code: "IN_ASSIGNMENT", description: "In Assignment" },
  }),
];

export const debugInProgressRequests: GSRequest[] = [
  createDebugRequest({
    ...defaultRequestStatusRequestInfo,
    requestStatus: { id: 10, code: "IN_PROGRESS", description: "In Progress" },
    requestStatusHistory: [{ id: 0, previousStatusId: 12, nextStatusId: 10, changedAt: "2024-10-2" }],
  }),
];

export const debugInRoundCompletionRequests: GSRequest[] = [
  createDebugRequest({
    ...defaultRequestStatusRequestInfo,
    requestStatus: { id: 13, code: "IN_ROUND_COMPLETION", description: "In Round Completion" },
    requestStatusHistory: [{ id: 1, previousStatusId: 10, nextStatusId: 13, changedAt: dayjs.utc().toISOString() }],
  }),
  createDebugRequest({
    ...defaultRequestStatusRequestInfo,
    inStakeholderConsultation: true,
    requestStatus: { id: 13, code: "IN_ROUND_COMPLETION", description: "In Round Completion" },
    requestStatusHistory: [{ id: 2, previousStatusId: 10, nextStatusId: 13, changedAt: dayjs.utc().toISOString() }],
  }),
];

export const debugInCompletenessRequests: GSRequest[] = [
  createDebugRequest({
    ...defaultRequestStatusRequestInfo,
    requestStatusHistory: [],
    requestStatus: { id: 9, code: "COMPLETENESS_CHECK", description: "In Completeness" },
  }),
  createDebugRequest({
    ...defaultRequestStatusRequestInfo,
    requestStatusHistory: [
      { id: 1, previousStatusId: mockActiveReferenceTypes.RequestStatus[11].id, nextStatusId: 7, changedAt: "" },
    ],
    requestStatus: { id: 9, code: "COMPLETENESS_CHECK", description: "In Completeness" },
  }),
];

const defaultClosedRequest = createDebugRequest({
  id: 5,
  index: 1,
  invoiceId: 3,
  reviewName: "Request 3",
  sameReviewTypeCount: 1,
  reviewDocumentStatus: 123,
  requestType: BASE_REQUEST_TYPE("TYPE111", 111),
  projectId: 1,
  project: { gsid: "1000", id: 1, name: "project 1", programmeOfActivitiesId: 1 },
  requestStatus: { id: 4, code: "COMPLETED_APPROVED", description: "Completed - Approved" },
  currentRoundIndex: 2,
  requestRounds: [
    { roundIndex: 2, startDate: dayjs.utc().toISOString(), endDate: dayjs.utc().toISOString(), statusId: 0 },
  ],
});

export const debugNotRequestedRequest = createDebugRequest({
  ...defaultClosedRequest,
  requestStatus: { id: 0, code: "NOT_REQUESTED", description: "Not requested" },
});

export const debugNotClosedRequest = createDebugRequest({
  id: 5,
  index: 1,
  invoiceId: 3,
  reviewName: "Request 3",
  sameReviewTypeCount: 1,
  reviewDocumentStatus: 123,
  requestType: BASE_REQUEST_TYPE("TYPE111", 111),
  projectId: 1,
  project: { gsid: "1000", id: 1, name: "project 1", programmeOfActivitiesId: 1 },
  requestStatus: { id: 6, code: "IN_PREPARATION", description: "In Preparation" },
  currentRoundIndex: 2,
  requestRounds: [
    { roundIndex: 2, startDate: dayjs.utc().toISOString(), endDate: dayjs.utc().toISOString(), statusId: 0 },
  ],
  migrated: false,
});

export const debugClosedRequests: GSRequest[] = [
  createDebugRequest({
    ...defaultClosedRequest,
    requestStatus: { id: 4, code: "COMPLETED_APPROVED", description: "Completed - Approved" },
    migrated: false,
  }),
  createDebugRequest({
    ...defaultClosedRequest,
    requestStatus: { id: 5, code: "COMPLETED_REJECTED", description: "Completed - Rejected" },
  }),
  createDebugRequest({
    ...defaultClosedRequest,
    requestStatus: { id: 8, code: "CANCELLED", description: "Cancelled" },
  }),
  createDebugRequest({
    ...defaultClosedRequest,
    requestStatus: { id: 7, code: "OMITTED", description: "Omitted" },
  }),
];

export const debugRequestComments: RequestComment[] = [
  {
    id: 1,
    requestId: 1,
    submittedComment: "Test Comment",
    uploadedByName: "Test User",
    uploadedByEmail: "test@test.com",
    accountId: 1,
    accountType: { id: 1, code: "Test Acc Type", description: "Acc Type" },
    uploadedDate: dayjs.utc().toISOString(),
    publicAccess: true,
    anonymous: false,
    addressed: false,
    suppressed: false,
    suppressionReasonId: 0,
    deleted: false,
    deletedReasonId: 0,
    piiMetadata: { piiPresent: true, action: { code: PiiActionCode.NEW }, piiDetail: [] },
  },
  {
    id: 2,
    requestId: 2,
    submittedComment: "Test Comment 2",
    uploadedByName: "Test User 2",
    uploadedByEmail: "test@test.com",
    accountId: 2,
    accountType: { id: 1, code: "Test Acc Type", description: "Acc Type" },
    uploadedDate: dayjs.utc().toISOString(),
    publicAccess: true,
    anonymous: false,
    addressed: false,
    suppressed: false,
    suppressionReasonId: 0,
    deleted: false,
    deletedReasonId: 0,
  },
  {
    id: 3,
    requestId: 3,
    submittedComment: "Test Comment 3",
    uploadedByName: "Test User",
    uploadedByEmail: "test@test.com",
    accountId: 1,
    accountType: { id: 1, code: "Test Acc Type", description: "Acc Type" },
    uploadedDate: dayjs.utc().toISOString(),
    publicAccess: true,
    anonymous: false,
    addressed: false,
    suppressed: false,
    suppressionReasonId: 0,
    deleted: false,
    deletedReasonId: 0,
  },
];

export const debugRequestTransitionResponse: RequestStatusResponse = {
  requestStatusId: 13, // In Round Completion
  permittedStatuses: [
    {
      requestStatusId: 10, // In Progress
      minimumRoundOfReviewCount: 0,
      permittedDuringConsultation: true,
    },
    {
      requestStatusId: 11, // In Audit
      minimumRoundOfReviewCount: 0,
      permittedDuringConsultation: true,
    },
    {
      requestStatusId: 6, // In Preparation
      minimumRoundOfReviewCount: 0,
      permittedDuringConsultation: false,
    },
    {
      requestStatusId: 5, // Completed Rejected
      minimumRoundOfReviewCount: 0,
      permittedDuringConsultation: false,
    },
    {
      requestStatusId: 4, // Completed Approved
      minimumRoundOfReviewCount: 0,
      permittedDuringConsultation: false,
    },
  ],
  activeAccountTypes: [],
};

export const mockBatchRequestMappings: ReferenceType[] = [
  {
    id: 1,
    code: "BATCH_REQ_TYPE_1",
    description: "PoA + 1 Real Case",
  },
  {
    id: 2,
    code: "BATCH_REQ_TYPE_2",
    description: "PoA + 1 or more Real Case",
  },
];

const mockPrivateConsultation: RequestConsultation = {
  id: 1,
  startDate: "2025-04-24T12:43:41.05365Z",
  endDate: "2025-05-15T12:43:41.05365Z",
  type: {
    id: 0,
    userType: "TAC",
    workingDays: 15,
  },
  project: {
    gsid: 1,
    id: 1,
    name: "Project 1",
  },
  request: {
    id: 1,
    index: 1,
  },
};

const mockPublicConsultation: RequestConsultation = {
  id: 2,
  startDate: "2025-04-24T12:43:41.05365Z",
  endDate: "2025-05-08T12:43:41.05365Z",
  type: {
    id: 1,
    userType: "PUBLIC",
    workingDays: 10,
  },
  project: {
    gsid: 1,
    id: 1,
    name: "Project 1",
  },
  request: {
    id: 1,
    index: 1,
  },
};

export const mockRequestConsultations: RequestConsultation[] = [mockPrivateConsultation, mockPublicConsultation];

const createBatchRequest = (id: number, projectId: number, name: string, legacyIds?: number[]) =>
  createDebugRequest({
    id,
    index: 1,
    reviewName: name,
    sameReviewTypeCount: 1,
    reviewDocumentStatus: 123,
    invoiceId: null,
    requestType: BASE_REQUEST_TYPE("TYPE1", 1),
    projectId,
    project: { gsid: "1000", id: projectId, name, programmeOfActivitiesId: 1 },
    requestStatus: { id: 10, code: "IN_PROGRESS", description: "In Progress" },
    currentRoundIndex: null,
    requestRounds: [
      { roundIndex: 0, startDate: dayjs.utc().toISOString(), endDate: dayjs.utc().toISOString(), statusId: 0 },
    ],
    inStakeholderConsultation: true,
    emissionReductionAmount: 10000,
    batchId: 5,
    currentConsultationStartDate: dayjs.utc().toISOString(),
    currentConsultationEndDate: dayjs.utc().add(1, "day").toString(),
    ...(legacyIds && { legacyBatchProjectIds: legacyIds }),
  });

export const mockBatchProjectsRequests: GSRequest[] = [
  createBatchRequest(35562, 1, "project 1", [1, 2, 3]),
  createBatchRequest(35563, 4, "project 4", [4]),
  createDebugRequest({
    id: 35564,
    index: 1,
    reviewName: "Request 2",
    sameReviewTypeCount: 1,
    reviewDocumentStatus: 123,
    invoiceId: null,
    requestType: BASE_REQUEST_TYPE("TYPE1", 1),
    projectId: 5,
    project: { gsid: "1000", id: 5, name: "project 5", programmeOfActivitiesId: 1 },
    requestStatus: { id: 10, code: "IN_PROGRESS", description: "In Progress" },
    currentRoundIndex: null,
    requestRounds: [
      { roundIndex: 0, startDate: dayjs.utc().toISOString(), endDate: dayjs.utc().toISOString(), statusId: 0 },
    ],
    inStakeholderConsultation: true,
    emissionReductionAmount: 10000,
    batchId: 6,
    batchTypeId: 1,
    currentConsultationStartDate: dayjs.utc().toISOString(),
    currentConsultationEndDate: dayjs.utc().add(1, "day").toString(),
  }),
];

export const mockTacDashboardRequests: TacRequestsResponse[] = [
  {
    id: 1,
    projectGsid: "1000",
    projectName: "project 1",
    requestName: "request 1",
    requestStatus: {
      id: 10,
      code: "IN_PROGRESS",
      description: "In Progress",
    },
    vvbAccountId: 100,
    vvbAccountName: "testVVBAccountOne",
    currentConsultationStartDate: dayjs.utc().toISOString(),
    currentConsultationEndDate: dayjs.utc().add(1, "day").toString(),
    submittedByGivenName: "Joe",
    submittedByFamilyName: "Blogs",
  },
  {
    id: 2,
    projectGsid: "2000",
    projectName: "project 2",
    requestName: "request 2",
    requestStatus: {
      id: 10,
      code: "IN_ASSIGNMENT",
      description: "In Assignment",
    },
    vvbAccountId: 200,
    vvbAccountName: "testVVBAccountTwo",
    currentConsultationStartDate: dayjs.utc().toISOString(),
    currentConsultationEndDate: dayjs.utc().add(1, "day").toString(),
    submittedByGivenName: "Jane",
    submittedByFamilyName: "Doe",
  },
];

export const mockCurrentPathwayGroupTimelineActivities: TimelineActivity[] = [
  {
    index: 1,
    name: "Assigning for Review",
    startDate: "2024-02-10T15:00:00.000000Z",
    dueDate: "2024-02-13T15:00:00.000000Z",
    endDate: "2024-02-12T12:00:00.000000Z",
    completed: true,
    current: false,
    externalResponsibility: false,
    skipped: false,
  },
  {
    index: 2,
    name: "Round 1 Review",
    startDate: "2024-02-12T15:00:00.000000Z",
    dueDate: "2024-02-26T15:00:00.000000Z",
    endDate: "2024-03-01T12:00:00.000000Z",
    completed: true,
    current: false,
    externalResponsibility: true,
    skipped: false,
  },
];

export const mockCommonDashboardRequestResponse: CommonDashboardRequestResponse[] = [
  {
    id: 1,
    requestStatus: {
      id: 1,
      code: "Status1",
      description: "Status 1",
    },
    startDate: "2024-01-10",
    currentRoundEndDate: "",
    firstRoundStartDate: "",
    currentRoundIndex: 1,
    closedDate: "2024-01-15",
    type: "Type A",
    name: "Request A",
    projectTypesString: "Type1, Type2",
    project: {
      gsid: "GS1",
      name: "Project 1",
      status: {
        id: 3,
        code: "Status3",
        description: "Status3",
      },
      poaGsid: "POA2",
      realCaseVpaGsid: "VPA2",
      classification: {
        id: 3,
        code: "Class3",
        description: "Class3",
      },
      types: [
        { id: 3, code: "Type1", description: "Type1" },
        { id: 4, code: "Type2", description: "Type2" },
      ],
      feeSchedule: {
        id: 1,
        code: "CASH_FEE",
        description: "Cash per credit",
      },
      accountName: "Project Developer Account 1",
    },
    invoice: { status: { id: 2, code: "Invoice1", description: "Invoice 1" } },
    monitoringPeriod: {
      id: 1,
      feeSchedule: {
        id: 2,
        code: "SHARE_PROCEEDS",
        description: "Share of proceeds",
      },
    },
  },
  {
    id: 2,
    requestStatus: {
      id: 2,
      code: "Status2",
      description: "Status 2",
    },
    startDate: "2024-01-11",
    currentRoundEndDate: "",
    firstRoundStartDate: "",
    currentRoundIndex: 2,
    closedDate: "2024-01-16",
    type: "Type B",
    name: "Request B",
    projectTypesString: "Type3, Type4",
    project: {
      gsid: "GS2",
      name: "Project 2",
      status: {
        id: 3,
        code: "Status3",
        description: "Status3",
      },
      poaGsid: "POA2",
      realCaseVpaGsid: "VPA2",
      classification: {
        id: 3,
        code: "Class3",
        description: "Class3",
      },
      types: [
        { id: 3, code: "Type3", description: "Type3" },
        { id: 4, code: "Type3", description: "Type4" },
      ],
      feeSchedule: {
        id: 2,
        code: "SHARE_PROCEEDS",
        description: "Share of proceeds",
      },
      accountName: "Project Developer Account 2",
    },
    invoice: { status: { id: 2, code: "Invoice2", description: "Invoice 2" } },
    monitoringPeriod: {
      id: 2,
      feeSchedule: {
        id: 1,
        code: "CASH_FEE",
        description: "Cash per credit",
      },
    },
  },
];

export const mockCreateRequestFormInitialValues: CreateRequestForm = {
  requestType: "",
  creditingPeriodId: null,
  startDate: null,
  endDate: null,
  emissionReductionAmount: 0,
  batchProjectIds: [],
  requiredDocuments: null,
  createBatchRequest: "",
  useNewCreditingPeriod: false,
  canCreateNewCreditingPeriod: false,
  batchRequestTypeId: null,
  batchStepProjectValues: null,
  methodologyConfigurations: [],
  newCreditingPeriodStartDate: null,
  newCreditingPeriodEndDate: null,
  isMonitoringPeriodEnabled: false,
  canCreateNewMonitoringPeriod: false,
  useNewMonitoringPeriod: false,
  monitoringPeriodId: null,
  newMonitoringPeriodStartDate: null,
  newMonitoringPeriodEndDate: null,
  feeSchedule: null,
  paa: false,
};

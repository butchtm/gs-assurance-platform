import { BatchedProjectRequest } from "../../components/requests/Requests";

export const mockBatchedProjectsList: BatchedProjectRequest[] = [
  {
    id: 12345,
    name: "Test PoA",
    classification: "Programme of Activities",
    requestId: 1,
    gsid: "GS1",
    methodologies: [],
    creditingPeriods: [
      {
        periodId: 1,
        startDate: "2025-3-24",
        endDate: "2030-3-23",
        methodologies: [],
      },
    ],
  },
  {
    id: 6789,
    name: "Test Real Case VPA",
    classification: "Real Case VPA",
    requestId: 2,
    gsid: "GS2",
    methodologies: [],
    creditingPeriods: [
      {
        periodId: 1,
        startDate: "2025-2-24",
        endDate: "2030-2-23",
        methodologies: [],
      },
    ],
  },
];

export const mockBatchedProjectsListWithNewCreditingPeriod: BatchedProjectRequest[] = [
  {
    id: 12345,
    name: "Test PoA",
    classification: "Programme of Activities",
    requestId: 1,
    gsid: "GS1",
    methodologies: [],
    creditingPeriods: [
      {
        periodId: 1,
        startDate: "2025-3-24",
        endDate: "2030-3-23",
        methodologies: [],
      },
    ],
  },
  {
    id: 6789,
    name: "Test Real Case VPA",
    classification: "Real Case VPA",
    requestId: 2,
    gsid: "GS2",
    methodologies: [],
    creditingPeriods: [
      {
        periodId: 0,
        startDate: "2025-2-24",
        endDate: "2030-2-23",
        methodologies: [],
      },
    ],
  },
];

const initialFormikFormValues = {
  requestType: "",
  creditingPeriods: [],
  emissionReductionAmount: "",
  createBatchRequest: "",
  batchRequestTypeId: null,
  batchProjectIds: [],
};

export const mockBatchedProjectsFormikValues = {
  values: {
    ...initialFormikFormValues,
    requestType: "1",
    createBatchRequest: "yes",
    batchRequestTypeId: 0,
    batchProjectIds: [12345, 6789],
    batchStepProjectValues: {
      batch: {
        batchTypeId: 1,
        [12345]: {
          projectId: 12345,
          creditingPeriodId: 1,
          emissionReductionAmount: 999,
          methodologies: [
            {
              methodologyId: 1,
              methodologyVersion: 2.3,
              creditingPeriodId: 1,
            },
          ],
        },
        [6789]: {
          projectId: 6789,
          creditingPeriodId: 1,
          emissionReductionAmount: 998,
          methodologies: [
            {
              methodologyId: 2,
              methodologyVersion: 1.5,
              creditingPeriodId: 1,
            },
          ],
        },
      },
    },
  },
};

export const mockBatchedProjectsFormikValuesWithNewCreditingPeriod = {
  values: {
    ...initialFormikFormValues,
    requestType: "1",
    createBatchRequest: "yes",
    batchRequestTypeId: 0,
    batchProjectIds: [12345, 6789],
    batchStepProjectValues: {
      batch: {
        batchTypeId: 1,
        [12345]: {
          projectId: 12345,
          creditingPeriodId: 1,
          emissionReductionAmount: 999,
          methodologies: [
            {
              methodologyId: 1,
              methodologyVersion: 2.3,
              creditingPeriodId: 1,
            },
          ],
        },
        [6789]: {
          projectId: 6789,
          creditingPeriodId: null,
          newCreditingPeriodStartDate: "2025-2-24",
          newCreditingPeriodEndDate: "2030-2-23",
          useNewCreditingPeriod: true,
          emissionReductionAmount: 998,
          methodologies: [
            {
              methodologyId: 2,
              methodologyVersion: 1.5,
              creditingPeriodId: null,
            },
          ],
        },
      },
    },
  },
};

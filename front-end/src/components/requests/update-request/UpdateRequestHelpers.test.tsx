import { waitFor } from "@testing-library/react";
import { useLocation } from "react-router-dom";

import { GSRequest } from "../../../common/request/GSRequest";
import { ParentRequestWithBatchedRequests } from "../create-request/Wizard/Request";
import { BatchedProjectRequests, updateRequest } from "../Requests";

import { transformToParentRequestWithBatchedRequests, useUpdateRequest } from "./UpdateRequestHelpers";

jest.mock("../Requests", () => ({
  updateRequest: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
  useNavigate: () => jest.fn(),
  useLocation: jest.fn(),
}));

const parentRequest: GSRequest = {
  id: 1,
  startDate: "2025-08-05T14:42:00.000Z",
  endDate: "2025-10-05T14:42:00.000Z",
  bookingDate: "",
  requestType: {
    id: 1,
    code: "TYPE1",
    description: "Type1",
  },
  index: 1,
  reviewName: "Request 1",
  sameReviewTypeCount: 1,
  reviewDocumentStatus: 123,
  invoiceId: null,
  creditingPeriod: {
    periodId: 1,
    startDate: null,
    endDate: null,
  },
  monitoringPeriod: {
    id: 1,
    startDate: null,
    endDate: null,
    feeSchedule: 0,
  },
  manuallyAdded: false,
  requestStatusHistory: [],
  projectId: 586,
  project: {
    gsid: "GS1",
    id: 1,
    name: "Project 1",
    programmeOfActivitiesId: 1,
  },
  requestStatus: {
    id: 9,
    code: "COMPLETENESS_CHECK",
    description: "GS for completeness check",
  },
  currentRoundIndex: null,
  requestRounds: [
    {
      roundIndex: 0,
      startDate: "2025-08-05T14:42:00.000Z",
      endDate: "2025-08-05T14:42:00.000Z",
      statusId: 0,
    },
  ],
  inStakeholderConsultation: true,
  emissionReductionAmount: 10000,
  batchId: 5,
  batchTypeId: 1,
  currentConsultationStartDate: "2025-08-05T14:42:00.000Z",
  currentConsultationEndDate: "2025-08-05T14:42:00.000Z",
  methodologies: [{ methodologyId: 0, methodologyVersion: 2.4, creditingPeriodId: 0 }],
  migrated: true,
};

const childRequest: GSRequest = {
  ...parentRequest,
  id: 2,
  project: {
    gsid: "GS2",
    id: 2,
    name: "Project 2",
    programmeOfActivitiesId: 1,
  },
};

const batchedProjectRequests: BatchedProjectRequests = {
  parent: {
    classification: "Real Case VPA",
    gsid: "GS1",
    id: 1,
    name: "project 1",
    requestId: 1,
    creditingPeriods: [],
    methodologies: [],
  },
  children: [
    {
      classification: "Regular Case VPA",
      gsid: "GS2",
      id: 2,
      name: "project 2",
      requestId: 2,
      creditingPeriods: [],
      methodologies: [],
    },
  ],
};

const transformedBatchData: ParentRequestWithBatchedRequests = {
  requestTypeId: 1,
  creditingPeriodId: 1,
  startDate: "2025-08-05T14:42:00.000Z",
  emissionReductionAmount: 10000,
  feeSchedule: null,
  batch: {
    batchTypeId: 1,
    [1]: {
      projectId: 1,
      requestId: 1,
      requestTypeId: 1,
      creditingPeriodId: 1,
      startDate: "2025-08-05T14:42:00.000Z",
      endDate: "2025-10-05T14:42:00.000Z",
      emissionReductionAmount: 10000,
      methodologies: [
        {
          creditingPeriodId: 0,
          methodologyId: 0,
          methodologyVersion: 2.4,
        },
      ],
      useNewCreditingPeriod: false,
      canCreateNewCreditingPeriod: false,
      newCreditingPeriodStartDate: null,
      newCreditingPeriodEndDate: null,
      isMonitoringPeriodEnabled: true,
      canCreateNewMonitoringPeriod: false,
      useNewMonitoringPeriod: false,
      monitoringPeriodId: 1,
      newMonitoringPeriodStartDate: null,
      newMonitoringPeriodEndDate: null,
      feeSchedule: 0,
    },
    [2]: {
      projectId: 2,
      requestId: 2,
      requestTypeId: 1,
      creditingPeriodId: 1,
      startDate: "2025-08-05T14:42:00.000Z",
      endDate: "2025-10-05T14:42:00.000Z",
      emissionReductionAmount: 10000,
      methodologies: [
        {
          creditingPeriodId: 0,
          methodologyId: 0,
          methodologyVersion: 2.4,
        },
      ],
      useNewCreditingPeriod: false,
      canCreateNewCreditingPeriod: false,
      newCreditingPeriodStartDate: null,
      newCreditingPeriodEndDate: null,
      isMonitoringPeriodEnabled: true,
      canCreateNewMonitoringPeriod: false,
      useNewMonitoringPeriod: false,
      monitoringPeriodId: 1,
      newMonitoringPeriodStartDate: null,
      newMonitoringPeriodEndDate: null,
      feeSchedule: 0,
    },
  },
};

describe("UpdateRequestHelpers", () => {
  it("collates and transforms batched request data to a match the formik schema", () => {
    const actual = transformToParentRequestWithBatchedRequests([parentRequest, childRequest], batchedProjectRequests);
    expect(actual).toStrictEqual(transformedBatchData);
  });

  const batchedFormValues = {
    requestType: "1",
    creditingPeriodId: 1,
    startDate: "2025-08-05T14:42:00.000Z",
    endDate: "2025-10-05T14:42:00.000Z",
    emissionReductionAmount: 100,
    batchStepProjectValues: transformedBatchData,
    methodologyConfigurations: [],
    newCreditingPeriodStartDate: null,
    newCreditingPeriodEndDate: null,
    monitoringPeriodId: 1,
    newMonitoringPeriodStartDate: null,
    newMonitoringPeriodEndDate: null,
    feeSchedule: 0,
  };

  it("submits multiple updated request payloads for batched requests", async () => {
    (useLocation as jest.Mock).mockReturnValue({
      state: {
        request: parentRequest,
        requestBatches: batchedProjectRequests,
      },
    });
    const updateRequestWrapper = useUpdateRequest();
    await updateRequestWrapper(batchedFormValues);
    await waitFor(() => {
      expect(updateRequest as jest.Mock).toHaveBeenCalledTimes(2);
      expect(updateRequest as jest.Mock).toHaveBeenCalledWith(
        {
          id: 1,
          creditingPeriod: {
            periodId: 1,
            startDate: null,
            endDate: null,
          },
          emissionReductionAmount: 10000,
          methodologies: [
            {
              creditingPeriodId: 0,
              methodologyId: 0,
              methodologyVersion: 2.4,
            },
          ],
          monitoringPeriod: {
            id: 1,
            startDate: null,
            endDate: null,
            feeSchedule: 0,
          },
        },
        1,
      );
      expect(updateRequest as jest.Mock).toHaveBeenCalledWith(
        {
          id: 2,
          creditingPeriod: {
            periodId: 1,
            startDate: null,
            endDate: null,
          },
          emissionReductionAmount: 10000,
          methodologies: [
            {
              creditingPeriodId: 0,
              methodologyId: 0,
              methodologyVersion: 2.4,
            },
          ],
          monitoringPeriod: {
            id: 1,
            startDate: null,
            endDate: null,
            feeSchedule: 0,
          },
        },
        2,
      );
    });
  });

  const standaloneFormValues = {
    requestType: "1",
    creditingPeriodId: 1,
    startDate: "2025-08-05T14:42:00.000Z",
    endDate: "2025-10-05T14:42:00.000Z",
    emissionReductionAmount: 100,
    batchStepProjectValues: null,
    methodologyConfigurations: [],
    newCreditingPeriodStartDate: null,
    newCreditingPeriodEndDate: null,
    monitoringPeriodId: 1,
    newMonitoringPeriodStartDate: null,
    newMonitoringPeriodEndDate: null,
    feeSchedule: null,
  };
  it("submits the updated request payload for standalone request", async () => {
    (useLocation as jest.Mock).mockReturnValue({
      state: {
        request: parentRequest,
        requestBatches: batchedProjectRequests,
      },
    });
    const updateRequestWrapper = useUpdateRequest();
    await updateRequestWrapper(standaloneFormValues);
    await waitFor(() => {
      expect(updateRequest as jest.Mock).toHaveBeenCalledWith(
        {
          id: 1,
          creditingPeriod: {
            periodId: 1,
            startDate: null,
            endDate: null,
          },
          emissionReductionAmount: 100,
          methodologies: [],
          monitoringPeriod: {
            id: 1,
            startDate: null,
            endDate: null,
            feeSchedule: null,
          },
        },
        1,
      );
    });
  });
});

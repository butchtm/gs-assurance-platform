import { debugRequests } from "../../../../common/request/DebugRequests";
import { BatchedProjectRequest } from "../../Requests";

import { getBatchedFeeScheduleValue, getStandaloneFeeScheduleValue } from "./FeeSchedule";
import { ProjectIdWithRequestValues } from "./Request";

describe("FeeSchedule", () => {
  describe("for standalone requests", () => {
    it("gets fee schedule from request if available", () => {
      const feeSchedule = getStandaloneFeeScheduleValue(
        getMockRequest(0, 1, "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_PROJECT"),
      );
      expect(feeSchedule).toBe(0);
    });
    it("gets fee schedule from project if not a draft change", () => {
      const feeSchedule = getStandaloneFeeScheduleValue(
        getMockRequest(null, 1, "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_PROJECT"),
      );
      expect(feeSchedule).toBe(1);
    });
  });

  describe("for batched requests", () => {
    const firstChildProjectData: BatchedProjectRequest = {
      classification: "Real Case VPA",
      gsid: "GS1",
      id: 1,
      name: "project 1",
      requestId: 1,
      creditingPeriods: [
        {
          periodId: 1,
          startDate: "",
          endDate: "",
          monitoringPeriods: [
            {
              id: 1,
              startDate: "",
              endDate: "",
              feeSchedule: 1,
            },
          ],
          methodologies: [],
        },
      ],
      methodologies: [],
    };
    const firstChildRequest: ProjectIdWithRequestValues = {
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
    };
    it("gets fee schedule from request if available", () => {
      const feeSchedule = getBatchedFeeScheduleValue(
        getMockRequest(0, 1, "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_VPA"),
        firstChildProjectData,
        firstChildRequest,
      );
      expect(feeSchedule).toBe(0);
    });
    it("gets fee schedule from project if not a draft change", () => {
      const feeSchedule = getBatchedFeeScheduleValue(
        getMockRequest(null, 1, "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_VPA"),
        firstChildProjectData,
        firstChildRequest,
      );
      expect(feeSchedule).toBe(1);
    });
    it("gets fee schedule from child request if not relevant for parent", () => {
      const feeSchedule = getBatchedFeeScheduleValue(
        getMockRequest(null, null, "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_VPA"),
        firstChildProjectData,
        firstChildRequest,
      );
      expect(feeSchedule).toBe(0);
    });
    it("gets fee schedule from child project if not a draft change and not relevant to parent project", () => {
      const feeSchedule = getBatchedFeeScheduleValue(
        getMockRequest(null, null, "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_VPA"),
        firstChildProjectData,
        {
          ...firstChildRequest,
          feeSchedule: null,
        },
      );
      expect(feeSchedule).toBe(1);
    });
  });
});

const getMockRequest = (requestFeeSchedule: number | null, projectFeeSchedule: number | null, requestType: string) => {
  return {
    ...debugRequests[0],
    monitoringPeriod: {
      id: 1,
      startDate: null,
      endDate: null,
      feeSchedule: requestFeeSchedule,
    },
    creditingPeriod: {
      periodId: 1,
      startDate: "",
      endDate: "",
      monitoringPeriods: [
        {
          id: 1,
          startDate: null,
          endDate: null,
          feeSchedule: projectFeeSchedule,
        },
      ],
    },
    requestType: {
      id: 1,
      code: requestType,
      description: "",
    },
  };
};

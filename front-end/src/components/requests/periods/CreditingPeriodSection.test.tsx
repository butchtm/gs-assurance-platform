import { render, screen } from "@testing-library/react";

import { Project } from "../../../common/project/Project";
import { GSRequest } from "../../../common/request/GSRequest";

import { CreditingPeriodSection } from "./CreditingPeriodSection";

jest.mock("../../../ProtectedComponent", () => {
  return ({ children }: { children: React.ReactNode }) => children;
});

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  useReferenceTypes: () => ({
    allReferenceTypes: {
      Methodology: [{ id: 101, description: "Methodology 1" }],
      FeeSchedule: [
        { id: 1, description: "Cash Fee Schedule" },
        { id: 2, description: "Share of Proceeds" },
      ],
    },
    referenceMethodologies: [{ id: 101, description: "Methodology 1" }],
  }),
  getReferenceValueDescriptionById: jest.fn((id: number, referenceTypes: { id: number; description: string }[]) => {
    return referenceTypes?.find((ref: { id: number; description: string }) => ref.id === id)?.description;
  }),
}));

jest.mock("../../../common/request/GSRequest", () => ({
  ...jest.requireActual("../../../common/request/GSRequest"),
  canRequestTypeViewFeeSchedule: jest.fn((requestTypeCode: string) => {
    const validTypes = [
      "PERFORMANCE_CERTIFICATION_|_PROJECT",
      "PERFORMANCE_CERTIFICATION_|_VPA",
      "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_PROJECT",
      "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_VPA",
      "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_POA",
      "ISSUANCE",
      "GENERIC",
    ];
    return validTypes.includes(requestTypeCode);
  }),
}));

const mockProject: Partial<Project> = {
  id: 1,
  gsid: "GS123",
  name: "Test Project",
  feeSchedule: {
    id: 2,
    code: "SHARE_OF_PROCEEDS",
    description: "Share of Proceeds",
  },
};

const mockRequest: GSRequest = {
  creditingPeriod: {
    periodId: 1,
    startDate: "2023-01-01",
    endDate: "2023-12-31",
  },
  monitoringPeriod: {
    id: 2,
    startDate: "2023-06-01",
    endDate: "2023-11-30",
    feeSchedule: null,
  },
  methodologies: [
    {
      methodologyId: 101,
      methodologyVersion: 1,
      creditingPeriodId: 1,
    },
  ],
  id: 0,
  startDate: "",
  endDate: "",
  bookingDate: "",
  requestType: {
    id: 1005,
    code: "PERFORMANCE_CERTIFICATION_|_PROJECT",
    description: "",
  },
  index: 0,
  reviewName: "",
  sameReviewTypeCount: 0,
  reviewDocumentStatus: 0,
  manuallyAdded: false,
  invoiceId: null,
  projectId: 0,
  project: {
    gsid: "",
    id: 0,
    name: "",
    programmeOfActivitiesId: 0,
    projectStatus: undefined,
  },
  requestStatus: {
    id: 0,
    code: "NOT_REQUESTED",
    description: "",
  },
  currentRoundIndex: null,
  requestRounds: [],
  requestStatusHistory: [],
  inStakeholderConsultation: false,
  emissionReductionAmount: 0,
  batchId: null,
  batchTypeId: null,
  migrated: false,
};

test("renders crediting and monitoring period titles", () => {
  render(<CreditingPeriodSection request={mockRequest} project={mockProject as Project} />);
  expect(screen.getByTestId("crediting-period")).toHaveTextContent("Crediting Period 1 - 01/01/2023 to 31/12/2023");
  expect(screen.getByTestId("monitoring-period")).toHaveTextContent("Monitoring Period 2 - 01/06/2023 to 30/11/2023");
});

test("renders fallback when no crediting period", () => {
  const requestWithoutCreditingPeriod = {
    ...mockRequest,
    creditingPeriod: {
      periodId: null,
      startDate: null,
      endDate: null,
    },
  };
  render(<CreditingPeriodSection request={requestWithoutCreditingPeriod} project={mockProject as Project} />);
  expect(screen.getByText("No matching crediting period found.")).toBeInTheDocument();
});

test("displays monitoring period fee schedule when available", () => {
  const requestWithFeeSchedule = {
    ...mockRequest,
    monitoringPeriod: {
      ...mockRequest.monitoringPeriod,
      feeSchedule: 1,
    },
  };
  render(<CreditingPeriodSection request={requestWithFeeSchedule} project={mockProject as Project} />);
  expect(screen.getByTestId("fee-schedule")).toHaveTextContent("Fee Schedule - Cash Fee Schedule");
});

test("displays project fee schedule when monitoring period fee schedule is not available", () => {
  render(<CreditingPeriodSection request={mockRequest} project={mockProject as Project} />);
  expect(screen.getByTestId("fee-schedule")).toHaveTextContent("Fee Schedule - Share of Proceeds");
});

test("displays 'Not Set' when neither monitoring period nor project has fee schedule", () => {
  const projectWithoutFeeSchedule = { ...mockProject, feeSchedule: undefined };
  render(<CreditingPeriodSection request={mockRequest} project={projectWithoutFeeSchedule as unknown as Project} />);
  expect(screen.getByTestId("fee-schedule")).toHaveTextContent("Fee Schedule - Not Set");
});

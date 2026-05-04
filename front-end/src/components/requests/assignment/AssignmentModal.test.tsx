import { render, screen, waitFor, within } from "@testing-library/react";

import {
  mockAssignments,
  mockAssignmentStatus,
  mockAssignmentTypes,
  mockOverrideTypeAssignments,
  mockRequestTypeAssignments,
} from "../../../common/assignments/DebugAssignments";
import { mockPlatformAccounts } from "../../../common/platform-accounts/DebugPlatformAccounts";
import { debugRequests } from "../../../common/request/DebugRequests";
import { mockActiveReferenceTypes } from "../../../common/data-types/DebugReferenceTypes";
import {
  getRequestAssignments,
  getAllAccountsByAssignmentType,
  getAssignmentTypeByRequestType,
  getAssignment,
} from "../assignment/Assignment";

import { AssignmentModal } from "./AssignmentModal";

jest.mock("../../APIClient/Client", () => ({
  apiClient: {
    makeRequest: jest.fn(),
  },
}));

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../common/data-types/DataReferenceTypes"),
  useReferenceTypes: () => ({
    activeReferenceTypes: {
      AssignmentType: mockAssignmentTypes,
      AssignmentStatus: mockAssignmentStatus,
      RequestStatus: mockActiveReferenceTypes.RequestStatus,
    },
    allReferenceTypes: {
      AssignmentType: mockAssignmentTypes,
      AssignmentStatus: mockAssignmentStatus,
    },
  }),
}));

jest.mock("../assignment/Assignment", () => ({
  getAllAccountsByAssignmentType: jest.fn(),
  getAssignmentTypeByRequestType: jest.fn(),
  getRequestAssignments: jest.fn(),
  getAssignment: jest.fn(),
}));

describe("AssignmentModal", () => {
  beforeEach(() => {
    (getAllAccountsByAssignmentType as jest.Mock).mockResolvedValue(mockPlatformAccounts);
    (getAssignmentTypeByRequestType as jest.Mock).mockResolvedValue([
      { assignmentTypeId: 1, mandatory: true },
      { assignmentTypeId: 2, mandatory: false },
    ]);
    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignments);
    (getAssignment as jest.Mock).mockResolvedValue(mockAssignments[0]);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  const debugRequest = debugRequests[0];
  const modalProps: React.ComponentProps<typeof AssignmentModal> = {
    isOpen: true,
    onClose: jest.fn(),
    requestId: debugRequest.id,
    requestProjectName: debugRequest.project.name,
    requestTypeId: debugRequest.requestType.id,
    requestTypeDescription: debugRequest.requestType.description,
    requestProjectDeveloperName: "Test PD",
    requestStatusId: debugRequest.requestStatus.id,
  };

  it("renders the request type assignments section", async () => {
    render(<AssignmentModal {...modalProps} />);
    expect(await screen.findByText("Request Type Assignments")).toBeInTheDocument();
  });

  it("renders the request type overrides section", async () => {
    render(<AssignmentModal {...modalProps} />);
    expect(await screen.findByText("Override Assignments")).toBeInTheDocument();
  });

  it("displays edit button if request status is not closed", async () => {
    const inProgressRequestStatusProps = { ...modalProps, readonlyOnOpen: true, requestStatusId: 10 };

    render(<AssignmentModal {...inProgressRequestStatusProps} />);

    expect(await screen.findByTestId("edit-assignments-button")).toBeInTheDocument();
  });

  it("should not display edit button if request status is complete", async () => {
    const completedApprovedRequestStatusProps = { ...modalProps, readonlyOnOpen: true, requestStatusId: 4 };

    render(<AssignmentModal {...completedApprovedRequestStatusProps} />);

    await waitFor(() => {
      expect(screen.queryByTestId("edit-assignments-button")).not.toBeInTheDocument();
    });
  });

  it("displays request type assignment descriptions in the correct container", async () => {
    render(<AssignmentModal {...modalProps} />);

    const requestTypeContainer = within(await screen.findByTestId("request-type-assignments"));

    await waitFor(() => {
      mockRequestTypeAssignments.forEach(async (assignment) => {
        const descriptionElements = await requestTypeContainer.findAllByText(assignment.description);
        expect(descriptionElements.length).toBeGreaterThan(0);
      });
    });
  }, 15000);

  it("displays override type assignment descriptions in the correct container", async () => {
    render(<AssignmentModal {...modalProps} />);

    const overrideContainer = within(await screen.findByTestId("override-assignments"));

    await waitFor(() => {
      mockOverrideTypeAssignments.forEach(async (assignment) => {
        const descriptionElements = await overrideContainer.findAllByText(assignment.description);
        expect(descriptionElements.length).toBeGreaterThan(0);
      });
    });
  }, 15000);
});

import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { mockActiveReferenceTypes } from "../../../common/data-types/DebugReferenceTypes";
import { debugInAuditRequests } from "../../../common/request/DebugRequests";
import * as Requests from "../Requests";

import InAuditStatusForm from "./InAuditStatusForm";

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../common/data-types/DataReferenceTypes"),
  getReferenceValueCodeById: jest.fn(),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
  getReferenceValueDescriptionById: jest.fn(),
}));

jest.mock("./../Requests", () => ({
  getAllDocumentsForProjectRequest: jest.fn(),
  getRequiredDocumentsForRequest: jest.fn(),
  updateRequest: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
}));

describe("InAuditStatusForm Component", () => {
  it("Should change to in progress if in round of review 1", async () => {
    const mockOnSubmit = jest.fn();
    const mockRequest = debugInAuditRequests[0];

    render(<InAuditStatusForm request={mockRequest} onSubmit={mockOnSubmit} />);

    fireEvent.click(screen.getByText("Submit"));

    await waitFor(() => {
      const inProgressStatus = mockActiveReferenceTypes.RequestStatus.find((status) => status.code === "IN_PROGRESS");
      if (!inProgressStatus) {
        throw new Error("IN_PROGRESS status not found in mockActiveReferenceTypes");
      }

      expect(Requests.updateRequest).toHaveBeenCalledWith(
        {
          id: mockRequest.id,
          requestStatusId: inProgressStatus.id,
        },
        mockRequest.projectId,
      );
    });
  });

  it("Should change to in progress if completeness and round completion already exist", async () => {
    const mockOnSubmit = jest.fn();
    const mockRequest = debugInAuditRequests[1];

    render(<InAuditStatusForm request={mockRequest} onSubmit={mockOnSubmit} />);

    fireEvent.click(screen.getByText("Submit"));

    await waitFor(() => {
      const inProgressStatus = mockActiveReferenceTypes.RequestStatus.find((status) => status.code === "IN_PROGRESS");
      expect(Requests.updateRequest).toHaveBeenCalledWith(
        {
          id: mockRequest.id,
          requestStatusId: inProgressStatus?.id,
        },
        mockRequest.projectId,
      );
    });
  });

  it("Should handle Round 2 normal journey: In Audit → In Progress → In Round Completion", async () => {
    const mockOnSubmit = jest.fn();
    const normalJourneyRequest = {
      ...debugInAuditRequests[1],
      fastTracked: false,
    };

    render(<InAuditStatusForm request={normalJourneyRequest} onSubmit={mockOnSubmit} />);

    const submitButton = screen.getByText("Submit");
    fireEvent.click(submitButton);

    await waitFor(() => {
      const inProgressStatus = mockActiveReferenceTypes.RequestStatus.find((status) => status.code === "IN_PROGRESS");
      expect(Requests.updateRequest).toHaveBeenCalledWith(
        {
          id: normalJourneyRequest.id,
          requestStatusId: inProgressStatus?.id,
        },
        normalJourneyRequest.projectId,
      );
    });
  });

  it("Should handle Round 2 fast track journey: In Audit → In Round Completion", async () => {
    const mockOnSubmit = jest.fn();

    const fastTrackJourneyRequest = {
      ...debugInAuditRequests[1],
      fastTracked: true,
    };

    render(<InAuditStatusForm request={fastTrackJourneyRequest} onSubmit={mockOnSubmit} />);

    const submitButton = screen.getByText("Submit");
    fireEvent.click(submitButton);

    await waitFor(() => {
      const inRoundCompletionStatus = mockActiveReferenceTypes.RequestStatus.find(
        (status) => status.code === "IN_ROUND_COMPLETION",
      );
      if (!inRoundCompletionStatus) {
        throw new Error("IN_ROUND_COMPLETION status not found in mockActiveReferenceTypes");
      }

      expect(Requests.updateRequest).toHaveBeenCalledWith(
        {
          id: fastTrackJourneyRequest.id,
          requestStatusId: inRoundCompletionStatus.id,
        },
        fastTrackJourneyRequest.projectId,
      );
    });
  });
});

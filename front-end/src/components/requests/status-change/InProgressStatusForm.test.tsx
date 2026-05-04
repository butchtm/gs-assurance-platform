import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { mockAssignmentsHasReview } from "../../../common/assignments/DebugAssignments";
import { mockActiveReferenceTypes } from "../../../common/data-types/DebugReferenceTypes";
import { debugInProgressRequests } from "../../../common/request/DebugRequests";
import { getRequestAssignments } from "../assignment/Assignment";
import { updateRequest } from "../Requests";

import InProgressStatusForm from "./InProgressStatusForm";

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../common/data-types/DataReferenceTypes"),
  getReferenceValueCodeById: jest.fn(),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
  getReferenceValueDescriptionById: jest.fn(),
}));

jest.mock("./../assignment/Assignment", () => ({
  getRequestAssignments: jest.fn(),
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

describe("InProgressStatusForm Component", () => {
  beforeEach(() => {
    (getRequestAssignments as jest.Mock).mockResolvedValue(mockAssignmentsHasReview);
  });

  it("Should change to in round completion if review is complete", async () => {
    const mockOnSubmit = jest.fn();
    render(<InProgressStatusForm request={debugInProgressRequests[0]} onSubmit={mockOnSubmit} />);

    fireEvent.click(screen.getByLabelText("Yes"));

    await waitFor(() => expect(screen.getByText("Submit")).toBeEnabled());

    fireEvent.click(screen.getByText("Submit"));

    await waitFor(() => {
      expect(updateRequest).toHaveBeenCalledWith(
        {
          id: debugInProgressRequests[0].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus[13].id, //In round completion
        },
        debugInProgressRequests[0].projectId,
      );
    });
  });
});

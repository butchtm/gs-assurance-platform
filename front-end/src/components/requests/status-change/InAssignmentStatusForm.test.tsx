import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { mockActiveReferenceTypes } from "../../../common/data-types/DebugReferenceTypes";
import { debugInAssignmentRequests } from "../../../common/request/DebugRequests";
import { MandatoryStatus } from "../MandatoryStatus";
import * as Requests from "../Requests";
import * as Actions from "../actions/Actions";

import InAssignmentStatusForm from "./InAssignmentStatusForm";

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
  checkIfAssignmentTypeRequiredForRequestType: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
}));

jest.mock("../actions/Actions", () => ({
  hasReviewerOrRegistryAssignment: jest.fn(),
}));

jest.mock("../assignment/Assignment", () => ({
  getRequestAssignments: jest.fn(),
}));

describe("InAssignmentStatusForm Component", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (Actions.hasReviewerOrRegistryAssignment as jest.Mock).mockReturnValue(true);
  });

  it("Should change to IN ROUND COMPLETION if review is not required", async () => {
    (Requests.checkIfAssignmentTypeRequiredForRequestType as jest.Mock).mockResolvedValue(MandatoryStatus.NOT_REQUIRED);
    (Actions.hasReviewerOrRegistryAssignment as jest.Mock).mockReturnValue(true);

    render(<InAssignmentStatusForm request={debugInAssignmentRequests[0]} onSubmit={jest.fn()} />);

    fireEvent.click(screen.getByLabelText("Yes"));

    await waitFor(() => {
      expect(screen.getByText("Submit")).not.toHaveAttribute("disabled");
    });

    fireEvent.click(screen.getByText("Submit"));

    await waitFor(() => {
      expect(Requests.updateRequest).toHaveBeenCalledWith(
        {
          id: debugInAssignmentRequests[0].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus.find(
            (requestStatus) => requestStatus.code == "IN_ROUND_COMPLETION",
          )?.id,
        },
        debugInAssignmentRequests[0].projectId,
      );
    });
  });

  it("Should change to IN PROGRESS if review is mandatory", async () => {
    (Requests.checkIfAssignmentTypeRequiredForRequestType as jest.Mock).mockResolvedValue(MandatoryStatus.REQUIRED);
    (Actions.hasReviewerOrRegistryAssignment as jest.Mock).mockReturnValue(true);

    render(<InAssignmentStatusForm request={debugInAssignmentRequests[0]} onSubmit={jest.fn()} />);

    fireEvent.click(screen.getByLabelText("Yes"));

    await waitFor(() => {
      expect(screen.getByText("Submit")).not.toHaveAttribute("disabled");
    });

    fireEvent.click(screen.getByText("Submit"));

    await waitFor(() => {
      expect(Requests.updateRequest).toHaveBeenCalledWith(
        {
          id: debugInAssignmentRequests[0].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus.find(
            (requestStatus) => requestStatus.code == "IN_PROGRESS",
          )?.id,
        },
        debugInAssignmentRequests[0].projectId,
      );
    });
  });

  it("Should change to IN PROGRESS if review is optional and review selected", async () => {
    (Requests.checkIfAssignmentTypeRequiredForRequestType as jest.Mock).mockResolvedValue(MandatoryStatus.OPTIONAL);
    (Actions.hasReviewerOrRegistryAssignment as jest.Mock).mockReturnValue(true);

    render(<InAssignmentStatusForm request={debugInAssignmentRequests[0]} onSubmit={jest.fn()} />);

    fireEvent.click(screen.getByLabelText("Yes"));

    const submitReviewYesBtn = await waitFor(() => screen.getByTestId("submit-for-review-yes"));

    await waitFor(() => {
      fireEvent.click(submitReviewYesBtn);
      fireEvent.click(screen.getByText("Submit"));

      expect(Requests.updateRequest).toHaveBeenCalledWith(
        {
          id: debugInAssignmentRequests[0].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus.find(
            (requestStatus) => requestStatus.code == "IN_PROGRESS",
          )?.id,
        },
        debugInAssignmentRequests[0].projectId,
      );
    });
  });

  it("Should change to ROUND COMPLETION if review is optional and no review selected", async () => {
    (Requests.checkIfAssignmentTypeRequiredForRequestType as jest.Mock).mockResolvedValue(MandatoryStatus.OPTIONAL);
    (Actions.hasReviewerOrRegistryAssignment as jest.Mock).mockReturnValue(true);

    render(<InAssignmentStatusForm request={debugInAssignmentRequests[0]} onSubmit={jest.fn()} />);

    fireEvent.click(screen.getByLabelText("Yes"));
    await screen.findByTestId("submit-for-review-no");
    fireEvent.click(screen.getByText("Submit"));

    await waitFor(() => {
      expect(Requests.updateRequest).toHaveBeenCalledWith(
        {
          id: debugInAssignmentRequests[0].id,
          requestStatusId: mockActiveReferenceTypes.RequestStatus.find(
            (requestStatus) => requestStatus.code == "IN_ROUND_COMPLETION",
          )?.id,
        },
        debugInAssignmentRequests[0].projectId,
      );
    });
  });

  it("Should not allow submit if has everything has been assigned is equal to no", async () => {
    render(<InAssignmentStatusForm request={debugInAssignmentRequests[0]} onSubmit={jest.fn()} />);

    fireEvent.click(screen.getByLabelText("No"));
    await waitFor(() => {
      expect(screen.getByText("Submit")).toHaveAttribute("disabled");
    });
  });

  it("Should disable submit button and hide submit-for-review options when no reviewer is assigned", async () => {
    (Requests.checkIfAssignmentTypeRequiredForRequestType as jest.Mock).mockResolvedValue(MandatoryStatus.REQUIRED);
    (Actions.hasReviewerOrRegistryAssignment as jest.Mock).mockReturnValue(false);

    render(<InAssignmentStatusForm request={debugInAssignmentRequests[0]} onSubmit={jest.fn()} />);

    fireEvent.click(screen.getByLabelText("Yes"));

    await waitFor(() => {
      expect(screen.getByText("Submit")).toBeDisabled();
      expect(screen.queryByTestId("submit-for-review-yes")).not.toBeInTheDocument();
      expect(screen.queryByTestId("submit-for-review-no")).not.toBeInTheDocument();
      expect(screen.getByTestId("reviewer-assignment-warning")).toBeInTheDocument();
    });
  });

  it("should allow submit button if review is optional and one is not assigned", async () => {
    (Requests.checkIfAssignmentTypeRequiredForRequestType as jest.Mock).mockResolvedValue(MandatoryStatus.OPTIONAL);
    (Actions.hasReviewerOrRegistryAssignment as jest.Mock).mockReturnValue(false);

    render(<InAssignmentStatusForm request={debugInAssignmentRequests[0]} onSubmit={jest.fn()} />);

    fireEvent.click(screen.getByLabelText("Yes"));

    await waitFor(() => {
      expect(screen.getByText("Submit")).not.toBeDisabled();
      expect(screen.getByTestId("submit-for-review-yes")).toBeInTheDocument();
      expect(screen.getByTestId("submit-for-review-no")).toBeInTheDocument();
    });
  });

  it("should disable submit button if review is optional but they request a review and one is not assigned", async () => {
    (Requests.checkIfAssignmentTypeRequiredForRequestType as jest.Mock).mockResolvedValue(MandatoryStatus.OPTIONAL);
    (Actions.hasReviewerOrRegistryAssignment as jest.Mock).mockReturnValue(false);

    render(<InAssignmentStatusForm request={debugInAssignmentRequests[0]} onSubmit={jest.fn()} />);

    fireEvent.click(screen.getByLabelText("Yes"));

    await waitFor(() => {
      expect(screen.getByTestId("submit-for-review-yes")).toBeInTheDocument();
      expect(screen.getByTestId("submit-for-review-no")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByTestId("submit-for-review-yes"));

    await waitFor(() => {
      expect(screen.getByText("Submit")).toBeDisabled();
      expect(screen.getByTestId("reviewer-assignment-warning")).toBeInTheDocument();
    });
  });
});

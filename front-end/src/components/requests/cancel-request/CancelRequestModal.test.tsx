import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { mockActiveReferenceTypes } from "../../../common/data-types/DebugReferenceTypes";
import { debugRequests } from "../../../common/request/DebugRequests";
import * as Requests from "../Requests";

import { CancelRequestModal } from "./CancelRequestModal";

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

describe("CancelRequestModal Component", () => {
  it("should display the correct status options", () => {
    render(
      <CancelRequestModal
        request={debugRequests[0]}
        isOpen={true}
        onClose={jest.fn()}
        updateRequestRefresh={jest.fn()}
      />,
    );

    fireEvent.mouseDown(screen.getByLabelText("Request Status"));
    expect(screen.getByText("Cancelled")).toBeInTheDocument();
    expect(screen.getByText("Omitted")).toBeInTheDocument();
  });

  it("should display the correct reason options", () => {
    render(
      <CancelRequestModal
        request={debugRequests[0]}
        isOpen={true}
        onClose={jest.fn()}
        updateRequestRefresh={jest.fn()}
      />,
    );

    fireEvent.mouseDown(screen.getByLabelText("Reason For Request Closure"));
    expect(screen.getByText("Reason 1")).toBeInTheDocument();
    expect(screen.getByText("Reason 2")).toBeInTheDocument();
  });

  it("should disable submit button when no value is selected", () => {
    render(
      <CancelRequestModal
        request={debugRequests[0]}
        isOpen={true}
        onClose={jest.fn()}
        updateRequestRefresh={jest.fn()}
      />,
    );

    const submitButton = screen.getByRole("button", { name: "Close Request" });
    expect(submitButton).toBeDisabled();
  });

  it("should disable submit when other selected but no text provided", () => {
    render(
      <CancelRequestModal
        request={debugRequests[0]}
        isOpen={true}
        onClose={jest.fn()}
        updateRequestRefresh={jest.fn()}
      />,
    );

    fireEvent.mouseDown(screen.getByLabelText("Request Status"));
    fireEvent.click(screen.getByText("Cancelled"));
    fireEvent.mouseDown(screen.getByLabelText("Reason For Request Closure"));
    fireEvent.click(screen.getByText("Other"));

    const submitButton = screen.getByText("Mark as Cancelled");
    expect(submitButton).toBeDisabled();
  });

  it("should enable submit button and change text when value is selected", () => {
    render(
      <CancelRequestModal
        request={debugRequests[0]}
        isOpen={true}
        onClose={jest.fn()}
        updateRequestRefresh={jest.fn()}
      />,
    );

    fireEvent.mouseDown(screen.getByLabelText("Request Status"));
    fireEvent.click(screen.getByText("Cancelled"));
    fireEvent.mouseDown(screen.getByLabelText("Reason For Request Closure"));
    fireEvent.click(screen.getByText("Reason 1"));

    const submitButton = screen.getByText("Mark as Cancelled");
    expect(submitButton).not.toBeDisabled();
  });

  it("should call updateRequest with correct arguments on form submit", async () => {
    const mockOnClose = jest.fn();
    const mockUpdateRequestRefresh = jest.fn();
    render(
      <CancelRequestModal
        isOpen={true}
        onClose={mockOnClose}
        updateRequestRefresh={mockUpdateRequestRefresh}
        request={debugRequests[1]}
      />,
    );

    fireEvent.mouseDown(screen.getByLabelText("Request Status"));
    fireEvent.click(screen.getByText("Cancelled"));
    fireEvent.mouseDown(screen.getByLabelText("Reason For Request Closure"));
    fireEvent.click(screen.getByText("Other"));
    const otherReason = screen.getByLabelText("Other Reason");
    fireEvent.change(otherReason, { target: { value: "This is the other reason" } });

    const submitButton = screen.getByTestId("close-request-button");
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText("Confirm Request Closure")).toBeInTheDocument();
    });

    const confirmButton = screen.getByTestId("confirm-close-request-button");
    fireEvent.click(confirmButton);

    await waitFor(() => {
      expect(Requests.updateRequest).toHaveBeenCalledWith(
        {
          id: 1,
          requestStatusId: mockActiveReferenceTypes.RequestStatus[8].id,
          manuallyAdded: true,
          cancellationReasonId: "3",
          otherCancellationReason: "This is the other reason",
        },
        3,
      );
    });
  });
});

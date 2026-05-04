import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { debugRequestComments } from "../../../../common/request/DebugRequests";
import { mockActiveReferenceTypes } from "../../../../common/data-types/DebugReferenceTypes";

import { CommentModifyAction } from "./CommentModifyAction";

jest.mock("../../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../../common/data-types/DataReferenceTypes"),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
}));

describe("CommentModifyAction", () => {
  const mockUpdateRequest = jest.fn();
  const mockComment = debugRequestComments[0];

  it("renders the edit button", async () => {
    render(<CommentModifyAction comment={mockComment} updateRequest={mockUpdateRequest} />);
    await waitFor(() => {
      expect(screen.getByTestId("comment-edit-icon")).toBeInTheDocument();
    });
  });

  it("opens the modal when the edit button is clicked", async () => {
    render(<CommentModifyAction comment={mockComment} updateRequest={mockUpdateRequest} />);
    await waitFor(() => {
      expect(screen.getByTestId("comment-edit-action")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByTestId("comment-edit-action"));

    await waitFor(() => {
      expect(screen.getByText("Moderate Comment")).toBeInTheDocument();
    });
  });

  it("calls updateRequest when the modal is closed", async () => {
    render(<CommentModifyAction comment={mockComment} updateRequest={mockUpdateRequest} />);
    await waitFor(() => {
      fireEvent.click(screen.getByTestId("comment-edit-action"));
      fireEvent.click(screen.getByTestId("modal-close-icon"));
      expect(mockUpdateRequest).toHaveBeenCalled();
    });
  });
});

import { fireEvent, render, screen, waitFor, within } from "@testing-library/react";
import { toast } from "react-toastify";

import { mockActiveReferenceTypes } from "../../../common/data-types/DebugReferenceTypes";

import { RequestCommentConfirmDeletion } from "./RequestCommentConfirmDeletion";
import { deleteRequestComment } from "./RequestComments";

const mockClose = jest.fn();

jest.mock("./RequestComments", () => ({
  updateRequestComment: jest.fn(),
  deleteRequestComment: jest.fn(),
}));

jest.mock("react-toastify", () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
}));

describe("RequestCommentConfirmDeletion", () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  it("renders correctly with reasons dropdown", () => {
    render(<RequestCommentConfirmDeletion open={true} onClose={mockClose} commentId={123} />);

    expect(screen.getByText("Confirm Deletion")).toBeInTheDocument();
  });

  it("handles selection and deletion of a comment", async () => {
    render(<RequestCommentConfirmDeletion open={true} onClose={mockClose} commentId={123} />);

    const autocomplete = screen.getByTestId("select-delete-reason");
    const input = within(autocomplete).getByRole("combobox");
    autocomplete.focus();

    fireEvent.change(input, { target: { value: "reason" } });
    fireEvent.keyDown(autocomplete, { key: "ArrowDown" });
    fireEvent.keyDown(autocomplete, { key: "Enter" });

    fireEvent.click(screen.getByRole("button", { name: "Delete" }));

    await waitFor(() => {
      expect(deleteRequestComment).toHaveBeenCalledWith(123, 1);
      expect(toast.success).toHaveBeenCalledWith("Comment deleted successfully.");
      expect(mockClose).toHaveBeenCalled();
    });
  });
});

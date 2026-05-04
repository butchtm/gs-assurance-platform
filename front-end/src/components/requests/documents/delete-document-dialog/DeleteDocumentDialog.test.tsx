import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { toast } from "react-toastify";

import { debugRequests } from "../../../../common/request/DebugRequests";
import { mockDocuments } from "../../../../common/document/DebugDocuments";

import DeleteDocumentDialog from "./DeleteDocumentDialog";
import { deleteDocument } from "./DeleteRequestDocument";

jest.mock("react-toastify", () => ({
  toast: {
    info: jest.fn(() => "toast-id"),
    update: jest.fn(),
    TYPE: {
      SUCCESS: "success",
      ERROR: "error",
    },
  },
}));

jest.mock("./DeleteRequestDocument", () => ({
  deleteDocument: jest.fn(),
}));

describe("DeleteDocumentDialog", () => {
  it("shows success toast when document is deleted", async () => {
    (deleteDocument as jest.Mock).mockResolvedValue({ success: true });

    render(
      <DeleteDocumentDialog isOpen={true} request={debugRequests[0]} document={mockDocuments[0]} onClose={jest.fn()} />,
    );

    fireEvent.click(screen.getByRole("button", { name: "Delete" }));

    await waitFor(() => {
      expect(toast.info).toHaveBeenCalledWith("Deleting document...", expect.any(Object));
      expect(toast.update).toHaveBeenCalledWith("toast-id", {
        render: "Document has been deleted successfully",
        type: "success",
        autoClose: 3000,
        closeButton: true,
      });
    });
  });

  it("shows error toast when document deletion fails", async () => {
    (deleteDocument as jest.Mock).mockResolvedValue({
      success: false,
      errorMessage: "Server error",
    });

    render(
      <DeleteDocumentDialog isOpen={true} request={debugRequests[0]} document={mockDocuments[0]} onClose={jest.fn()} />,
    );

    fireEvent.click(screen.getByRole("button", { name: "Delete" }));

    await waitFor(() => {
      expect(toast.info).toHaveBeenCalledWith("Deleting document...", expect.any(Object));
      expect(toast.update).toHaveBeenCalledWith("toast-id", {
        render: "Failed to delete document: Server error",
        type: "error",
        autoClose: 5000,
        closeButton: true,
      });
    });
  });
});

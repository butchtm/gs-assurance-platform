import "@testing-library/jest-dom";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { toast } from "react-toastify";

import { debugRequests } from "../../../../common/request/DebugRequests";
import { createInvoice } from "../../../Invoice/Invoice";

import { CreateInvoiceAction } from "./CreateInvoiceAction";
import { UpdateInvoiceStatusAction } from "./UpdateInvoiceStatusAction";

jest.mock("react-toastify", () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

jest.mock("../../../../common/data-types/DataReferenceTypes", () => ({
  useReferenceTypes: () => ({
    activeReferenceTypes: {
      InvoiceStatus: [{ code: "NOT_TRIGGERED", id: 0 }],
    },
  }),
}));

jest.mock("../../../Invoice/Invoice", () => ({
  createInvoice: jest.fn(),
  getInvoice: jest.fn(),
}));

const mockUpdateRequest = jest.fn();

describe("CreateInvoiceAction", () => {
  it("renders invoice create button correctly", () => {
    render(<CreateInvoiceAction request={debugRequests[0]} updateRequest={mockUpdateRequest} />);
    const createButton = screen.getByText("Create Invoice");
    expect(createButton).toBeInTheDocument();
  });

  it("creates invoice when clicked", async () => {
    (createInvoice as jest.Mock).mockResolvedValueOnce({});

    render(<CreateInvoiceAction request={debugRequests[0]} updateRequest={mockUpdateRequest} />);
    const createButton = screen.getByText("Create Invoice");

    fireEvent.click(createButton);

    await waitFor(() => {
      expect(createInvoice).toHaveBeenCalledWith({
        requestId: 35562,
        invoiceStatusId: 0,
        amount: 0,
      });
      expect(mockUpdateRequest).toHaveBeenCalled();
      expect(toast.success).toHaveBeenCalledWith("Invoice Created Successfully");
    });
  });

  it("handles invoice creation failure", async () => {
    (createInvoice as jest.Mock).mockRejectedValueOnce(new Error("Creation failed"));

    render(<CreateInvoiceAction request={debugRequests[0]} updateRequest={mockUpdateRequest} />);
    const createButton = screen.getByText("Create Invoice");

    fireEvent.click(createButton);

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith("Failed to create invoice");
      expect(mockUpdateRequest).toHaveBeenCalled();
    });
  });
});

describe("UpdateInvoiceStatusAction", () => {
  it("renders invoice update button correctly", () => {
    render(<UpdateInvoiceStatusAction request={debugRequests[1]} updateRequest={mockUpdateRequest} />);
    const updateButton = screen.getByText("Update Invoice Status");
    expect(updateButton).toBeInTheDocument();
  });

  it("opens invoice update modal when clicked", () => {
    render(<UpdateInvoiceStatusAction request={debugRequests[1]} updateRequest={mockUpdateRequest} />);
    const updateButton = screen.getByText("Update Invoice Status");

    fireEvent.click(updateButton);

    const modal = screen.getByText("Save") && screen.getByText("Cancel");
    expect(modal).toBeInTheDocument();
  });

  it("updates request when modal is closed with a status", () => {
    render(<UpdateInvoiceStatusAction request={debugRequests[1]} updateRequest={mockUpdateRequest} />);

    const updateButton = screen.getByText("Update Invoice Status");
    fireEvent.click(updateButton);

    const modalCloseButton = screen.getByText("Cancel");
    fireEvent.click(modalCloseButton);

    expect(mockUpdateRequest).toHaveBeenCalled();
  });
});

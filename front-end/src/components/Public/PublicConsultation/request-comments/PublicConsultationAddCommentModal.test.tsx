import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { toast } from "react-toastify";

import { highProbability, mediumProbability, publicAnalyseText } from "../../../../common/piiAnalysis/PiiAnalysis";
import * as RequestComments from "../../../requests/comments/RequestComments";

import PublicConsultationAddCommentModal from "./PublicConsultationAddCommentModal";

jest.mock("react-toastify", () => ({
  toast: {
    error: jest.fn(),
    success: jest.fn(),
  },
}));

jest.mock("../../../requests/comments/RequestComments", () => ({
  publicCreateRequestComment: jest.fn(() => Promise.resolve()),
}));

jest.mock("../../../../common/piiAnalysis/PiiAnalysis", () => ({
  ...jest.requireActual("../../../../common/piiAnalysis/PiiAnalysis"),
  publicAnalyseText: jest.fn(),
}));

const mockPublicAnalyseText = publicAnalyseText as jest.MockedFunction<typeof publicAnalyseText>;

describe("PublicConsultationAddCommentModal", () => {
  const mockOnClose = jest.fn();
  const props = {
    requestId: 1,
    isOpen: true,
    onClose: mockOnClose,
  };

  beforeEach(() => {
    jest.clearAllMocks();
    render(<PublicConsultationAddCommentModal {...props} />);
    mockPublicAnalyseText.mockResolvedValue({
      probablePii: [
        { entity: "Name", probability: "HIGH", identifiedString: "pii name" },
        { entity: "Address", probability: "MEDIUM", identifiedString: "pii address" },
      ],
    });
  });

  it("should render the modal when isOpen is true", () => {
    expect(screen.getByTestId("modal-close-icon")).toBeInTheDocument();
  });

  it("should close the modal on clicking the close button", () => {
    fireEvent.click(screen.getByTestId("modal-close-icon"));
    expect(mockOnClose).toHaveBeenCalled();
  });

  it("should update input fields correctly", () => {
    const nameInput: HTMLInputElement = screen.getByLabelText(/Name/i);
    const emailInput: HTMLInputElement = screen.getByLabelText(/Email/i);
    const commentInput: HTMLInputElement = screen.getByTestId("CommentTextField");

    fireEvent.change(nameInput, { target: { value: "John Doe" } });
    fireEvent.change(emailInput, { target: { value: "john@example.com" } });
    fireEvent.change(commentInput, { target: { value: "This is a test comment." } });

    expect(nameInput.value).toBe("John Doe");
    expect(emailInput.value).toBe("john@example.com");
    expect(commentInput.value).toBe("This is a test comment.");
  });

  it("should should not allow to check for PII if the email is invalid", () => {
    fireEvent.change(screen.getByLabelText(/Name/i), { target: { value: "test name" } });
    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: "test invalid email" } });
    fireEvent.change(screen.getByTestId("CommentTextField"), {
      target: { value: "test comment" },
    });
    expect(screen.getByRole("button", { name: /Check For PII/i })).toBeDisabled();
    expect(screen.getByTestId("modal-close-icon")).toBeInTheDocument();
  });

  it("should not allow to check for PII if name is missing", () => {
    fireEvent.change(screen.getByLabelText(/Name/i), { target: { value: "" } });
    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: "test@email.com" } });
    fireEvent.change(screen.getByTestId("CommentTextField"), {
      target: { value: "test comment" },
    });
    expect(screen.getByRole("button", { name: /Check For PII/i })).toBeDisabled();
    expect(screen.getByTestId("modal-close-icon")).toBeInTheDocument();
  });

  it("should not allow to check for PII if comment is missing", () => {
    fireEvent.change(screen.getByLabelText(/Name/i), { target: { value: "test name" } });
    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: "test@email.com" } });
    fireEvent.change(screen.getByTestId("CommentTextField"), {
      target: { value: "" },
    });
    expect(screen.getByRole("button", { name: /Check For PII/i })).toBeDisabled();
    expect(screen.getByTestId("modal-close-icon")).toBeInTheDocument();
  });

  it("should disable the submit button before the user has checked for PII", () => {
    fireEvent.change(screen.getByLabelText(/Name/i), { target: { value: "test name" } });
    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: "test@email.com" } });

    fireEvent.change(screen.getByTestId("CommentTextField"), {
      target: { value: "test comment" },
    });
    expect(screen.getByRole("button", { name: /Submit Comment/i })).toBeDisabled();
  });

  it("should call the endpoint to analyse the comment for PII and display potential PII", async () => {
    fireEvent.change(screen.getByLabelText(/Name/i), { target: { value: "test name" } });
    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: "test@email.com" } });

    fireEvent.change(screen.getByTestId("CommentTextField"), {
      target: { value: "test comment pii name, pii address" },
    });

    fireEvent.click(screen.getByRole("button", { name: /Check For PII/i }));
    expect(mockPublicAnalyseText).toHaveBeenCalled();
    await waitFor(() => {
      expect(screen.getByText("Identified Potential PII")).toBeInTheDocument();
    });
    expect(screen.getByText("pii name")).toHaveStyle("color: " + highProbability.toString());
    expect(screen.getByText("pii address")).toHaveStyle("color: " + mediumProbability.toString());
  });

  it("should allow submission if no potential PII identified", async () => {
    fireEvent.change(screen.getByLabelText(/Name/i), { target: { value: "test name" } });
    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: "test@email.com" } });

    fireEvent.change(screen.getByTestId("CommentTextField"), {
      target: { value: "test comment" },
    });

    mockPublicAnalyseText.mockReset();
    mockPublicAnalyseText.mockResolvedValue({
      probablePii: [],
    });
    fireEvent.click(screen.getByRole("button", { name: /Check For PII/i }));
    expect(mockPublicAnalyseText).toHaveBeenCalled();
    await waitFor(() => {
      expect(screen.getByText("No PII identified")).toBeInTheDocument();
    });
    expect(screen.getByRole("button", { name: /Submit Comment/i })).not.toBeDisabled();
  });

  it("should allow submission after PII checked and understood", async () => {
    fireEvent.change(screen.getByLabelText(/Name/i), { target: { value: "test name" } });
    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: "test@email.com" } });

    fireEvent.change(screen.getByTestId("CommentTextField"), {
      target: { value: "test comment pii name, pii address" },
    });

    fireEvent.click(screen.getByRole("button", { name: /Check For PII/i }));
    expect(mockPublicAnalyseText).toHaveBeenCalled();
    await waitFor(() => {
      expect(screen.getByText("Identified Potential PII")).toBeInTheDocument();
    });
    fireEvent.click(screen.getByTestId("understands-pii-checkbox"));
    fireEvent.click(screen.getByRole("button", { name: /Submit Comment/i }));
    await waitFor(() => {
      expect(RequestComments.publicCreateRequestComment as jest.Mock).toHaveBeenCalled();
      expect(toast.success).toHaveBeenCalledWith("Comment added successfully, it will show publicly upon review.");
    });
  });

  it("should close the modal after submitting the comment", async () => {
    fireEvent.change(screen.getByLabelText(/Name/i), { target: { value: "test name" } });
    fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: "test@example.com" } });
    fireEvent.change(screen.getByTestId("CommentTextField"), {
      target: { value: "test comment" },
    });

    fireEvent.click(screen.getByRole("button", { name: /Check For PII/i }));
    expect(mockPublicAnalyseText).toHaveBeenCalled();
    await waitFor(() => {
      expect(screen.getByText("Identified Potential PII")).toBeInTheDocument();
    });
    fireEvent.click(screen.getByTestId("understands-pii-checkbox"));
    fireEvent.click(screen.getByRole("button", { name: /Submit Comment/i }));
    await waitFor(() => {
      expect(RequestComments.publicCreateRequestComment as jest.Mock).toHaveBeenCalled();
      expect(mockOnClose).toHaveBeenCalled();
    });
  });
});

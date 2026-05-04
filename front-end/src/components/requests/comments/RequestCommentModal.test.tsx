import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { toast } from "react-toastify";

import { analyseText, highProbability, mediumProbability } from "../../../common/piiAnalysis/PiiAnalysis";
import { mockPlatformAccounts } from "../../../common/platform-accounts/DebugPlatformAccounts";
import { debugRequests } from "../../../common/request/DebugRequests";

import RequestCommentModal from "./RequestCommentModal";
import { createRequestComment } from "./RequestComments";

jest.mock("./RequestComments", () => ({
  createRequestComment: jest.fn(),
}));

jest.mock("react-toastify", () => ({
  toast: {
    success: jest.fn(),
  },
}));

jest.mock("../../../common/piiAnalysis/PiiAnalysis", () => ({
  ...jest.requireActual("../../../common/piiAnalysis/PiiAnalysis"),
  analyseText: jest.fn(),
}));

const mockAnalyseText = analyseText as jest.MockedFunction<typeof analyseText>;
const probablePii = {
  probablePii: [
    { entity: "Name", probability: "HIGH", identifiedString: "pii name" },
    { entity: "Address", probability: "MEDIUM", identifiedString: "pii address" },
  ],
};

beforeEach(() => {
  mockAnalyseText.mockResolvedValue(probablePii);
});

describe("RequestCommentModal", () => {
  it("Renders the modal when isOpen is true and closes when cancel is clicked", async () => {
    const handleClose = jest.fn();

    render(
      <RequestCommentModal
        isOpen={true}
        account={mockPlatformAccounts[0]}
        onClose={handleClose}
        request={debugRequests[0]}
      />,
    );

    await userEvent.click(screen.getByTestId("modal-cancel-button"));
    expect(handleClose).toHaveBeenCalled();
  });

  it("should disable the submit button before the user has checked for PII", () => {
    render(
      <RequestCommentModal
        account={mockPlatformAccounts[0]}
        isOpen={true}
        onClose={jest.fn()}
        request={debugRequests[0]}
      />,
    );

    fireEvent.change(screen.getByTestId("comment-input"), {
      target: { value: "test comment" },
    });
    expect(screen.getByRole("button", { name: /Submit Comment/i })).toBeDisabled();
  });

  it("should call the endpoint to analyse the comment for PII and display potential PII", async () => {
    render(
      <RequestCommentModal
        account={mockPlatformAccounts[0]}
        isOpen={true}
        onClose={jest.fn()}
        request={debugRequests[0]}
      />,
    );

    fireEvent.change(screen.getByTestId("comment-input"), {
      target: { value: "test comment pii name, pii address" },
    });

    fireEvent.click(screen.getByRole("button", { name: /Check For PII/i }));
    expect(mockAnalyseText).toHaveBeenCalled();
    await waitFor(() => {
      expect(screen.getByText("Identified Potential PII")).toBeInTheDocument();
    });
    expect(screen.getByText("pii name")).toHaveStyle("color: " + highProbability.toString());
    expect(screen.getByText("pii address")).toHaveStyle("color: " + mediumProbability.toString());
  });

  it("should allow submission if no potential PII identified", async () => {
    render(
      <RequestCommentModal
        account={mockPlatformAccounts[0]}
        isOpen={true}
        onClose={jest.fn()}
        request={debugRequests[0]}
      />,
    );

    fireEvent.change(screen.getByTestId("comment-input"), {
      target: { value: "test comment" },
    });

    mockAnalyseText.mockReset();
    mockAnalyseText.mockResolvedValue({
      probablePii: [],
    });
    fireEvent.click(screen.getByRole("button", { name: /Check For PII/i }));
    expect(mockAnalyseText).toHaveBeenCalled();
    await waitFor(() => {
      expect(screen.getByText("No PII identified")).toBeInTheDocument();
    });
    expect(screen.getByRole("button", { name: /Submit Comment/i })).not.toBeDisabled();
  });

  it("should allow submission after PII checked and understood", async () => {
    render(
      <RequestCommentModal
        account={mockPlatformAccounts[0]}
        isOpen={true}
        onClose={jest.fn()}
        request={debugRequests[0]}
      />,
    );

    fireEvent.change(screen.getByTestId("comment-input"), {
      target: { value: "test comment pii name, pii address" },
    });

    fireEvent.click(screen.getByRole("button", { name: /Check For PII/i }));
    expect(mockAnalyseText).toHaveBeenCalled();
    await waitFor(() => {
      expect(screen.getByText("Identified Potential PII")).toBeInTheDocument();
    });
    fireEvent.click(screen.getByTestId("understands-pii-checkbox"));
    fireEvent.click(screen.getByRole("button", { name: /Submit Comment/i }));
    await waitFor(() => {
      expect(createRequestComment).toHaveBeenCalledWith({
        accountId: 1,
        anonymous: false,
        comment: "test comment pii name, pii address",
        publicAccess: false,
        requestId: 35562,
        textAnalysisResults: probablePii,
      });
    });
    expect(toast.success).toHaveBeenCalledWith("Commented Successfully");
  });

  it("Anonymous checkbox toggles the boolean", async () => {
    render(
      <RequestCommentModal
        account={mockPlatformAccounts[0]}
        isOpen={true}
        onClose={jest.fn()}
        request={debugRequests[0]}
      />,
    );

    await userEvent.click(screen.getByTestId("comment-anonymously"));
    expect(screen.getByRole("checkbox")).toHaveProperty("checked", true);
    fireEvent.change(screen.getByTestId("comment-input"), {
      target: { value: "test comment pii name, pii address" },
    });

    fireEvent.click(screen.getByRole("button", { name: /Check For PII/i }));
    expect(mockAnalyseText).toHaveBeenCalled();
    await waitFor(() => {
      expect(screen.getByText("Identified Potential PII")).toBeInTheDocument();
    });
    fireEvent.click(screen.getByTestId("understands-pii-checkbox"));
    userEvent.click(await screen.findByText("Submit Comment"));

    await waitFor(() => {
      expect(createRequestComment).toHaveBeenCalledWith({
        accountId: 1,
        anonymous: true,
        comment: "test comment pii name, pii address",
        publicAccess: false,
        requestId: 35562,
        textAnalysisResults: probablePii,
      });
    });
  });
});

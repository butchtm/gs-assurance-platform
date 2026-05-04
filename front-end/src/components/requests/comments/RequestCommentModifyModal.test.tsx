import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { mockActiveReferenceTypes } from "../../../common/data-types/DebugReferenceTypes";
import { PiiActionCode } from "../../../common/piiAnalysis/PiiAction";
import { debugRequestComments } from "../../../common/request/DebugRequests";

import RequestCommentModifyModal from "./RequestCommentModifyModal";
import { deleteRequestComment, updateRequestComment } from "./RequestComments";

jest.mock("./RequestComments", () => ({
  updateRequestComment: jest.fn(),
  deleteRequestComment: jest.fn(),
}));

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../common/data-types/DataReferenceTypes"),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
}));

describe("RequestCommentModifyModal", () => {
  it("renders the modal when isOpen is true", async () => {
    const handleClose = jest.fn();
    render(<RequestCommentModifyModal isOpen={true} onClose={handleClose} comment={debugRequestComments[0]} />);
    await waitFor(() => {
      expect(screen.getByText("Moderate Comment")).toBeInTheDocument();
    });
  });

  it("closes when the close icon is clicked", async () => {
    const handleClose = jest.fn();
    render(<RequestCommentModifyModal isOpen={true} onClose={handleClose} comment={debugRequestComments[0]} />);

    await waitFor(() => {
      fireEvent.click(screen.getByTestId("modal-close-icon"));
      expect(handleClose).toHaveBeenCalled();
    });
  });

  it("calls updateRequestComment when the update button is clicked", async () => {
    const handleClose = jest.fn();
    (updateRequestComment as jest.Mock).mockResolvedValue({});

    render(<RequestCommentModifyModal isOpen={true} onClose={handleClose} comment={debugRequestComments[0]} />);

    fireEvent.click(screen.getByTestId("update-comment"));

    await waitFor(() =>
      expect(updateRequestComment).toHaveBeenCalledWith({
        id: debugRequestComments[0].id,
        publicAccess: debugRequestComments[0].publicAccess,
        anonymous: debugRequestComments[0].anonymous,
        addressed: debugRequestComments[0].addressed,
        suppressed: debugRequestComments[0].suppressed,
      }),
    );
  });

  it("opens confirmation dialog and deletes the comment on confirm", () => {
    const handleClose = jest.fn();
    (deleteRequestComment as jest.Mock).mockResolvedValue({});

    render(<RequestCommentModifyModal isOpen={true} onClose={handleClose} comment={debugRequestComments[0]} />);

    fireEvent.click(screen.getByTestId("delete-comment"));
    expect(screen.getByText("Confirm Deletion")).toBeInTheDocument();
  });

  it("renders pii actions when pii is present", async () => {
    const handleClose = jest.fn();
    render(<RequestCommentModifyModal isOpen={true} onClose={handleClose} comment={debugRequestComments[0]} />);
    await waitFor(() => {
      expect(screen.getByText("PII Actions")).toBeInTheDocument();
      expect(screen.getByText("Current Action:")).toBeInTheDocument();
    });
  });

  it("does not render pii actions when pii is not present", async () => {
    const handleClose = jest.fn();
    render(<RequestCommentModifyModal isOpen={true} onClose={handleClose} comment={debugRequestComments[1]} />);
    await waitFor(() => {
      expect(screen.queryByText("PII Actions")).not.toBeInTheDocument();
      expect(screen.queryByText("Current Action:")).not.toBeInTheDocument();
    });
  });

  it("disables public toggle when PII action does not allow public access", async () => {
    const handleClose = jest.fn();
    render(<RequestCommentModifyModal isOpen={true} onClose={handleClose} comment={debugRequestComments[0]} />);

    await waitFor(() => {
      const publicToggle = screen.getByTestId("public-comment-label").querySelector('input[type="checkbox"]');
      expect(publicToggle).toBeDisabled();
    });
  });

  it("enables public toggle when PII action allows public access", async () => {
    const commentWithAcceptedPii = {
      ...debugRequestComments[0],
      piiMetadata: {
        piiPresent: true,
        action: { code: PiiActionCode.ACCEPTED },
        piiDetail: [],
      },
    };

    const handleClose = jest.fn();
    render(<RequestCommentModifyModal isOpen={true} onClose={handleClose} comment={commentWithAcceptedPii} />);

    await waitFor(() => {
      const publicToggle = screen.getByTestId("public-comment-label").querySelector('input[type="checkbox"]');
      expect(publicToggle).not.toBeDisabled();
    });
  });
});

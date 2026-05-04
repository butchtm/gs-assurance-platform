import { render, screen, waitFor } from "@testing-library/react";

import { debugRequestComments, mockRequestConsultations } from "../../../common/request/DebugRequests";

import { RequestCommentsList } from "./RequestCommentsList";

describe("RequestCommentModifyModal", () => {
  it("fetches and renders comments", async () => {
    render(<RequestCommentsList requestComments={debugRequestComments} refreshComments={jest.fn()} />);

    await waitFor(() => {
      debugRequestComments.forEach((comment) => {
        expect(screen.getByText(comment.submittedComment)).toBeInTheDocument();
      });
    });
  });

  it("fetches and renders names correctly", async () => {
    render(<RequestCommentsList requestComments={debugRequestComments} refreshComments={jest.fn()} />);

    await waitFor(() => {
      expect(screen.getByText("Test User 2")).toBeInTheDocument();
    });
  });

  it("renders a message when there are no comments", async () => {
    render(<RequestCommentsList requestComments={[]} refreshComments={jest.fn()} />);

    await waitFor(() => {
      expect(screen.getByText("No comments")).toBeInTheDocument();
    });
  });

  it("renders public consultation info", async () => {
    render(
      <RequestCommentsList consultations={mockRequestConsultations} requestComments={[]} refreshComments={jest.fn()} />,
    );

    await waitFor(() => {
      expect(screen.getByText("Public Consultation")).toBeInTheDocument();
    });
  });
});

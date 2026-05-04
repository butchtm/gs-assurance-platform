import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { mockProjectInConsultation } from "../../../common/project/DebugProject";
import { PublicStakeholderComment } from "../PublicConsultation/PublicConsultation";

import { PublicStakeholderCommentsList } from "./PublicStakeholderCommentsList";

describe("PublicStakeholderCommentsList", () => {
  const mockComments = mockProjectInConsultation.requests[0].comments;

  it("should display all the comment details", async () => {
    render(<PublicStakeholderCommentsList comments={mockComments}></PublicStakeholderCommentsList>);

    await waitFor(() => {
      expect(screen.getByText("Test Comment")).toBeInTheDocument();
      expect(screen.getByText("Test Comment Anonymous")).toBeInTheDocument();

      expect(screen.getByText("Test User")).toBeInTheDocument();

      expect(screen.getByText("01/07/2024")).toBeInTheDocument();
      expect(screen.getByText("02/07/2024")).toBeInTheDocument();
    });
  });

  it("should display a message if no comments are present", async () => {
    render(<PublicStakeholderCommentsList comments={[]}></PublicStakeholderCommentsList>);

    await waitFor(() => {
      expect(screen.getByText("No Public Comments Found")).toBeInTheDocument();
    });
  });

  it("should truncate long comments to 300 characters and show expand button", async () => {
    const longComment = "a".repeat(400);
    const longComments: PublicStakeholderComment[] = [
      {
        id: 1,
        submittedComment: longComment,
        uploadedByName: "Test User",
        uploadedDate: "2024-07-01",
        anonymous: false,
        addressed: false,
      },
    ];

    render(<PublicStakeholderCommentsList comments={longComments}></PublicStakeholderCommentsList>);

    await waitFor(() => {
      const truncatedText = "a".repeat(300) + "...";
      expect(screen.getByText(truncatedText)).toBeInTheDocument();
      expect(screen.getByTestId("expand-comment-1")).toBeInTheDocument();
    });
  });

  it("should not show expand button for short comments", async () => {
    const shortComments: PublicStakeholderComment[] = [
      {
        id: 1,
        submittedComment: "Short comment",
        uploadedByName: "Test User",
        uploadedDate: "2024-07-01",
        anonymous: false,
        addressed: false,
      },
    ];

    render(<PublicStakeholderCommentsList comments={shortComments}></PublicStakeholderCommentsList>);

    await waitFor(() => {
      expect(screen.getByText("Short comment")).toBeInTheDocument();
      expect(screen.queryByTestId("expand-comment-1")).not.toBeInTheDocument();
    });
  });

  it("should expand comment when expand button is clicked", async () => {
    const longComment = "a".repeat(400);
    const longComments: PublicStakeholderComment[] = [
      {
        id: 1,
        submittedComment: longComment,
        uploadedByName: "Test User",
        uploadedDate: "2024-07-01",
        anonymous: false,
        addressed: false,
      },
    ];

    render(<PublicStakeholderCommentsList comments={longComments}></PublicStakeholderCommentsList>);

    const expandButton = await screen.findByTestId("expand-comment-1");

    expect(screen.getByText("a".repeat(300) + "...")).toBeInTheDocument();

    fireEvent.click(expandButton);

    await waitFor(() => {
      expect(screen.getByText(longComment)).toBeInTheDocument();
      expect(screen.queryByText("a".repeat(300) + "...")).not.toBeInTheDocument();
    });
  });

  it("should collapse comment when collapse button is clicked", async () => {
    const longComment = "a".repeat(400);
    const longComments: PublicStakeholderComment[] = [
      {
        id: 1,
        submittedComment: longComment,
        uploadedByName: "Test User",
        uploadedDate: "2024-07-01",
        anonymous: false,
        addressed: false,
      },
    ];

    render(<PublicStakeholderCommentsList comments={longComments}></PublicStakeholderCommentsList>);

    const expandButton = await screen.findByTestId("expand-comment-1");

    fireEvent.click(expandButton);

    await waitFor(() => {
      expect(screen.getByText(longComment)).toBeInTheDocument();
    });

    fireEvent.click(expandButton);

    await waitFor(() => {
      expect(screen.getByText("a".repeat(300) + "...")).toBeInTheDocument();
      expect(screen.queryByText(longComment)).not.toBeInTheDocument();
    });
  });

  it("should handle multiple comments with independent expand/collapse states", async () => {
    const longComment1 = "a".repeat(400);
    const longComment2 = "b".repeat(400);
    const multipleComments: PublicStakeholderComment[] = [
      {
        id: 1,
        submittedComment: longComment1,
        uploadedByName: "Test User 1",
        uploadedDate: "2024-07-01",
        anonymous: false,
        addressed: false,
      },
      {
        id: 2,
        submittedComment: longComment2,
        uploadedByName: "Test User 2",
        uploadedDate: "2024-07-02",
        anonymous: false,
        addressed: false,
      },
    ];

    render(<PublicStakeholderCommentsList comments={multipleComments}></PublicStakeholderCommentsList>);

    const expandButton1 = await screen.findByTestId("expand-comment-1");
    const expandButton2 = await screen.findByTestId("expand-comment-2");

    fireEvent.click(expandButton1);

    await waitFor(() => {
      expect(screen.getByText(longComment1)).toBeInTheDocument();
      expect(screen.getByText("b".repeat(300) + "...")).toBeInTheDocument();
    });

    fireEvent.click(expandButton2);

    await waitFor(() => {
      expect(screen.getByText(longComment1)).toBeInTheDocument();
      expect(screen.getByText(longComment2)).toBeInTheDocument();
    });

    fireEvent.click(expandButton1);

    await waitFor(() => {
      expect(screen.getByText("a".repeat(300) + "...")).toBeInTheDocument();
      expect(screen.getByText(longComment2)).toBeInTheDocument();
    });
  });
});

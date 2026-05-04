import { render, screen } from "@testing-library/react";

import { BatchUsersSummaryModal } from "./BatchUsersSummaryModal";

describe("BatchUsersSummaryModal", () => {
  it("should display correct stats for completed batch", () => {
    render(
      <BatchUsersSummaryModal
        isOpen={true}
        onClose={jest.fn()}
        isLoading={false}
        batchUserResponse={{
          id: 1,
          errors: [],
          createdAt: "2024-10-11T12:00:00Z",
          updatedAt: "2024-10-11T12:00:00Z",
          complete: false,
          count: 50,
          successCount: 48,
        }}
        onBatchedUsersUploaded={jest.fn()}
      />,
    );

    expect(screen.getByText(/50 uploaded/i)).toBeInTheDocument();
    expect(screen.getByText(/48 successful/i)).toBeInTheDocument();
    expect(screen.getByText(/0 failed/i)).toBeInTheDocument();
  });

  it("should display a table of users which have failed to create", () => {
    render(
      <BatchUsersSummaryModal
        isOpen={true}
        onClose={jest.fn()}
        isLoading={false}
        batchUserResponse={{
          id: 1,
          errors: [
            {
              userName: "julian",
              message: "Unable to upload user to Cognito, please retry",
              account: "Test Company Ltd",
            },
          ],
          createdAt: "2024-10-11T12:00:00Z",
          updatedAt: "2024-10-11T12:00:00Z",
          complete: false,
          count: 50,
          successCount: 49,
        }}
        onBatchedUsersUploaded={jest.fn()}
      />,
    );

    expect(screen.getByText("Failed Users")).toBeInTheDocument();
    expect(screen.getByText("julian")).toBeInTheDocument();
    expect(screen.getByText("Test Company Ltd")).toBeInTheDocument();
    expect(screen.getByText("Unable to upload user to Cognito, please retry")).toBeInTheDocument();
  });
});

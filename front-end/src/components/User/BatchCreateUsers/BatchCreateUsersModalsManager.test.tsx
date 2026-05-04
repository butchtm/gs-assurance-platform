import { render, screen, waitFor } from "@testing-library/react";
import user from "@testing-library/user-event";

import { BatchUserError, BatchUserResponse, getBatchStatus, uploadBatchUsers } from "../User";

import { BatchCreateUsersModalsManager } from "./BatchCreateUsersModalsManager";

jest.mock("../User", () => ({
  uploadBatchUsers: jest.fn(),
  getBatchStatus: jest.fn(),
}));

const getResponse = (args: {
  complete: boolean;
  successCount: number;
  errors: BatchUserError[];
}): BatchUserResponse => {
  return {
    id: 1,
    errors: args.errors,
    createdAt: "2024-10-11T12:00:00Z",
    updatedAt: "2024-10-11T12:00:00Z",
    complete: args.complete,
    count: 50,
    successCount: args.successCount,
  } as BatchUserResponse;
};

const initialResponse = getResponse({ complete: false, successCount: 0, errors: [] });

const mockError = {
  userName: "jane",
  message: "Unable to upload user to Cognito, please retry",
  account: "Test Company Ltd",
} as BatchUserError;

describe("BatchCreateUsersModalsManager", () => {
  const uploadMockFile = async (): Promise<File> => {
    const mockFileContent = [
      ["username", "firstName", "lastName", "email", "groups", "accountId"],
      ["jane", "Jane", "Smith", "jane@example.com", "GS-Admin", "1"],
    ];

    let csvContent = "";
    mockFileContent.forEach(function (rowArray) {
      const row = rowArray.join(",");
      csvContent += row + "\r\n";
    });
    const blob = new Blob([csvContent]);
    const mockFile = new File([blob], "users.csv", {
      type: "text/csv",
    });

    const input = screen.getByLabelText("Upload CSV");
    await user.upload(input, mockFile);
    return mockFile;
  };

  it("should poll for the batch creation status until the creation is complete", async () => {
    render(<BatchCreateUsersModalsManager isOpen={true} onClose={jest.fn()} refreshUsers={jest.fn()} />);
    (uploadBatchUsers as jest.Mock).mockResolvedValue(initialResponse);
    (getBatchStatus as jest.Mock)
      .mockResolvedValueOnce(getResponse({ complete: false, successCount: 25, errors: [] }))
      .mockResolvedValue(getResponse({ complete: true, successCount: 49, errors: [mockError] }));

    await uploadMockFile();

    await waitFor(
      () => {
        expect(screen.getByText("50 uploaded - 49 successful, 1 failed")).toBeInTheDocument();
        expect(getBatchStatus).toHaveBeenCalledTimes(2);
      },
      { timeout: 3000 },
    );
  });

  it("should refresh the user list on successful users", async () => {
    const mockRefreshUsers = jest.fn();
    render(<BatchCreateUsersModalsManager isOpen={true} onClose={jest.fn()} refreshUsers={mockRefreshUsers} />);
    (uploadBatchUsers as jest.Mock).mockResolvedValue(initialResponse);
    (getBatchStatus as jest.Mock)
      .mockResolvedValueOnce(getResponse({ complete: false, successCount: 25, errors: [] }))
      .mockResolvedValue(getResponse({ complete: true, successCount: 49, errors: [mockError] }));

    await uploadMockFile();

    await waitFor(
      () => {
        expect(mockRefreshUsers).toHaveBeenCalledTimes(1);
      },
      { timeout: 3000 },
    );
  });

  it("should not refresh the user list if no successful users", async () => {
    const mockRefreshUsers = jest.fn();
    render(<BatchCreateUsersModalsManager isOpen={true} onClose={jest.fn()} refreshUsers={mockRefreshUsers} />);
    (uploadBatchUsers as jest.Mock).mockResolvedValue(initialResponse);
    (getBatchStatus as jest.Mock)
      .mockResolvedValueOnce(getResponse({ complete: false, successCount: 0, errors: [] }))
      .mockResolvedValue(getResponse({ complete: true, successCount: 0, errors: [mockError] }));

    await uploadMockFile();

    await waitFor(() => {
      expect(mockRefreshUsers).toHaveBeenCalledTimes(0);
    });
  });
});

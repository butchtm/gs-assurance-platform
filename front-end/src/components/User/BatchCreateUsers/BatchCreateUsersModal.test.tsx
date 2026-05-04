import { render, screen, waitFor } from "@testing-library/react";
import user from "@testing-library/user-event";

import { BatchCreateUsersModal } from "./BatchCreateUsersModal";

describe("BatchCreateUsersModal", () => {
  it("should initially provide instructions for which fields are required in the CSV", async () => {
    render(<BatchCreateUsersModal isOpen={true} onClose={jest.fn()} onBatchedUsersUploaded={jest.fn()} />);

    await waitFor(() => {
      expect(screen.getByText("userName")).toBeInTheDocument();
      expect(screen.getByText("firstName")).toBeInTheDocument();
      expect(screen.getByText("lastName")).toBeInTheDocument();
      expect(
        screen.getByText(
          "groups: ARM-Team, Finance, GS-Admin, GS-Registry, GS-Reviewer, Project-Developer, TAC, VVB, Support",
        ),
      ).toBeInTheDocument();
    });
  });

  it("should submit a stringified CSV of users", async () => {
    const handleBatchedUsersUploaded = jest.fn();
    render(
      <BatchCreateUsersModal isOpen={true} onClose={jest.fn()} onBatchedUsersUploaded={handleBatchedUsersUploaded} />,
    );

    const csvContent = await uploadMockFile();

    await waitFor(() => {
      expect(handleBatchedUsersUploaded).toHaveBeenCalledWith(csvContent);
    });
  });
});

const uploadMockFile = async (): Promise<File> => {
  const mockFileContent = [
    ["username", "firstName", "lastName", "email", "groups", "accountId"],
    ["jsmith", "Jane", "Smith", "jsmith@example.com", "GS-Admin", "1"],
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

import { render, screen, waitFor } from "@testing-library/react";

import { AuthContext, AuthContextType } from "../../../Auth/Login/AuthProvider";
import { debugRequests } from "../../../../common/request/DebugRequests";
import { mockDocuments } from "../../../../common/document/DebugDocuments";

import { DocumentDeleteAction } from "./DocumentDeleteAction";

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useParams: jest.fn(),
}));

jest.mock("../../../../ProtectedComponent", () => {
  return ({ children }: { children: React.ReactNode }) => children;
});

describe("DocumentDeleteAction Component", () => {
  it("should show delete button for admin users", async () => {
    render(
      <AuthContext.Provider
        value={
          {
            isAdminUser: () => true,
          } as AuthContextType
        }
      >
        <DocumentDeleteAction request={debugRequests[0]} document={mockDocuments[0]} onDocumentDelete={jest.fn()} />
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.getByRole("button", { name: "Delete document" })).toBeInTheDocument();
    });
  });

  it("should not show delete button for non-admin users", async () => {
    render(
      <AuthContext.Provider
        value={
          {
            isAdminUser: () => false,
          } as AuthContextType
        }
      >
        <DocumentDeleteAction request={debugRequests[0]} document={mockDocuments[0]} onDocumentDelete={jest.fn()} />
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.queryByRole("button", { name: "Delete document" })).not.toBeInTheDocument();
    });
  });
});

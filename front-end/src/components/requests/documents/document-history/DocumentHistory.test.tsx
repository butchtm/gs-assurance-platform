import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { mockDocuments } from "../../../../common/document/DebugDocuments";
import { debugRequests } from "../../../../common/request/DebugRequests";
import { AuthContext, AuthContextType } from "../../../Auth/Login/AuthProvider";
import { mockOnDocumentModify } from "../DocumentList.test";

import { DocumentHistory } from "./DocumentHistory";

jest.mock("../../../../common/env/env", () => ({
  isFormEnabled: jest.fn(() => true),
}));

describe("DocumentHistory", () => {
  beforeEach(() => {
    const mockAuthProvider = {
      isAdminUser: jest.fn(),
      isArmUser: jest.fn(),
      isSupportUser: jest.fn(),
    } as Record<keyof AuthContextType, jest.Mock>;

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <DocumentHistory documents={mockDocuments} request={debugRequests[0]} onDocumentModify={mockOnDocumentModify} />
      </AuthContext.Provider>,
    );
  });

  it("displays the document history as closed by default", async () => {
    await waitFor(() => {
      expect(screen.getByText("Document History")).toBeInTheDocument();
      expect(screen.queryByText("Document1.pdf")).not.toBeInTheDocument();
    });
  });

  it("displays the document history when expanded", async () => {
    await waitFor(() => {
      fireEvent.click(screen.getByTestId("expand-history"));
    });

    await waitFor(() => {
      expect(screen.getByText("Document1.pdf")).toBeInTheDocument();
      expect(screen.getByText("OldDocument1.pdf")).toBeInTheDocument();
      expect(screen.getByText("Document2.docx")).toBeInTheDocument();

      expect(screen.getByText("01/01/2023 10:00")).toBeInTheDocument();
      expect(screen.getByText("01/01/2022 10:00")).toBeInTheDocument();
      expect(screen.getByText("02/02/2023 10:00")).toBeInTheDocument();

      expect(screen.getByText("1 January 2023 12:00")).toBeInTheDocument();
      expect(screen.getByText("1 January 2023 12:00")).toBeInTheDocument();
      expect(screen.getByText("2 February 2023 18:00")).toBeInTheDocument();
    });
  });
});

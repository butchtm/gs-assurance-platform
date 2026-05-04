import { render, screen, waitFor } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";

import { mockActiveReferenceTypes } from "../../../../common/data-types/DebugReferenceTypes";
import { AuthContext, AuthContextType } from "../../../Auth/Login/AuthProvider";
import { getMethodologyHistory } from "../../Project";

import { MethodologiesHistory } from "./MethodologiesHistory";

const mockHistory = [
  {
    id: 123,
    projectId: 1,
    periodId: 2,
    requestId: 3,
    updatedAt: "2025-04-10T10:56:57.718888Z",
    updatedByAccountId: 7,
    updatedByAccountName: "Project Developer",
    updates: [
      {
        id: 1,
        originalVersion: 1.2,
        updatedVersion: 7.9,
        operation: "UPDATE",
      },
    ],
    verdict: "REQUEST_NOT_APPROVED",
  },
  {
    projectMethodologyUpdateId: 456,
    projectId: 1,
    periodId: 2,
    requestId: null,
    updatedAt: "2025-04-12T10:56:57.718888Z",
    updatedByAccountId: 8,
    updatedByAccountName: "GS ARM Team",
    updates: [
      {
        id: 2,
        originalVersion: null,
        updatedVersion: 5.5,
        operation: "ADD",
      },
    ],
    verdict: "REQUEST_APPROVED",
  },
  {
    id: 789,
    projectId: 1,
    periodId: 2,
    requestId: null,
    updatedAt: "2025-04-14T10:56:57.718888Z",
    updatedByAccountId: null,
    updatedByAccountName: "Admin",
    updates: [
      {
        id: 3,
        originalVersion: 4.0,
        updatedVersion: null,
        operation: "DELETE",
      },
    ],
    verdict: "PROJECT_FORCED",
  },
];

const mockMethodologyHistoryResponse = {
  content: mockHistory,
  pageSize: 10,
  pageNumber: 1,
  totalPages: 1,
  totalElements: 10,
};

jest.mock("../../Project", () => ({
  getMethodologyHistory: jest.fn(),
}));

jest.mock("../../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../../common/data-types/DataReferenceTypes"),
  useReferenceTypes: () => ({
    referenceMethodologies: mockActiveReferenceTypes.Methodology,
  }),
}));

describe("MethodologiesHistory", () => {
  beforeEach(() => {
    (getMethodologyHistory as jest.Mock).mockResolvedValue(mockMethodologyHistoryResponse);
  });
  it("should fetch the history via a request", async () => {
    render(
      <AuthContext.Provider value={{ isAdminUser: () => true, isArmUser: () => true } as unknown as AuthContextType}>
        <BrowserRouter>
          <MethodologiesHistory projectId={1} periodId={"2"} />
        </BrowserRouter>
      </AuthContext.Provider>,
    );
    await waitFor(() => {
      expect(getMethodologyHistory).toHaveBeenCalledWith(1, "2", [], 1, 10);
    });
  });

  it("should render the methodology history", async () => {
    render(
      <AuthContext.Provider value={{ isAdminUser: () => true, isArmUser: () => true } as unknown as AuthContextType}>
        <BrowserRouter>
          <MethodologiesHistory projectId={1} periodId={"2"} />
        </BrowserRouter>
      </AuthContext.Provider>,
    );
    await waitFor(() => {
      expect(screen.getByText("Date")).toBeInTheDocument();
      expect(screen.getByText("10/04/2025")).toBeInTheDocument();
      expect(screen.getByText("12/04/2025")).toBeInTheDocument();
      expect(screen.getByText("14/04/2025")).toBeInTheDocument();

      expect(screen.getByText("Methodology")).toBeInTheDocument();
      expect(screen.getByText("Methodology1")).toBeInTheDocument();
      expect(screen.getByText("Methodology2")).toBeInTheDocument();
      expect(screen.getByText("Methodology3")).toBeInTheDocument();

      expect(screen.getByText("Previous Version")).toBeInTheDocument();
      expect(screen.getByText("1.2")).toBeInTheDocument();
      expect(screen.getByText("4")).toBeInTheDocument();

      expect(screen.getByText("New Version")).toBeInTheDocument();
      expect(screen.getByText("7.9")).toBeInTheDocument();
      expect(screen.getByText("5.5")).toBeInTheDocument();

      expect(screen.getByText("Change Type")).toBeInTheDocument();
      expect(screen.getByText("New")).toBeInTheDocument();
      expect(screen.getByText("Version Change")).toBeInTheDocument();
      expect(screen.getByText("Removal")).toBeInTheDocument();
      expect(screen.getByText("Approved")).toBeInTheDocument();
      expect(screen.getByText("Denied")).toBeInTheDocument();
      expect(screen.getByText("Forced")).toBeInTheDocument();
    });
  });
});

import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";

import { mockBatchedProjectsRealCase } from "../../../common/project/DebugProject";
import * as requestModule from "../Requests";
import { AuthContext, AuthContextType } from "../../Auth/Login/AuthProvider";

import { RequestBatchSummary } from "./RequestBatchSummary";

jest.mock("./../Requests", () => ({
  getPublicBatchedProjectRequests: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
}));

const mockBatchProjectRequests = {
  parent: { classification: "test", gsid: "GS123", id: 123, name: "Real Case VPA", requestId: 2, methodologies: [] },
  children: [
    { classification: "test", gsid: "GS1223", id: 1233, name: "Regular Case VPA", requestId: 3, methodologies: [] },
  ],
};

const renderWithAuth = () => {
  render(
    <AuthContext.Provider
      value={
        {
          isAdminUser: () => true,
          isArmUser: () => true,
        } as AuthContextType
      }
    >
      <RequestBatchSummary projectId={1} batchRequests={mockBatchProjectRequests} />
    </AuthContext.Provider>,
  );
};

describe("RequestBatchSummary", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("renders correctly", () => {
    renderWithAuth();
    expect(screen.getByText(/Request Batches/i)).toBeInTheDocument();
  });

  it("renders the tree correctly with a real case parent and regular case children", async () => {
    renderWithAuth();
    await waitFor(() => expect(screen.getByText("Real Case VPA")).toBeInTheDocument());
  });

  it("expands tree when clicked", async () => {
    (requestModule.getPublicBatchedProjectRequests as jest.Mock).mockResolvedValue(mockBatchedProjectsRealCase);
    renderWithAuth();
    await waitFor(async () => {
      const button = screen.getByText("Real Case VPA").closest("button");
      if (button) {
        userEvent.click(button);
        await waitFor(() => expect(screen.getByText("Regular Case VPA")).toBeInTheDocument());

        userEvent.click(button);
        expect(screen.queryByText("Regular Case VPA")).not.toBeInTheDocument();
      }
    });
  });
});

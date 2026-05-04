import { render, screen, waitFor } from "@testing-library/react";

import { mockRequiredDocumentsResponse } from "../../common/document/DebugDocuments";
import mockProjects, { debugProject } from "../../common/project/DebugProject";
import { debugRequests } from "../../common/request/DebugRequests";
import { AuthContext, AuthContextType } from "../Auth/Login/AuthProvider";
import { getProject } from "../Project/Project";

import { RequestsView } from "./RequestsView";

jest.mock("../Project/Project", () => ({
  getAllProjectsForAccount: jest.fn(),
  createProject: jest.fn(),
  getProject: jest.fn(),
}));

jest.mock("./Requests", () => ({
  getRequestsByProject: jest.fn().mockResolvedValue(debugRequests),
  getRequiredDocumentsForRequest: jest.fn().mockResolvedValue(mockRequiredDocumentsResponse),
  getPublicBatchedProjectRequests: jest.fn(),
  getPrivateBatchProjectRequests: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
  useSearchParams: () => [
    {
      get: jest.fn(),
    },
    jest.fn(),
  ],
}));

jest.mock("../../common/env/env", () => ({
  isEndConsultationEnabled: jest.fn(),
}));

const mockAuthProvider = {
  isProjectDeveloperUser: jest.fn(),
  isAdminUser: jest.fn(),
  isArmUser: jest.fn(),
} as Record<keyof AuthContextType, jest.Mock>;

describe("RequestView Component", () => {
  beforeEach(() => {
    mockAuthProvider.isProjectDeveloperUser.mockReturnValue(false);
    mockAuthProvider.isAdminUser.mockReturnValue(false);
    mockAuthProvider.isArmUser.mockReturnValue(false);
    (getProject as jest.Mock).mockResolvedValue(debugProject);
  });

  it("renders correctly and displays create new request button for admins", async () => {
    mockAuthProvider.isAdminUser.mockReturnValue(true);
    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <RequestsView project={mockProjects[1]} />
      </AuthContext.Provider>,
    );

    await waitFor(() => expect(screen.getByText("Create New Request")).toBeInTheDocument());
  });

  it("renders correctly and displays create new request button for ARM team", async () => {
    mockAuthProvider.isArmUser.mockReturnValue(true);
    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <RequestsView project={mockProjects[1]} />
      </AuthContext.Provider>,
    );

    await waitFor(() => expect(screen.getByText("Create New Request")).toBeInTheDocument());
  });

  it("renders correctly and displays create new request button for Project Developer that IS assigned to a project", async () => {
    mockAuthProvider.isProjectDeveloperUser.mockReturnValue(true);
    render(
      <AuthContext.Provider
        value={
          {
            ...(mockAuthProvider as unknown as AuthContextType),
            isUserOwnerOf: () => true,
          } as AuthContextType
        }
      >
        <RequestsView project={mockProjects[1]} />
      </AuthContext.Provider>,
    );

    await waitFor(() => expect(screen.getByText("Create New Request")).toBeInTheDocument());
  });

  it("DOES NOT display create new request button for Project Developer that IS NOT assigned to a project", async () => {
    mockAuthProvider.isProjectDeveloperUser.mockReturnValue(true);
    render(
      <AuthContext.Provider
        value={
          {
            ...(mockAuthProvider as unknown as AuthContextType),
            isUserOwnerOf: () => false,
          } as AuthContextType
        }
      >
        <RequestsView project={mockProjects[1]} />
      </AuthContext.Provider>,
    );

    await waitFor(() => expect(screen.queryByText("Create New Request")).not.toBeInTheDocument());
  });

  it("renders RequestDropdown components with correct titles", async () => {
    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <RequestsView project={{ id: 1, name: "Mock Project" }} />
      </AuthContext.Provider>,
    );

    for (const request of debugRequests) {
      const titleElement = await screen.findByText(request.reviewName);
      expect(titleElement).toBeInTheDocument();
    }
  });
});

import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter, useParams } from "react-router-dom";

import mockProjects, {
  debugProject,
  mockProjectForHierarchy,
  mockProjectHierarchyTree,
} from "../../../common/project/DebugProject";
import { debugRequests, mockBatchProjectsRequests } from "../../../common/request/DebugRequests";
import { AuthContext, AuthContextType } from "../../Auth/Login/AuthProvider";
import { getPublicBatchedProjectRequests, getRequestWithId } from "../../requests/Requests";
import { getProject, getProjectByGsid, getProjectRiskHistory, getProjectTree } from "../Project";

import ViewProject from "./ViewProject";

jest.mock("../Project", () => ({
  getProject: jest.fn(),
  getProjectByGsid: jest.fn(),
  getProjectTree: jest.fn(),
  getProjectRiskHistory: jest.fn(),
}));

jest.mock("../../requests/Requests", () => ({
  getRequestWithId: jest.fn().mockResolvedValue(debugRequests[0]),
  getRequestsByProject: jest.fn().mockResolvedValue(debugRequests),
  getPublicBatchedProjectRequests: jest.fn(),
  getPrivateBatchProjectRequests: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
  useParams: jest.fn(),
  useLocation: jest.fn(),
  useSearchParams: () => [
    {
      get: jest.fn(),
    },
    jest.fn(),
  ],
}));

jest.mock("../../../common/env/env", () => ({
  isEndConsultationEnabled: jest.fn(),
}));

const renderWithDefaultAuth = (adminOverride?: boolean) => {
  render(
    <AuthContext.Provider
      value={
        {
          isAdminUser: () => adminOverride ?? false,
          isArmUser: () => false,
          isProjectDeveloperUser: () => false,
          isUserOwnerOf: () => false,
        } as unknown as AuthContextType
      }
    >
      <ViewProject />
    </AuthContext.Provider>,
  );
};

describe("ViewProject Component", () => {
  it("navigates to a project via ID and displays project information", async () => {
    (getProject as jest.Mock).mockResolvedValueOnce(debugProject);
    (useParams as jest.Mock).mockImplementation(() => ({ projectId: "1" }));
    renderWithDefaultAuth();

    await waitFor(() => {
      expect(getProject).toHaveBeenCalledWith(1);
      expect(screen.getByText("GS123 - test project")).toBeInTheDocument();
    });
  });

  it("navigates to a project via GSID and displays project information", async () => {
    (getProjectByGsid as jest.Mock).mockResolvedValueOnce(debugProject);
    (useParams as jest.Mock).mockImplementation(() => ({ projectId: "GS123" }));
    renderWithDefaultAuth();

    await waitFor(() => {
      expect(getProjectByGsid).toHaveBeenCalledWith("GS123");
      expect(screen.getByText("GS123 - test project")).toBeInTheDocument();
    });
  });

  it("renders transition scheme view tab if is a transitioning project", async () => {
    (getProjectByGsid as jest.Mock).mockResolvedValueOnce(mockProjects[0]);
    (useParams as jest.Mock).mockImplementation(() => ({ projectId: "GS123" }));
    renderWithDefaultAuth();

    await waitFor(() => {
      expect(screen.getByText("GSID")).toBeInTheDocument();

      expect(screen.getByText("Transition Scheme")).toBeInTheDocument();
    });
  });

  it("does not render transition scheme view tab if it is not a transitioning project", async () => {
    (getProjectByGsid as jest.Mock).mockResolvedValueOnce(mockProjects[5]);
    (useParams as jest.Mock).mockImplementation(() => ({ projectId: "GSID91" }));
    renderWithDefaultAuth();

    await waitFor(() => {
      expect(screen.getByText("GSID")).toBeInTheDocument();

      expect(screen.queryByText("Transition Scheme")).not.toBeInTheDocument();
    });
  });

  it("does not render transition scheme view tab if transition detail is null", async () => {
    const project = JSON.parse(JSON.stringify(mockProjects[5]));
    project.transitionDetail = null;
    (getProjectByGsid as jest.Mock).mockResolvedValueOnce(project);
    (useParams as jest.Mock).mockImplementation(() => ({ projectId: "GSID91" }));
    renderWithDefaultAuth();

    await waitFor(() => {
      expect(screen.getByText("GSID")).toBeInTheDocument();

      expect(screen.queryByText("Transition Scheme")).not.toBeInTheDocument();
    });
  });

  it("renders correct page on when tab clicked for: Project Information", async () => {
    (getProjectByGsid as jest.Mock).mockResolvedValueOnce(mockProjects[0]);
    (useParams as jest.Mock).mockImplementation(() => ({ projectId: "GS123" }));
    render(
      <AuthContext.Provider
        value={
          {
            isProjectDeveloperUser: jest.fn().mockResolvedValue(true),
            isUserOwnerOf: () => true,
            isAdminUser: () => false,
            isArmUser: () => false,
          } as Record<keyof AuthContextType, jest.Mock> as unknown as AuthContextType
        }
      >
        <ViewProject />
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.getByText("GSID")).toBeInTheDocument();
    });

    //Click off of default project information tab first
    fireEvent.click(screen.getByText("Requests"));

    fireEvent.click(screen.getByText("Project Information"));

    await waitFor(() => {
      expect(screen.getByText("Project Characteristics")).toBeInTheDocument();
    });
  });

  it("renders correct page on when tab clicked for: Transition Scheme", async () => {
    (getProjectByGsid as jest.Mock).mockResolvedValueOnce(mockProjects[0]);
    (useParams as jest.Mock).mockImplementation(() => ({ projectId: "GS123" }));
    render(
      <AuthContext.Provider
        value={
          {
            isAdminUser: () => false,
            isArmUser: () => false,
            isProjectDeveloperUser: () => false,
            isUserOwnerOf: () => false,
          } as unknown as AuthContextType
        }
      >
        <MemoryRouter>
          <ViewProject />
        </MemoryRouter>
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.getByText("GSID")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByText("Transition Scheme"));

    await waitFor(() => {
      expect(screen.getByText("TRANSITION FROM OTHER SCHEME")).toBeInTheDocument();
    });
  });

  it("renders correct page on when tab clicked for: Requests", async () => {
    (getProjectByGsid as jest.Mock).mockResolvedValueOnce(mockProjects[0]);
    (useParams as jest.Mock).mockImplementation(() => ({ projectId: "GS123" }));
    (getPublicBatchedProjectRequests as jest.Mock).mockResolvedValue(mockBatchProjectsRequests);
    const { container } = render(
      <AuthContext.Provider
        value={
          {
            isProjectDeveloperUser: jest.fn().mockResolvedValue(true),
            isUserOwnerOf: () => true,
            isAdminUser: () => false,
            isArmUser: () => false,
          } as Record<keyof AuthContextType, jest.Mock> as unknown as AuthContextType
        }
      >
        <ViewProject />
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.getByText("GSID")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByText("Requests"));

    await waitFor(() => {
      expect(container.getElementsByClassName("requests-view").length).toBe(1);
    });
  });

  it("renders correct page on when tab clicked for: Project VPAs", async () => {
    (getProjectByGsid as jest.Mock).mockResolvedValueOnce(mockProjectForHierarchy);
    (useParams as jest.Mock).mockImplementation(() => ({ projectId: "GS1" }));
    (getRequestWithId as jest.Mock).mockResolvedValue(debugRequests);
    (getPublicBatchedProjectRequests as jest.Mock).mockResolvedValue(mockBatchProjectsRequests);
    (getProjectTree as jest.Mock).mockResolvedValue(mockProjectHierarchyTree);
    renderWithDefaultAuth();

    await waitFor(() => {
      expect(screen.getByText("GSID")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByText("Project VPAs"));

    await waitFor(() => {
      expect(screen.getByText("GS1")).toBeInTheDocument();
      expect(screen.getByText("Test POA")).toBeInTheDocument();
    });
  });

  it("should display the risk banner if project is deemed high risk", async () => {
    (getProject as jest.Mock).mockResolvedValueOnce({
      ...debugProject,
      currentRiskSummary: {
        projectId: 1,
        risk: true,
        summary: "Project has been deemed high risk",
        dateApplied: "2025-07-14T15:00:00.000Z",
      },
    });
    (useParams as jest.Mock).mockImplementation(() => ({ projectId: "1" }));
    renderWithDefaultAuth(true);

    await waitFor(() => {
      expect(getProject).toHaveBeenCalledWith(1);
      expect(screen.getByText("High Risk")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByText("High Risk"));

    await waitFor(() => {
      expect(screen.getByText("Project has been deemed high risk")).toBeInTheDocument();
    });
  });

  it("should allow for updating the risk from the risk banner", async () => {
    (getProject as jest.Mock).mockResolvedValueOnce({
      ...debugProject,
      currentRiskSummary: {
        projectId: 1,
        risk: true,
        summary: "Project has been deemed high risk",
        dateApplied: "2025-07-14T15:00:00.000Z",
      },
    });
    (useParams as jest.Mock).mockImplementation(() => ({ projectId: "1" }));
    renderWithDefaultAuth(true);

    await waitFor(() => {
      expect(getProject).toHaveBeenCalledWith(1);
      expect(screen.getByText("High Risk")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByText("High Risk"));
    fireEvent.click(screen.getByTestId("update-risk"));

    await waitFor(() => {
      expect(screen.getByText("Update Project Risk")).toBeInTheDocument();
    });
  });

  it("should display a risk management tab for admin and ARM users", async () => {
    (getProject as jest.Mock).mockResolvedValueOnce(debugProject);
    (getProjectRiskHistory as jest.Mock).mockResolvedValueOnce({
      content: [],
      pageSize: 0,
      pageNumber: 0,
      totalPages: 0,
      totalElements: 0,
    });
    (useParams as jest.Mock).mockImplementation(() => ({ projectId: "1" }));
    renderWithDefaultAuth(true);

    await waitFor(() => {
      expect(screen.getByText("Risk")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByText("Risk"));

    await waitFor(() => {
      expect(screen.getByRole("button", { name: "Update Risk" })).toBeInTheDocument();
    });
  });

  it("should allow for updating the risk from the risk management tab", async () => {
    (getProject as jest.Mock).mockResolvedValueOnce(debugProject);
    (getProjectRiskHistory as jest.Mock).mockResolvedValueOnce({
      content: [],
      pageSize: 0,
      pageNumber: 0,
      totalPages: 0,
      totalElements: 0,
    });
    (useParams as jest.Mock).mockImplementation(() => ({ projectId: "1" }));
    renderWithDefaultAuth(true);

    await waitFor(() => {
      expect(screen.getByText("Risk")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByText("Risk"));
    fireEvent.click(screen.getByRole("button", { name: "Update Risk" }));

    await waitFor(() => {
      expect(screen.getByText("Update Project Risk")).toBeInTheDocument();
    });
  });

  it("can access the project edit from the project screen if ARM or admin", async () => {
    (getProject as jest.Mock).mockResolvedValueOnce(debugProject);
    (useParams as jest.Mock).mockImplementation(() => ({ projectId: "1" }));

    renderWithDefaultAuth(true);
    await waitFor(() => {
      expect(screen.getByTestId("edit-project")).toBeInTheDocument();
    });
  });

  it("cannot access the project edit from the project screen if not ARM or admin", async () => {
    (getProject as jest.Mock).mockResolvedValueOnce(debugProject);
    (useParams as jest.Mock).mockImplementation(() => ({ projectId: "1" }));
    renderWithDefaultAuth();
    await waitFor(() => {
      expect(screen.queryByTestId("edit-project")).not.toBeInTheDocument();
    });
  });
});

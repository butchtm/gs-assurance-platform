import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { cloneDeep } from "lodash";

import {
  mockActiveReferenceTypes,
  mockAllReferenceTypes,
  mockInactiveReferenceTypes,
} from "../../../common/data-types/DebugReferenceTypes";
import mockProjects from "../../../common/project/DebugProject";
import {
  debugClosedRequests,
  debugNotClosedRequest,
  debugNotRequestedRequest,
  debugRequests,
  debugRequestTransitionResponse,
  debugInProgressRequests,
  mockBatchProjectsRequests,
} from "../../../common/request/DebugRequests";
import { AuthContext, AuthContextType } from "../../Auth/Login/AuthProvider";
import { getAvailableRequestStatusTransitions, getRequestWithId } from "../Requests";
import { getRequestAssignments } from "../assignment/Assignment";
import { mockPlatformAccounts } from "../../../common/platform-accounts/DebugPlatformAccounts";

import { ActionsDropdown } from "./ActionsDropdown";

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../common/data-types/DataReferenceTypes"),
  getReferenceValueCodeById: jest.fn(),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
    inactiveReferenceTypes: mockInactiveReferenceTypes,
    allReferenceTypes: mockAllReferenceTypes,
  }),
  getReferenceValueDescriptionById: jest.fn(),
}));

jest.mock("../../requests/Requests", () => ({
  getRequestWithId: jest.fn(),
  getPublicBatchedProjectRequests: () => ({
    parent: {
      gsid: 1000,
    },
    children: [],
  }),
  getAvailableRequestStatusTransitions: jest.fn(),
  getPrivateBatchedProjectRequests: jest.fn(),
}));

jest.mock("../../Project/Project", () => ({
  getBatchProjects: () => ({
    content: mockProjects.slice(0, 3),
    totalPages: 1,
  }),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
}));

jest.mock("../../../common/env/env", () => ({
  isEndConsultationEnabled: jest.fn(),
}));

jest.mock("../assignment/Assignment", () => ({
  getRequestAssignments: jest.fn(),
}));

const mockAuthProvider = {
  isProjectDeveloperUser: jest.fn(),
  isAdminUser: jest.fn(),
  isArmUser: jest.fn(),
  isFinanceUser: jest.fn(),
  isReviewerUser: jest.fn(),
  isRegistryUser: jest.fn(),
  isTacUser: jest.fn(),
  isVvbUser: jest.fn(),
  isUserOwnerOf: jest.fn(),
  isSupportUser: jest.fn(),
} as Record<string, jest.Mock>;

describe("ActionsDropdown", () => {
  beforeEach(() => {
    jest.resetAllMocks();
    mockAuthProvider.isProjectDeveloperUser.mockReturnValue(false);
    mockAuthProvider.isAdminUser.mockReturnValue(false);
    mockAuthProvider.isArmUser.mockReturnValue(false);
    mockAuthProvider.isFinanceUser.mockReturnValue(false);
    mockAuthProvider.isReviewerUser.mockReturnValue(false);
    mockAuthProvider.isRegistryUser.mockReturnValue(false);
    mockAuthProvider.isTacUser.mockReturnValue(false);
    mockAuthProvider.isVvbUser.mockReturnValue(false);
    mockAuthProvider.isUserOwnerOf.mockReturnValue(false);
    mockAuthProvider.isSupportUser.mockReturnValue(false);
  });

  it("renders view N projects button when batch projects in request", async () => {
    (getRequestWithId as jest.Mock).mockResolvedValue(mockBatchProjectsRequests[0]);
    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <ActionsDropdown
          request={mockBatchProjectsRequests[0]}
          updateRequest={jest.fn()}
          updateDocuments={jest.fn()}
          accountId={1}
        />
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.getByText("View 3 projects in this request")).toBeInTheDocument();
    });
  });

  it("does not render view projects button when only 1 batch project in request", async () => {
    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <ActionsDropdown
          request={mockBatchProjectsRequests[1]}
          updateRequest={jest.fn()}
          updateDocuments={jest.fn()}
          accountId={1}
        />
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.queryByText("View 1 projects in this request")).not.toBeInTheDocument();
    });
  });

  it.each([
    ["Completed - Approved", debugClosedRequests[0]],
    ["Completed - Rejected", debugClosedRequests[1]],
    ["Cancelled", debugClosedRequests[2]],
    ["Omitted", debugClosedRequests[3]],
  ])("does not render upload document button for closed request (%s)", async (label, closedRequest) => {
    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <ActionsDropdown request={closedRequest} updateRequest={jest.fn()} updateDocuments={jest.fn()} accountId={1} />
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.queryByText("Upload Document")).not.toBeInTheDocument();
    });
  });

  it("shows upload document button for closed request for admin role", async () => {
    mockAuthProvider.isAdminUser.mockReturnValue(true);

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={debugClosedRequests[0]}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );

    expect(await screen.findByText("Upload Document")).toBeInTheDocument();
  });

  it("shows upload document button for open requests", async () => {
    (getRequestWithId as jest.Mock).mockResolvedValue(debugNotClosedRequest);
    (getAvailableRequestStatusTransitions as jest.Mock).mockResolvedValue(debugRequestTransitionResponse);

    mockAuthProvider.isProjectDeveloperUser.mockReturnValue(true);

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={debugNotClosedRequest}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.getByText("Upload Document")).toBeInTheDocument();
    });
  });

  it("shows upload document button for in progress request with reviewer assignment for reviewer role", async () => {
    mockAuthProvider.isReviewerUser.mockReturnValue(true);

    const request = debugInProgressRequests[0];
    const account = mockPlatformAccounts[2];
    (getRequestAssignments as jest.Mock).mockResolvedValue([
      {
        assignmentType: {
          id: 1,
          code: "REVIEW",
          description: "Review",
        },
        assignmentStatus: {
          id: 1,
          code: "ACCEPTED",
          description: "Accepted",
        },
        account: account,
        requestId: request.id,
      },
    ]);

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={request}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={account.id}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );

    expect(await screen.findByText("Upload Document")).toBeInTheDocument();
  });

  it("shows update request button for open requests for ARM team", async () => {
    (getRequestWithId as jest.Mock).mockResolvedValue(debugNotClosedRequest);
    (getAvailableRequestStatusTransitions as jest.Mock).mockResolvedValue(debugRequestTransitionResponse);
    mockAuthProvider.isArmUser.mockReturnValue(true);

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={debugNotClosedRequest}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.getByText("Update Request")).toBeInTheDocument();
    });
  });

  it("does not show update request button for closed requests for ARM team", async () => {
    (getRequestWithId as jest.Mock).mockResolvedValue(debugClosedRequests[0]);
    (getAvailableRequestStatusTransitions as jest.Mock).mockResolvedValue(debugRequestTransitionResponse);
    mockAuthProvider.isArmUser.mockReturnValue(true);

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={debugClosedRequests[0]}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.queryByText("Update Request")).not.toBeInTheDocument();
    });
  });

  it("does not show update request button for migrated requests", async () => {
    (getRequestWithId as jest.Mock).mockResolvedValue(debugRequests[0]);
    (getAvailableRequestStatusTransitions as jest.Mock).mockResolvedValue(debugRequestTransitionResponse);
    mockAuthProvider.isAdminUser.mockReturnValue(true);

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={debugRequests[0]}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.queryByText("Update Request")).not.toBeInTheDocument();
    });
  });

  it("shows no actions for not requested status requests for non admin or arm user", async () => {
    mockAuthProvider.isAdminUser.mockReturnValue(false);
    mockAuthProvider.isArmUser.mockReturnValue(false);

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={debugNotRequestedRequest}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );

    expect(await screen.findByText("No Actions Available")).toBeInTheDocument();
  });

  it("shows upload document button for not requested request for admin user", async () => {
    mockAuthProvider.isAdminUser.mockReturnValue(true);

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={debugNotRequestedRequest}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );

    expect(await screen.findByText("Upload Document")).toBeInTheDocument();
  });

  it("shows upload document button for not requested request for arm user", async () => {
    mockAuthProvider.isArmUser.mockReturnValue(true);

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={debugNotRequestedRequest}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );

    expect(await screen.findByText("Upload Document")).toBeInTheDocument();
  });

  it("shows add comment button for TAC user when request is in consultation", async () => {
    const requestInConsultation = {
      ...debugNotClosedRequest,
      inStakeholderConsultation: true,
    };

    mockAuthProvider.isTacUser.mockReturnValue(true);

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={requestInConsultation}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );

    expect(await screen.findByText("Add Comment")).toBeInTheDocument();
  });

  it("shows the open conversation action for ARM and Admin if no conversation present", async () => {
    mockAuthProvider.isAdminUser.mockReturnValue(true);
    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={debugNotClosedRequest}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );
    await waitFor(() => {
      expect(screen.getByText("Open Conversation")).toBeInTheDocument();
    });
  });

  it("shows the view conversation action for relevant PD", async () => {
    mockAuthProvider.isProjectDeveloperUser.mockReturnValue(true);
    mockAuthProvider.isUserOwnerOf.mockReturnValue(true);
    mockAuthProvider.isAdminUser.mockReturnValue(false);
    mockAuthProvider.isArmUser.mockReturnValue(false);

    const requestWithOpenConversation = {
      ...debugNotClosedRequest,
      conversation: {
        id: 1,
        status: {
          id: 1,
          code: "OPEN",
          description: "Open",
        },
      },
    };

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={requestWithOpenConversation}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );
    await waitFor(() => {
      expect(screen.getByText("View Active Conversation")).toBeInTheDocument();
    });
  });

  it("shows the view conversation action for relevant VVB", async () => {
    mockAuthProvider.isVvbUser.mockReturnValue(true);
    mockAuthProvider.isUserOwnerOf.mockReturnValue(true);
    mockAuthProvider.isAdminUser.mockReturnValue(false);
    mockAuthProvider.isArmUser.mockReturnValue(false);

    (getRequestAssignments as jest.Mock).mockResolvedValue([
      {
        assignmentType: {
          id: 1,
          code: "AUDIT",
          description: "Audit",
        },
        assignmentStatus: {
          id: 1,
          code: "ACCEPTED",
          description: "Accepted",
        },
        account: mockPlatformAccounts[1],
        requestId: debugNotClosedRequest.id,
      },
    ]);

    const requestWithOpenConversation = {
      ...debugNotClosedRequest,
      conversation: {
        id: 1,
        status: {
          id: 1,
          code: "OPEN",
          description: "Open",
        },
      },
    };

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={requestWithOpenConversation}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );
    await waitFor(() => {
      expect(screen.getByText("View Active Conversation")).toBeInTheDocument();
    });
  });

  it("shows the view conversation action for Admin or ARM", async () => {
    mockAuthProvider.isAdminUser.mockReturnValue(true);
    mockAuthProvider.isArmUser.mockReturnValue(false);

    const requestWithOpenConversation = {
      ...debugNotClosedRequest,
      conversation: {
        id: 1,
        status: {
          id: 1,
          code: "OPEN",
          description: "Open",
        },
      },
    };

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={requestWithOpenConversation}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );
    await waitFor(() => {
      expect(screen.getByText("View Active Conversation")).toBeInTheDocument();
    });
  });

  it("shows the view closed conversation action for relevant users if a conversation is closed", async () => {
    mockAuthProvider.isAdminUser.mockReturnValue(true);
    const requestWithClosedConversation = {
      ...debugNotClosedRequest,
      conversation: {
        id: 1,
        status: {
          id: 2,
          code: "CLOSED",
          description: "Closed",
        },
      },
    };

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={requestWithClosedConversation}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );
    await waitFor(() => {
      expect(screen.getByText("View Closed Conversation")).toBeInTheDocument();
    });
  });

  it("does not show the cancel request button for any users if request is already closed", async () => {
    mockAuthProvider.isAdminUser.mockReturnValue(true);

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={debugClosedRequests[0]}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );
    await waitFor(() => {
      expect(screen.queryByText("Cancel Request")).not.toBeInTheDocument();
    });
  });

  it("shows the cancel request button for admin users", async () => {
    mockAuthProvider.isAdminUser.mockReturnValue(true);

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={debugNotClosedRequest}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );
    await waitFor(() => {
      expect(screen.getByText("Cancel Request")).toBeInTheDocument();
    });
  });

  it("shows the cancel request button for ARM users", async () => {
    mockAuthProvider.isArmUser.mockReturnValue(true);

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={debugNotClosedRequest}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );
    await waitFor(() => {
      expect(screen.getByText("Cancel Request")).toBeInTheDocument();
    });
  });

  it("does not show the cancel request button for VVB users if no accepted assignments", async () => {
    mockAuthProvider.isVvbUser.mockReturnValue(true);

    (getRequestAssignments as jest.Mock).mockResolvedValue([]);

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={debugNotClosedRequest}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );
    await waitFor(() => {
      expect(screen.queryByText("Cancel Request")).not.toBeInTheDocument();
    });
  });

  it("shows the cancel request button for VVB users if accepted assignments and request In Audit", async () => {
    mockAuthProvider.isVvbUser.mockReturnValue(true);
    const inAuditRequest = cloneDeep(debugNotClosedRequest);
    inAuditRequest.requestStatus = {
      id: 11,
      code: "IN_AUDIT",
      description: "In Audit",
    };

    (getRequestAssignments as jest.Mock).mockResolvedValue([
      {
        assignmentType: {
          id: 1,
          code: "AUDIT",
          description: "Audit",
        },
        assignmentStatus: {
          id: 1,
          code: "ACCEPTED",
          description: "Accepted",
        },
        account: mockPlatformAccounts[1],
        requestId: inAuditRequest.id,
      },
    ]);

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={inAuditRequest}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );
    await waitFor(() => {
      expect(screen.getByText("Cancel Request")).toBeInTheDocument();
    });
  });

  it("shows the cancel request button for PD users if request is in preparation", async () => {
    mockAuthProvider.isProjectDeveloperUser.mockReturnValue(true);
    const inPreparationRequest = cloneDeep(debugNotClosedRequest);
    inPreparationRequest.requestStatus = {
      id: 6,
      code: "IN_PREPARATION",
      description: "In Preparation",
    };

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={inPreparationRequest}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );
    await waitFor(() => {
      expect(screen.getByText("Cancel Request")).toBeInTheDocument();
    });
  });

  it("does not show the cancel request button for PD users if request is not in preparation", async () => {
    mockAuthProvider.isProjectDeveloperUser.mockReturnValue(true);
    const inCompletenessCheckRequest = cloneDeep(debugNotClosedRequest);
    inCompletenessCheckRequest.requestStatus = {
      id: 9,
      code: "COMPLETENESS_CHECK",
      description: "Completeness Check",
    };

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActionsDropdown
            request={inCompletenessCheckRequest}
            updateRequest={jest.fn()}
            updateDocuments={jest.fn()}
            accountId={1}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );
    await waitFor(() => {
      expect(screen.queryByText("Cancel Request")).not.toBeInTheDocument();
    });
  });
});

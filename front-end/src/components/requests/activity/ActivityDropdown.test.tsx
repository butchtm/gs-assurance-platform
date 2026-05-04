import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";

import { RequestActivity } from "../../../common/request/GSRequest";
import { AuthContext, AuthContextType } from "../../Auth/Login/AuthProvider";
import { User } from "../../User/User";

import { ActivityDropdown } from "./ActivityDropdown";

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
  user: {} as User,
} as Record<keyof Omit<AuthContextType, "user">, jest.Mock> & {
  user: User;
};

describe("ActivityDropdown", () => {
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
    mockAuthProvider.user = {} as User;
  });

  const renderWithMockAuthProvider = () => {
    const mockRequestActivity: RequestActivity = {
      id: 2,
      index: 2,
      name: "Round 2 Review",
      startDate: "2024-11-20T10:21:37.54Z",
      endDate: "2024-11-28T10:21:37.54Z",
      dueDate: "2024-12-01T10:21:37.54Z",
      skipped: false,
      activity: {
        id: 2,
        index: 2,
        name: "Conclusion of Review",
        displayName: "Round 2+ Review",
        totalDays: 8,
        internal: false,
      },
      account: {
        id: 7,
        name: "Reviewer",
        accountType: {
          id: 0,
          code: "REVIEWER",
          description: "Reviewer",
          isActive: true,
        },
      },
    };

    render(
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <MemoryRouter>
          <ActivityDropdown requestActivity={mockRequestActivity} />
        </MemoryRouter>
      </AuthContext.Provider>,
    );
  };

  it("should render the activity information", async () => {
    mockAuthProvider.isProjectDeveloperUser.mockReturnValue(true);
    renderWithMockAuthProvider();

    await waitFor(() => {
      expect(screen.getByText("Round 2 Review")).toBeInTheDocument();
      expect(screen.getByText("01/12/2024")).toBeInTheDocument();
    });
  });

  it("should render the responsibility information for internal roles", async () => {
    mockAuthProvider.isAdminUser.mockReturnValue(true);
    renderWithMockAuthProvider();

    await waitFor(() => {
      expect(screen.getByText("Responsibility")).toBeInTheDocument();
      expect(screen.getByText("Reviewer")).toBeInTheDocument();
    });
  });

  it("should render the responsibility information for assigned RoEs", async () => {
    mockAuthProvider.isReviewerUser.mockReturnValue(true);
    mockAuthProvider.isUserOwnerOf.mockReturnValue(true);
    renderWithMockAuthProvider();

    await waitFor(() => {
      expect(screen.getByText("Responsibility")).toBeInTheDocument();
      expect(screen.getByText("Reviewer")).toBeInTheDocument();
    });
  });

  it("should not render the responsibility information for most users", async () => {
    mockAuthProvider.isProjectDeveloperUser.mockReturnValue(true);
    renderWithMockAuthProvider();

    await waitFor(() => {
      expect(screen.queryByText("Responsibility")).not.toBeInTheDocument();
      expect(screen.queryByText("Reviewer")).not.toBeInTheDocument();
    });
  });
});

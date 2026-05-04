import { render, screen, waitFor } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";

import { AuthContext, AuthContextType } from "../../components/Auth/Login/AuthProvider";
import { PermissionGroup } from "../../PermissionGroup";
import { getUnreadMessages } from "../../components/requests/conversation/Conversation";

import { Header } from "./Header";

jest.mock("../../components/requests/conversation/Conversation", () => ({
  getUnreadMessages: jest.fn(),
}));

const renderWithAuth = () => {
  render(
    <BrowserRouter>
      <AuthContext.Provider
        value={
          {
            isAuthenticated: true,
            isArmUser: () => true,
            isProjectDeveloperUser: () => false,
            isVvbUser: () => false,
            user: {
              groups: [PermissionGroup.ARM_TEAM],
              givenName: "Jane",
              familyName: "Doe",
            },
          } as AuthContextType
        }
      >
        <Header />
      </AuthContext.Provider>
    </BrowserRouter>,
  );
};

describe("Header", () => {
  beforeEach(() => {
    (getUnreadMessages as jest.Mock).mockResolvedValue([
      {
        requestId: 1,
        conversationId: 1,
        unreadCount: 3,
      },
      {
        requestId: 2,
        conversationId: 2,
        unreadCount: 5,
      },
    ]);
  });

  afterEach(() => jest.clearAllMocks());

  it("should provide navigation options", async () => {
    renderWithAuth();
    await waitFor(() => {
      expect(screen.getByText("My Requests")).toBeInTheDocument();
      expect(screen.getByText("All Requests")).toBeInTheDocument();
      expect(screen.getByText("Dashboard")).toBeInTheDocument();
      expect(screen.getByText("Projects")).toBeInTheDocument();
      expect(screen.getByText("Platform Accounts")).toBeInTheDocument();
      expect(screen.getByText("Consultations")).toBeInTheDocument();
      expect(screen.getByText("Admin")).toBeInTheDocument();
    });
  });

  it("should show unread messages count if available", async () => {
    renderWithAuth();
    await waitFor(() => {
      expect(screen.getByTestId("unread-messages-button")).toBeInTheDocument();
      expect(screen.getByTestId("unread-messages-badge")).toBeInTheDocument();
    });
  });

  it("should provide access to the user menu", async () => {
    renderWithAuth();
    await waitFor(() => {
      expect(screen.getByText("Jane Doe")).toBeInTheDocument();
    });
  });
});

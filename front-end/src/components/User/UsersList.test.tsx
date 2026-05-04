import { act, fireEvent, render, screen, waitFor } from "@testing-library/react";
import { HelmetProvider } from "react-helmet-async";
import { BrowserRouter } from "react-router-dom";

import { PermissionGroup } from "../../PermissionGroup";
import { AuthContext, AuthContextType } from "../Auth/Login/AuthProvider";

import { getUsers } from "./User";
import { UsersList } from "./UsersList";

jest.mock("./User", () => ({
  ...jest.requireActual("./User"),
  getUsers: jest.fn(),
}));

describe("UserList", () => {
  const mockGetUsersResponse = {
    content: [
      {
        id: 1,
        userName: "smithy",
        emailAddress: "smithy@example.com",
        isActive: true,
        account: {
          id: 1,
          name: "Test Company Ltd",
          emailAddress: "tcompany@example.com",
          active: true,
          address: "123 Test Street",
          accountType: {
            id: 1,
            code: "ARM_TEAM",
            description: "ARM-Team",
            isActive: true,
          },
        },
        givenName: "Jane",
        familyName: "Smith",
        groups: [PermissionGroup.ARM_TEAM],
      },
    ],
    currentPage: 1,
    totalPages: 1,
  };

  it("should display the list of users", async () => {
    (getUsers as jest.Mock).mockResolvedValue(mockGetUsersResponse);
    render(
      <HelmetProvider>
        <AuthContext.Provider
          value={
            {
              isAdminUser: () => true,
            } as AuthContextType
          }
        >
          <BrowserRouter>
            <UsersList />
          </BrowserRouter>
        </AuthContext.Provider>
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(screen.getByText("smithy")).toBeInTheDocument();
      expect(screen.getByText("Jane Smith")).toBeInTheDocument();
      expect(screen.getByText("smithy@example.com")).toBeInTheDocument();
      expect(screen.getByText("Test Company Ltd")).toBeInTheDocument();
      expect(screen.getByText("ARM-Team")).toBeInTheDocument();
    });
  });

  it("should create manual filter by active users in API call", async () => {
    (getUsers as jest.Mock).mockResolvedValue(mockGetUsersResponse);
    render(
      <HelmetProvider>
        <AuthContext.Provider
          value={
            {
              isAdminUser: () => true,
            } as AuthContextType
          }
        >
          <BrowserRouter>
            <UsersList />
          </BrowserRouter>
        </AuthContext.Provider>
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(getUsers).toHaveBeenCalledWith(1, 10, [], "", undefined);
    });

    fireEvent.click(screen.getByRole("button", { name: /Active/i }));
    fireEvent.click(await screen.findByText("Filter by Active"));

    await waitFor(() => {
      expect(getUsers).toHaveBeenCalledWith(1, 10, [], "", true);
    });
  });

  it("should create manual search by username or email in API call", async () => {
    (getUsers as jest.Mock).mockResolvedValue(mockGetUsersResponse);
    render(
      <HelmetProvider>
        <AuthContext.Provider
          value={
            {
              isAdminUser: () => true,
            } as AuthContextType
          }
        >
          <BrowserRouter>
            <UsersList />
          </BrowserRouter>
        </AuthContext.Provider>
      </HelmetProvider>,
    );

    const searchInput = await screen.findByPlaceholderText("Username or Email");

    act(() => {
      fireEvent.change(searchInput, { target: { value: "smithy" } });
    });

    await waitFor(() => {
      expect(getUsers).toHaveBeenCalledWith(1, 10, [], "smithy", undefined);
    });
  });
});

import { fireEvent, render, screen } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";

import { AuthContext, AuthContextType } from "../../components/Auth/Login/AuthProvider";
import { PermissionGroup } from "../../PermissionGroup";

import { UserMenu } from "./UserMenu";

describe("NavMenu", () => {
  describe("logged out", () => {
    it("displays login when user is logged out", () => {
      render(
        <BrowserRouter>
          <AuthContext.Provider
            value={
              {
                isAuthenticated: false,
              } as AuthContextType
            }
          >
            <UserMenu />
          </AuthContext.Provider>
        </BrowserRouter>,
      );

      expect(screen.getByText("Login")).toBeInTheDocument();
    });
  });

  describe("logged in", () => {
    beforeEach(() => {
      render(
        <AuthContext.Provider
          value={
            {
              isAuthenticated: true,
              user: {
                givenName: "Jane",
                familyName: "Doe",
                account: {
                  name: "Test Developer",
                },
                groups: [PermissionGroup.PROJECT_DEVELOPER],
              },
            } as AuthContextType
          }
        >
          <UserMenu />
        </AuthContext.Provider>,
      );
    });

    it("should see the relevant account name", () => {
      expect(screen.getByText("Jane Doe")).toBeInTheDocument();
    });

    it("should see the relevant platform account and role in dropdown", () => {
      fireEvent.click(screen.getByText("Jane Doe"));
      expect(screen.getByText("Project-Developer")).toBeInTheDocument();
      expect(screen.getByText("Test Developer")).toBeInTheDocument();
    });
  });
});

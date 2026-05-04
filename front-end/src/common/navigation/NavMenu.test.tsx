import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";

import { AuthContext, AuthContextType } from "../../components/Auth/Login/AuthProvider";
import { User } from "../../components/User/User";
import { PermissionGroup } from "../../PermissionGroup";

import { NavMenu } from "./NavMenu";

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
}));

describe("NavMenu", () => {
  beforeEach(() => {
    render(
      <AuthContext.Provider
        value={
          {
            isAuthenticated: true,
            user: {
              groups: [PermissionGroup.GS_ADMIN],
            } as User,
          } as AuthContextType
        }
      >
        <MemoryRouter>
          <NavMenu
            validNavbarRoutes={[
              {
                title: "Platform Accounts",
                path: "/accounts",
                access: [PermissionGroup.GS_ADMIN],
              },
            ]}
          />
        </MemoryRouter>
      </AuthContext.Provider>,
    );
  });

  it('GS-Admin should see "Platform Accounts"', () => {
    expect(screen.getByText("Platform Accounts")).toBeInTheDocument();
  });
});

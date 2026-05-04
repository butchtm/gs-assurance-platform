import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { HelmetProvider } from "react-helmet-async";

import { PermissionGroup } from "../../PermissionGroup";
import { isFormSandboxEnabled } from "../../common/env/env";
import { AuthContext, AuthContextType } from "../Auth/Login/AuthProvider";

import { AdministrationLandingPage } from "./AdministrationLandingPage";

jest.mock("../../common/env/env", () => ({
  isFormSandboxEnabled: jest.fn(),
}));

describe("AdministrationLandingPage", () => {
  const buildUserAuthContext = (permissionGroup: PermissionGroup) => {
    return {
      isAuthenticated: true,
      user: {
        groups: [permissionGroup],
      },
    } as AuthContextType;
  };

  it("should render a drawer with reporting options visible", async () => {
    render(
      <HelmetProvider>
        <AdministrationLandingPage />
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(screen.queryByRole("button", { name: "Reporting" })).toBeInTheDocument();
      expect(screen.queryByRole("button", { name: "Status Report" })).toBeInTheDocument();
    });
  });

  it("should render a drawer with only reporting options visible for non-admins", async () => {
    render(
      <HelmetProvider>
        <AuthContext.Provider value={buildUserAuthContext(PermissionGroup.ARM_TEAM)}>
          <AdministrationLandingPage />
        </AuthContext.Provider>
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(screen.queryByRole("button", { name: "Reference Types" })).not.toBeInTheDocument();
      expect(screen.queryByRole("button", { name: "Methodologies" })).not.toBeInTheDocument();
      expect(screen.queryByRole("button", { name: "Reporting" })).toBeInTheDocument();
      expect(screen.queryByRole("button", { name: "Status Report" })).toBeInTheDocument();
      expect(screen.queryByRole("button", { name: "Simple Form" })).not.toBeInTheDocument();
    });
  });

  it("should render a drawer with all options visible for admin users", async () => {
    (isFormSandboxEnabled as jest.Mock).mockReturnValue(true);

    render(
      <HelmetProvider>
        <AuthContext.Provider value={buildUserAuthContext(PermissionGroup.GS_ADMIN)}>
          <AdministrationLandingPage />
        </AuthContext.Provider>
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(screen.queryByRole("button", { name: "Reference Types" })).toBeInTheDocument();
      expect(screen.queryByRole("button", { name: "Methodologies" })).toBeInTheDocument();
      expect(screen.queryByRole("button", { name: "Reporting" })).toBeInTheDocument();
      expect(screen.queryByRole("button", { name: "Status Report" })).toBeInTheDocument();
      expect(screen.queryByRole("button", { name: "Simple Form" })).toBeInTheDocument();
      expect(screen.queryByRole("button", { name: "Stepper Form" })).toBeInTheDocument();
    });
  });

  it("should not render form sandbox option if not configured", async () => {
    (isFormSandboxEnabled as jest.Mock).mockReturnValue(false);

    render(
      <HelmetProvider>
        <AuthContext.Provider value={buildUserAuthContext(PermissionGroup.GS_ADMIN)}>
          <AdministrationLandingPage />
        </AuthContext.Provider>
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(screen.queryByRole("button", { name: "Form Sandbox" })).not.toBeInTheDocument();
    });
  });

  it("should be able to close and re-open the drawer", async () => {
    const { container } = render(
      <HelmetProvider>
        <AdministrationLandingPage />
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(container.querySelector(".MuiPaper-root")).toBeVisible();
    });

    fireEvent.click(screen.getByTestId("close-drawer"));

    await waitFor(() => {
      expect(container.querySelector(".MuiPaper-root")).not.toBeVisible();
    });

    fireEvent.click(screen.getByTestId("open-drawer"));

    await waitFor(() => {
      expect(container.querySelector(".MuiPaper-root")).toBeVisible();
    });
  });
});

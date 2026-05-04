import { render, screen, waitFor } from "@testing-library/react";

import { PermissionGroup } from "../../PermissionGroup";
import { AuthContext, AuthContextType } from "../../components/Auth/Login/AuthProvider";
import { mockPiiActions } from "../data-types/DebugReferenceTypes";

import { PiiActionDropdown } from "./PiiActionDropdown";

const renderAsAdmin = () => {
  return renderAsRole([PermissionGroup.GS_ADMIN]);
};

const renderAsArm = () => {
  return renderAsRole([PermissionGroup.ARM_TEAM]);
};

const renderAsFinance = () => {
  return renderAsRole([PermissionGroup.FINANCE]);
};

const renderAsRole = (permissionGroups: PermissionGroup[]) => {
  return render(
    <AuthContext.Provider
      value={
        {
          isAuthenticated: true,
          user: {
            groups: permissionGroups,
          },
        } as AuthContextType
      }
    >
      <PiiActionDropdown
        supportedActions={mockPiiActions}
        selectedAction={mockPiiActions[0]}
        onActionChange={jest.fn()}
      />
    </AuthContext.Provider>,
  );
};

describe("PiiActionDropdown", () => {
  it("should show dropdown with preselected value for admin user", async () => {
    renderAsAdmin();

    await waitFor(() => {
      expect(screen.getByRole("combobox", { name: "Action" })).toHaveValue("Under Investigation");
    });
  });

  it("should show dropdown with preselected value for arm user", async () => {
    renderAsArm();

    await waitFor(() => {
      expect(screen.getByRole("combobox", { name: "Action" })).toHaveValue("Under Investigation");
    });
  });

  it("should not show dropdown for non arm and non admin user", async () => {
    renderAsFinance();

    await waitFor(() => {
      expect(screen.queryByRole("combobox", { name: "Action" })).not.toBeInTheDocument();
    });
  });
});

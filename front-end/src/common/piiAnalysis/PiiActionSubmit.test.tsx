import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { toast } from "react-toastify";

import { AuthContext, AuthContextType } from "../../components/Auth/Login/AuthProvider";
import { PermissionGroup } from "../../PermissionGroup";
import { mockPiiActions } from "../data-types/DebugReferenceTypes";

import { submitPiiAction } from "./PiiAction";
import { PiiActionSubmit } from "./PiiActionSubmit";

jest.mock("./PiiAction", () => ({
  submitPiiAction: jest.fn(),
}));

jest.mock("react-toastify", () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

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
      <PiiActionSubmit selectedAction={mockPiiActions[0]} />
    </AuthContext.Provider>,
  );
};

describe("PiiActionSubmit", () => {
  it("should toast success if action request succeeds", async () => {
    (submitPiiAction as jest.Mock).mockResolvedValue({ success: true });
    renderAsAdmin();

    fireEvent.click(screen.getByRole("button", { name: "Submit" }));

    await waitFor(() => {
      expect(toast.success).toHaveBeenCalledWith("PII action submitted successfully.");
    });
  });

  it("should toast error if action request fails", async () => {
    (submitPiiAction as jest.Mock).mockResolvedValue({ success: false });
    renderAsAdmin();

    fireEvent.click(screen.getByRole("button", { name: "Submit" }));

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith("Failed to submit PII action. Please try again.");
    });
  });

  it("should show button for admin user", async () => {
    renderAsAdmin();

    await waitFor(() => {
      expect(screen.getByRole("button", { name: "Submit" })).toBeInTheDocument();
    });
  });

  it("should show button for arm user", async () => {
    renderAsArm();

    await waitFor(() => {
      expect(screen.getByRole("button", { name: "Submit" })).toBeInTheDocument();
    });
  });

  it("should not show button for non arm and non admin user", async () => {
    renderAsFinance();

    await waitFor(() => {
      expect(screen.queryByRole("button", { name: "Submit" })).not.toBeInTheDocument();
    });
  });
});

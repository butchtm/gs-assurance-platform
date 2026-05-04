import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { adminUpdateMfaSettings, getMfaSettings } from "./MultiFactorSettings";
import UserMultiFactorSettingsModal from "./UserMultiFactorSettingsModal";

jest.mock("./MultiFactorSettings", () => ({
  getMfaSettings: jest.fn(),
  adminUpdateMfaSettings: jest.fn(),
}));

jest.mock("react-toastify", () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

describe("UserMultiFactorSettingsModal - Admin Mode", () => {
  const mockOnClose = jest.fn();
  const testUsername = "testuser";

  const mockMfaSettings = {
    softwareTokenMfaEnabled: false,
    emailMfaEnabled: false,
  };

  beforeEach(() => {
    jest.clearAllMocks();
    (getMfaSettings as jest.Mock).mockResolvedValue(mockMfaSettings);
    (adminUpdateMfaSettings as jest.Mock).mockResolvedValue({ success: true, ...mockMfaSettings });
  });

  it("should render admin mode modal with correct title and content", async () => {
    render(
      <UserMultiFactorSettingsModal isOpen={true} onClose={mockOnClose} isAdminMode={true} username={testUsername} />,
    );

    await waitFor(() => {
      expect(screen.getByText(`Admin: Multi Factor Authentication Settings for ${testUsername}`)).toBeInTheDocument();
    });

    expect(screen.getByText(/As an administrator, you can manage/)).toBeInTheDocument();
    expect(screen.getByText("Software Token MFA (Via Authenticator App)")).toBeInTheDocument();
    expect(screen.getByText("Admin can only disable, not set up new tokens")).toBeInTheDocument();
  });

  it("should prevent admin from enabling software token MFA", async () => {
    render(
      <UserMultiFactorSettingsModal isOpen={true} onClose={mockOnClose} isAdminMode={true} username={testUsername} />,
    );

    await waitFor(() => {
      expect(screen.getByTestId("admin-software-token-mfa-toggle")).toBeInTheDocument();
    });

    const softwareTokenToggle = screen.getByTestId("admin-software-token-mfa-toggle").querySelector("input");

    await waitFor(() => {
      if (softwareTokenToggle) {
        fireEvent.click(softwareTokenToggle);
      }
    });

    expect(screen.queryByTestId("software-token-setup")).not.toBeInTheDocument();
  });

  it("should show warning when all MFA methods are disabled in admin mode", async () => {
    render(
      <UserMultiFactorSettingsModal isOpen={true} onClose={mockOnClose} isAdminMode={true} username={testUsername} />,
    );

    await waitFor(() => {
      expect(screen.getByText(/Warning: Disabling all MFA methods/)).toBeInTheDocument();
    });
  });
});

import { render, screen, waitFor } from "@testing-library/react";

import { getMfaSettings, updateMfaSettings } from "./MultiFactorSettings";
import UserMultiFactorSettingsModal from "./UserMultiFactorSettingsModal";

jest.mock("./MultiFactorSettings", () => ({
  getMfaSettings: jest.fn(),
  updateMfaSettings: jest.fn(),
}));

jest.mock("react-toastify", () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

describe("UserMultiFactorSettingsModal - User Mode", () => {
  const mockOnClose = jest.fn();

  const mockMfaSettings = {
    softwareTokenMfaEnabled: false,
    emailMfaEnabled: false,
  };

  beforeEach(() => {
    jest.clearAllMocks();
    (getMfaSettings as jest.Mock).mockResolvedValue(mockMfaSettings);
    (updateMfaSettings as jest.Mock).mockResolvedValue({ success: true, ...mockMfaSettings });
  });

  it("should render user mode modal with correct title and content", async () => {
    render(<UserMultiFactorSettingsModal isOpen={true} onClose={mockOnClose} isAdminMode={false} />);

    await waitFor(() => {
      expect(screen.getByText("Multi Factor Authentication Settings")).toBeInTheDocument();
    });

    expect(screen.getByText(/Enabling 2 Factor Authentication on your account/)).toBeInTheDocument();
    expect(screen.getByText("Enable Application Based MFA (Via Authenticator App)")).toBeInTheDocument();
  });

  it("should load MFA settings on open", async () => {
    render(<UserMultiFactorSettingsModal isOpen={true} onClose={mockOnClose} isAdminMode={false} />);

    await waitFor(() => {
      expect(getMfaSettings).toHaveBeenCalledWith(undefined);
    });
  });

  it("should show warning when all MFA methods are disabled", async () => {
    render(<UserMultiFactorSettingsModal isOpen={true} onClose={mockOnClose} isAdminMode={false} />);

    await waitFor(() => {
      expect(screen.getByText(/Warning: Disabling all MFA methods/)).toBeInTheDocument();
    });
  });
});

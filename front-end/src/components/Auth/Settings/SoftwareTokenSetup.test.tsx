import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { getMfaSettings, setupSoftwareToken } from "./MultiFactorSettings";
import UserMultiFactorSettingsModal from "./UserMultiFactorSettingsModal";

jest.mock("./MultiFactorSettings", () => ({
  getMfaSettings: jest.fn(),
  setupSoftwareToken: jest.fn(),
}));

jest.mock("react-toastify", () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

jest.mock("./SoftwareTokenSetup", () => {
  return function MockSoftwareTokenSetup({
    onVerificationComplete,
    onCancel,
  }: {
    onVerificationComplete: () => void;
    onCancel: () => void;
  }) {
    return (
      <div data-testid="software-token-setup">
        <button onClick={onVerificationComplete} data-testid="verify-complete">
          Complete Verification
        </button>
        <button onClick={onCancel} data-testid="cancel-setup">
          Cancel Setup
        </button>
      </div>
    );
  };
});

describe("UserMultiFactorSettingsModal - QR Code Setup Flow", () => {
  const mockOnClose = jest.fn();

  const mockMfaSettings = {
    softwareTokenMfaEnabled: false,
    emailMfaEnabled: false,
  };

  const mockSoftwareTokenSetupResponse = {
    secretCode: "JBSWY3DPEHPK3PXP", //fake secret code
    session: "test-session-123",
    otpauthUrl: "otpauth://totp/test?secret=JBSWY3DPEHPK3PXP",
  };

  beforeEach(() => {
    jest.clearAllMocks();
    (getMfaSettings as jest.Mock).mockResolvedValue(mockMfaSettings);
    (setupSoftwareToken as jest.Mock).mockResolvedValue(mockSoftwareTokenSetupResponse);
  });

  it("should enable software token MFA and show QR setup", async () => {
    render(<UserMultiFactorSettingsModal isOpen={true} onClose={mockOnClose} isAdminMode={false} />);

    await waitFor(() => {
      expect(screen.getByTestId("software-token-mfa-toggle")).toBeInTheDocument();
    });

    const softwareTokenToggle = screen.getByTestId("software-token-mfa-toggle").querySelector("input");

    await waitFor(() => {
      if (softwareTokenToggle) {
        fireEvent.click(softwareTokenToggle);
      }
    });

    await waitFor(() => {
      expect(setupSoftwareToken).toHaveBeenCalled();
      expect(screen.getByTestId("software-token-setup")).toBeInTheDocument();
    });
  });

  it("should handle QR code verification completion", async () => {
    render(<UserMultiFactorSettingsModal isOpen={true} onClose={mockOnClose} isAdminMode={false} />);

    await waitFor(() => {
      expect(screen.getByTestId("software-token-mfa-toggle")).toBeInTheDocument();
    });

    const softwareTokenToggle = screen.getByTestId("software-token-mfa-toggle").querySelector("input");

    await waitFor(() => {
      if (softwareTokenToggle) {
        fireEvent.click(softwareTokenToggle);
      }
    });

    await waitFor(() => {
      expect(screen.getByTestId("software-token-setup")).toBeInTheDocument();
    });

    const completeButton = screen.getByTestId("verify-complete");
    await waitFor(() => {
      fireEvent.click(completeButton);
    });

    await waitFor(() => {
      expect(getMfaSettings).toHaveBeenCalledTimes(2);
    });
  });
});

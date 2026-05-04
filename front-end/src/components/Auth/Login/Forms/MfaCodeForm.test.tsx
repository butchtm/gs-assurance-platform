import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

import MfaCodeForm, { MfaType } from "./MfaCodeForm";

const renderMfaCodeForm = (props = {}) => {
  const defaultProps = {
    challengeType: "SOFTWARE_TOKEN_MFA",
    username: "testuser",
    isLoading: false,
    submitMfaCode: jest.fn(),
    mfaError: undefined,
    onMfaTypeSelection: jest.fn(),
    availableMfaMethods: [MfaType.SOFTWARE_TOKEN_MFA],
    resetLogin: jest.fn(),
  };

  return render(<MfaCodeForm {...defaultProps} {...props} />);
};

describe("MfaCodeForm", () => {
  let mockSubmitMfaCode: jest.Mock;

  beforeEach(() => {
    mockSubmitMfaCode = jest.fn();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it("renders authenticator form correctly", () => {
    renderMfaCodeForm({
      challengeType: MfaType.SOFTWARE_TOKEN_MFA,
      submitMfaCode: mockSubmitMfaCode,
    });

    expect(screen.getByText("Enter Authenticator Code")).toBeInTheDocument();
    expect(screen.getByText("Please enter the 6-digit code from your authenticator app.")).toBeInTheDocument();
    expect(screen.getByLabelText("Verification Code")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Verify Code" })).toBeInTheDocument();
    expect(screen.queryByText("Having trouble?")).not.toBeInTheDocument();
  });

  it("renders email OTP form correctly", () => {
    renderMfaCodeForm({
      challengeType: MfaType.EMAIL_OTP,
      submitMfaCode: mockSubmitMfaCode,
    });

    expect(screen.getByText("Enter Email Verification Code")).toBeInTheDocument();
    expect(screen.getByText("Please enter the verification code sent to your email address.")).toBeInTheDocument();
    expect(screen.getByLabelText("Verification Code")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Verify Code" })).toBeInTheDocument();
    expect(screen.queryByText("Having trouble?")).not.toBeInTheDocument();
  });

  it("submits form with valid verification code", async () => {
    const user = userEvent.setup();
    renderMfaCodeForm({
      username: "testuser",
      submitMfaCode: mockSubmitMfaCode,
    });

    const codeInput = screen.getByLabelText("Verification Code");
    const submitButton = screen.getByRole("button", { name: "Verify Code" });

    await user.type(codeInput, "123456");
    await user.click(submitButton);

    expect(mockSubmitMfaCode).toHaveBeenCalledWith("testuser", "123456");
  });

  it("displays validation error for empty code", async () => {
    const user = userEvent.setup();
    renderMfaCodeForm({ submitMfaCode: mockSubmitMfaCode });

    await user.keyboard("{Tab}");
    await user.keyboard("{Tab}");
    await user.keyboard("{Enter}");

    await waitFor(() => {
      expect(screen.getByText("Verification code is required")).toBeInTheDocument();
    });

    expect(mockSubmitMfaCode).not.toHaveBeenCalled();
  });

  it("displays validation error for code too short", async () => {
    const user = userEvent.setup();
    renderMfaCodeForm({ submitMfaCode: mockSubmitMfaCode });

    const codeInput = screen.getByLabelText("Verification Code");
    const submitButton = screen.getByRole("button", { name: "Verify Code" });

    await user.type(codeInput, "123");
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText("Verification code must be at least 6 characters")).toBeInTheDocument();
    });

    expect(mockSubmitMfaCode).not.toHaveBeenCalled();
  });

  it("validates code field on blur", async () => {
    const user = userEvent.setup();
    renderMfaCodeForm({ submitMfaCode: mockSubmitMfaCode });

    const codeInput = screen.getByLabelText("Verification Code");

    await user.click(codeInput);
    await user.tab();

    await waitFor(() => {
      expect(screen.getByText("Verification code is required")).toBeInTheDocument();
    });
  });

  it("displays MFA error message", () => {
    renderMfaCodeForm({
      submitMfaCode: mockSubmitMfaCode,
      mfaError: "Invalid verification code",
    });

    expect(screen.getByText("Verification Failed")).toBeInTheDocument();
    expect(screen.getByText("Verification code is incorrect")).toBeInTheDocument();
  });

  it("disables submit button when form is invalid after blur validation", async () => {
    const user = userEvent.setup();
    renderMfaCodeForm({ submitMfaCode: mockSubmitMfaCode });

    const codeInput = screen.getByLabelText("Verification Code");
    const submitButton = screen.getByRole("button", { name: "Verify Code" });

    await user.type(codeInput, "123");
    await user.tab();

    await waitFor(() => {
      expect(screen.getByText("Verification code must be at least 6 characters")).toBeInTheDocument();
    });

    expect(submitButton).toBeDisabled();
  });

  it("enables submit button when form is valid", async () => {
    const user = userEvent.setup();
    renderMfaCodeForm({ submitMfaCode: mockSubmitMfaCode });

    const codeInput = screen.getByLabelText("Verification Code");
    const submitButton = screen.getByRole("button", { name: "Verify Code" });

    await user.type(codeInput, "123456");

    expect(submitButton).not.toBeDisabled();
  });

  it("displays select MFA verification method form if challengeType is SELECT_MFA_TYPE", () => {
    renderMfaCodeForm({
      challengeType: MfaType.SELECT_MFA_TYPE,
      availableMfaMethods: [MfaType.SOFTWARE_TOKEN_MFA, MfaType.EMAIL_OTP],
    });

    expect(screen.getByText("Select Verification Method")).toBeInTheDocument();
    expect(screen.queryByText("Enter Authenticator Code")).not.toBeInTheDocument();
    expect(screen.queryByText("Enter Email Verification Code")).not.toBeInTheDocument();
  });

  it("allows restarting login process from authenticator form when other mfa method is available", () => {
    const resetLoginCallback = jest.fn();
    renderMfaCodeForm({
      challengeType: MfaType.SOFTWARE_TOKEN_MFA,
      availableMfaMethods: [MfaType.SOFTWARE_TOKEN_MFA, MfaType.EMAIL_OTP],
      resetLogin: resetLoginCallback,
    });

    expect(screen.getByText("Enter Authenticator Code")).toBeInTheDocument();
    expect(screen.getByText("Please enter the 6-digit code from your authenticator app.")).toBeInTheDocument();
    expect(screen.getByLabelText("Verification Code")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Verify Code" })).toBeInTheDocument();
    expect(screen.getByText("Having trouble?")).toBeInTheDocument();

    fireEvent.click(screen.getByText("Repeat login process to sign in another way"));

    expect(resetLoginCallback).toHaveBeenCalled();
  });

  it("allows restarting login process from email OTP form when other mfa method is available", () => {
    const resetLoginCallback = jest.fn();
    renderMfaCodeForm({
      challengeType: MfaType.EMAIL_OTP,
      availableMfaMethods: [MfaType.SOFTWARE_TOKEN_MFA, MfaType.EMAIL_OTP],
      resetLogin: resetLoginCallback,
    });

    expect(screen.getByText("Enter Email Verification Code")).toBeInTheDocument();
    expect(screen.getByText("Please enter the verification code sent to your email address.")).toBeInTheDocument();
    expect(screen.getByLabelText("Verification Code")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Verify Code" })).toBeInTheDocument();
    expect(screen.getByText("Having trouble?")).toBeInTheDocument();

    fireEvent.click(screen.getByText("Repeat login process to sign in another way"));

    expect(resetLoginCallback).toHaveBeenCalled();
  });
});

import { render, screen, fireEvent } from "@testing-library/react";

import MfaTypeSelectionForm from "./MfaTypeSelectionForm";
import { MfaType } from "./MfaCodeForm";

const onMfaTypeSelectionCallback = jest.fn();

describe("MfaTypeSelectionForm", () => {
  it("renders select verification method form correctly", () => {
    render(<MfaTypeSelectionForm onMfaTypeSelection={onMfaTypeSelectionCallback} />);

    expect(screen.getByText("Select Verification Method")).toBeInTheDocument();
    expect(
      screen.getByText(
        "Your account has multi-factor authentication enabled. Please choose from one of the verification methods below",
      ),
    ).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /authenticator app/i })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /email one-time-passcode/i })).toBeInTheDocument();
  });

  it("calls onMfaTypeSelection with SOFTWARE_TOKEN_MFA when Authenticator App button is clicked", () => {
    render(<MfaTypeSelectionForm onMfaTypeSelection={onMfaTypeSelectionCallback} />);

    const authAppButton = screen.getByRole("button", { name: /authenticator app/i });
    fireEvent.click(authAppButton);

    expect(onMfaTypeSelectionCallback).toHaveBeenCalledWith(MfaType.SOFTWARE_TOKEN_MFA);
  });

  it("calls onMfaTypeSelection with EMAIL_OTP when Email One-Time-Passcode button is clicked", () => {
    render(<MfaTypeSelectionForm onMfaTypeSelection={onMfaTypeSelectionCallback} />);

    const emailButton = screen.getByRole("button", { name: /email one-time-passcode/i });
    fireEvent.click(emailButton);

    expect(onMfaTypeSelectionCallback).toHaveBeenCalledWith(MfaType.EMAIL_OTP);
  });
});

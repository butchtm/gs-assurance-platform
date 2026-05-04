import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { useEffect } from "react";

import FinishAccountSetupForm from "./FinishAccountSetupForm";

jest.mock("react-password-checklist", () => {
  return function MockPasswordChecklist({
    onChange,
    value,
    valueAgain,
  }: {
    onChange: (isValid: boolean) => void;
    value: string;
    valueAgain: string;
  }) {
    const isValid = Boolean(
      value &&
      valueAgain &&
      value.length >= 8 &&
      value === valueAgain &&
      /[A-Z]/.test(value) &&
      /[a-z]/.test(value) &&
      /\d/.test(value) &&
      /[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]/.test(value),
    );

    useEffect(() => {
      onChange(isValid);
    }, [isValid, onChange]);

    return <div data-testid="password-checklist">Password requirements: {isValid ? "Valid" : "Invalid"}</div>;
  };
});

const renderFinishAccountSetupForm = (props = {}) => {
  const defaultProps = {
    submitAccountSetup: jest.fn(),
  };

  return render(<FinishAccountSetupForm {...defaultProps} {...props} />);
};

describe("FinishAccountSetupForm", () => {
  let mockSubmitAccountSetup: jest.Mock;

  beforeEach(() => {
    mockSubmitAccountSetup = jest.fn();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it("renders form with all required fields", () => {
    renderFinishAccountSetupForm({ submitAccountSetup: mockSubmitAccountSetup });

    expect(screen.getByText("Finish setting up your account")).toBeInTheDocument();
    expect(screen.getByLabelText(/First Name/)).toBeInTheDocument();
    expect(screen.getByLabelText(/Last Name/)).toBeInTheDocument();
    expect(screen.getByLabelText("New Password")).toBeInTheDocument();
    expect(screen.getByLabelText("Confirm New Password")).toBeInTheDocument();
    expect(screen.getByTestId("password-checklist")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Submit" })).toBeInTheDocument();
  });

  it("submit button is disabled initially", () => {
    renderFinishAccountSetupForm({ submitAccountSetup: mockSubmitAccountSetup });

    const submitButton = screen.getByRole("button", { name: "Submit" });
    expect(submitButton).toBeDisabled();
  });

  it("submit button is disabled when first name is missing", async () => {
    const user = userEvent.setup();
    renderFinishAccountSetupForm({ submitAccountSetup: mockSubmitAccountSetup });

    const lastNameInput = screen.getByLabelText(/Last Name/);
    const newPasswordInput = screen.getByLabelText("New Password");
    const confirmPasswordInput = screen.getByLabelText("Confirm New Password");
    const submitButton = screen.getByRole("button", { name: "Submit" });

    await user.type(lastNameInput, "Doe");
    await user.type(newPasswordInput, "Password123!");
    await user.type(confirmPasswordInput, "Password123!");

    await waitFor(() => {
      expect(submitButton).toBeDisabled();
    });
  });

  it("submit button is disabled when last name is missing", async () => {
    const user = userEvent.setup();
    renderFinishAccountSetupForm({ submitAccountSetup: mockSubmitAccountSetup });

    const firstNameInput = screen.getByLabelText(/First Name/);
    const newPasswordInput = screen.getByLabelText("New Password");
    const confirmPasswordInput = screen.getByLabelText("Confirm New Password");
    const submitButton = screen.getByRole("button", { name: "Submit" });

    await user.type(firstNameInput, "John");
    await user.type(newPasswordInput, "Password123!");
    await user.type(confirmPasswordInput, "Password123!");

    await waitFor(() => {
      expect(submitButton).toBeDisabled();
    });
  });

  it("submit button is disabled when password is invalid", async () => {
    const user = userEvent.setup();
    renderFinishAccountSetupForm({ submitAccountSetup: mockSubmitAccountSetup });

    const firstNameInput = screen.getByLabelText(/First Name/);
    const lastNameInput = screen.getByLabelText(/Last Name/);
    const newPasswordInput = screen.getByLabelText("New Password");
    const confirmPasswordInput = screen.getByLabelText("Confirm New Password");
    const submitButton = screen.getByRole("button", { name: "Submit" });

    await user.type(firstNameInput, "John");
    await user.type(lastNameInput, "Doe");
    await user.type(newPasswordInput, "weak");
    await user.type(confirmPasswordInput, "weak");

    await waitFor(() => {
      expect(submitButton).toBeDisabled();
    });
  });

  it("submit button is enabled when all fields are valid", async () => {
    const user = userEvent.setup();
    renderFinishAccountSetupForm({ submitAccountSetup: mockSubmitAccountSetup });

    const firstNameInput = screen.getByLabelText(/First Name/);
    const lastNameInput = screen.getByLabelText(/Last Name/);
    const newPasswordInput = screen.getByLabelText("New Password");
    const confirmPasswordInput = screen.getByLabelText("Confirm New Password");
    const submitButton = screen.getByRole("button", { name: "Submit" });

    await user.type(firstNameInput, "John");
    await user.type(lastNameInput, "Doe");
    await user.type(newPasswordInput, "Password123!");
    await user.type(confirmPasswordInput, "Password123!");

    await waitFor(() => {
      expect(submitButton).not.toBeDisabled();
    });
  });

  it("submits form with correct data", async () => {
    const user = userEvent.setup();
    renderFinishAccountSetupForm({ submitAccountSetup: mockSubmitAccountSetup });

    const firstNameInput = screen.getByLabelText(/First Name/);
    const lastNameInput = screen.getByLabelText(/Last Name/);
    const newPasswordInput = screen.getByLabelText("New Password");
    const confirmPasswordInput = screen.getByLabelText("Confirm New Password");
    const submitButton = screen.getByRole("button", { name: "Submit" });

    await user.type(firstNameInput, "John");
    await user.type(lastNameInput, "Doe");
    await user.type(newPasswordInput, "Password123!");
    await user.type(confirmPasswordInput, "Password123!");

    await waitFor(() => {
      expect(submitButton).not.toBeDisabled();
    });

    await user.click(submitButton);

    expect(mockSubmitAccountSetup).toHaveBeenCalledWith("Password123!", "John", "Doe");
  });

  it("updates password validation when passwords change", async () => {
    const user = userEvent.setup();
    renderFinishAccountSetupForm({ submitAccountSetup: mockSubmitAccountSetup });

    const newPasswordInput = screen.getByLabelText("New Password");
    const confirmPasswordInput = screen.getByLabelText("Confirm New Password");

    await user.type(newPasswordInput, "Password123!");
    await user.type(confirmPasswordInput, "Password123!");

    await waitFor(() => {
      expect(screen.getByText("Password requirements: Valid")).toBeInTheDocument();
    });
  });

  it("shows invalid password state when passwords don't match", async () => {
    const user = userEvent.setup();
    renderFinishAccountSetupForm({ submitAccountSetup: mockSubmitAccountSetup });

    const newPasswordInput = screen.getByLabelText("New Password");
    const confirmPasswordInput = screen.getByLabelText("Confirm New Password");

    await user.type(newPasswordInput, "Password123!");
    await user.type(confirmPasswordInput, "DifferentPassword123!");

    await waitFor(() => {
      expect(screen.getByText("Password requirements: Invalid")).toBeInTheDocument();
    });
  });

  it("prevents form submission with invalid form", async () => {
    const user = userEvent.setup();
    renderFinishAccountSetupForm({ submitAccountSetup: mockSubmitAccountSetup });

    const firstNameInput = screen.getByLabelText(/First Name/);

    await user.type(firstNameInput, "John");
    await user.keyboard("{Enter}");

    expect(mockSubmitAccountSetup).not.toHaveBeenCalled();
  });
});

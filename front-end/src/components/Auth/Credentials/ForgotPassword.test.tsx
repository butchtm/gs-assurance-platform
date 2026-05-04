import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { useEffect } from "react";
import { MemoryRouter } from "react-router-dom";
import { toast } from "react-toastify";

import AuthenticationRepository from "../../APIClient/AuthenticationRepository";

import ForgotPassword from "./ForgotPassword";

jest.mock("../../APIClient/AuthenticationRepository", () => ({
  forgotPassword: jest.fn(),
  confirmForgotPassword: jest.fn(),
}));

const mockNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
}));

jest.mock("react-toastify", () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

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

const renderForgotPassword = () => {
  return render(
    <MemoryRouter>
      <ForgotPassword />
    </MemoryRouter>,
  );
};

describe("ForgotPassword", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe("Email Input Stage", () => {
    it("renders email input form initially", () => {
      renderForgotPassword();

      expect(screen.getByText("Forgot Password")).toBeInTheDocument();
      expect(screen.getByLabelText("Email")).toBeInTheDocument();
      expect(screen.getByRole("button", { name: "Submit" })).toBeInTheDocument();
    });

    it("validates email format on blur", async () => {
      const user = userEvent.setup();
      renderForgotPassword();

      const emailInput = screen.getByLabelText("Email");
      await user.type(emailInput, "invalid-email");
      await user.tab();

      await waitFor(() => {
        expect(screen.getByText("Enter a valid email address")).toBeInTheDocument();
      });
    });

    it("accepts valid email format", async () => {
      const user = userEvent.setup();
      renderForgotPassword();

      const emailInput = screen.getByLabelText("Email");
      await user.type(emailInput, "test@example.com");
      await user.tab();

      await waitFor(() => {
        expect(screen.queryByText("Enter a valid email address")).not.toBeInTheDocument();
      });
    });

    it("submits email for password reset", async () => {
      const user = userEvent.setup();
      (AuthenticationRepository.forgotPassword as jest.Mock).mockResolvedValue({});
      renderForgotPassword();

      const emailInput = screen.getByLabelText("Email");
      const submitButton = screen.getByRole("button", { name: "Submit" });

      await user.type(emailInput, "test@example.com");
      await user.click(submitButton);

      expect(AuthenticationRepository.forgotPassword).toHaveBeenCalledWith("test@example.com");

      await waitFor(() => {
        expect(screen.getByText("Enter Verification Code")).toBeInTheDocument();
        expect(
          screen.getByText(
            "A password recovery email has been sent to test@example.com if an email address exists in the system.",
          ),
        ).toBeInTheDocument();
      });
    });

    it("disables submit button for invalid email", async () => {
      const user = userEvent.setup();
      renderForgotPassword();

      const emailInput = screen.getByLabelText("Email");
      const submitButton = screen.getByRole("button", { name: "Submit" });

      await user.type(emailInput, "invalid-email");

      await waitFor(() => {
        expect(submitButton).toBeDisabled();
      });
    });
  });

  describe("Code Input Stage", () => {
    beforeEach(async () => {
      const user = userEvent.setup();
      (AuthenticationRepository.forgotPassword as jest.Mock).mockResolvedValue({});
      renderForgotPassword();

      const emailInput = screen.getByLabelText("Email");
      const submitButton = screen.getByRole("button", { name: "Submit" });

      await user.type(emailInput, "test@example.com");
      await user.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText("Enter Verification Code")).toBeInTheDocument();
      });
    });

    it("renders password reset form after email submission", () => {
      expect(screen.getByText("Enter Verification Code")).toBeInTheDocument();
      expect(screen.getByLabelText("Verification Code")).toBeInTheDocument();
      expect(screen.getByLabelText("New Password")).toBeInTheDocument();
      expect(screen.getByLabelText("Confirm New Password")).toBeInTheDocument();
      expect(screen.getByTestId("password-checklist")).toBeInTheDocument();
      expect(screen.getByRole("button", { name: "Submit" })).toBeInTheDocument();
    });

    it("validates verification code is required", () => {
      const submitButton = screen.getByRole("button", { name: "Submit" });
      expect(submitButton).toBeDisabled();
    });

    it("validates new password is required", async () => {
      const user = userEvent.setup();
      const verificationCodeInput = screen.getByLabelText("Verification Code");
      const submitButton = screen.getByRole("button", { name: "Submit" });

      await user.type(verificationCodeInput, "123456");

      await waitFor(() => {
        expect(submitButton).toBeDisabled();
      });
    });

    it("validates confirm password is required", async () => {
      const user = userEvent.setup();
      const verificationCodeInput = screen.getByLabelText("Verification Code");
      const newPasswordInput = screen.getByLabelText("New Password");
      const submitButton = screen.getByRole("button", { name: "Submit" });

      await user.type(verificationCodeInput, "123456");
      await user.type(newPasswordInput, "Password123!");

      await waitFor(() => {
        expect(submitButton).toBeDisabled();
      });
    });

    it("validates passwords match", async () => {
      const user = userEvent.setup();
      const newPasswordInput = screen.getByLabelText("New Password");
      const confirmPasswordInput = screen.getByLabelText("Confirm New Password");

      await user.type(newPasswordInput, "Password123!");
      await user.type(confirmPasswordInput, "DifferentPassword123!");
      await user.tab();

      await waitFor(() => {
        expect(screen.getByText("Passwords do not match")).toBeInTheDocument();
      });
    });

    it("submits password reset successfully", async () => {
      const user = userEvent.setup();
      (AuthenticationRepository.confirmForgotPassword as jest.Mock).mockResolvedValue({});

      const verificationCodeInput = screen.getByLabelText("Verification Code");
      const newPasswordInput = screen.getByLabelText("New Password");
      const confirmPasswordInput = screen.getByLabelText("Confirm New Password");
      const submitButton = screen.getByRole("button", { name: "Submit" });

      await user.type(verificationCodeInput, "123456");
      await user.type(newPasswordInput, "Password123!");
      await user.type(confirmPasswordInput, "Password123!");

      await waitFor(() => {
        expect(submitButton).not.toBeDisabled();
      });

      await user.click(submitButton);

      expect(AuthenticationRepository.confirmForgotPassword).toHaveBeenCalledWith(
        "test@example.com",
        "Password123!",
        "123456",
      );

      await waitFor(() => {
        expect(toast.success).toHaveBeenCalledWith("Password reset successfully.");
        expect(mockNavigate).toHaveBeenCalledWith("/login");
      });
    });

    it("disables submit button when form is invalid", async () => {
      const user = userEvent.setup();
      const verificationCodeInput = screen.getByLabelText("Verification Code");
      const newPasswordInput = screen.getByLabelText("New Password");
      const submitButton = screen.getByRole("button", { name: "Submit" });

      await user.type(verificationCodeInput, "123456");
      await user.type(newPasswordInput, "weak");

      await waitFor(() => {
        expect(submitButton).toBeDisabled();
      });
    });
  });

  describe("Email Validation", () => {
    it("rejects email that is too long", async () => {
      const user = userEvent.setup();
      renderForgotPassword();

      const emailInput = screen.getByLabelText("Email");
      const longEmail = "a".repeat(250) + "@example.com";

      await user.type(emailInput, longEmail);
      await user.tab();

      await waitFor(() => {
        expect(screen.getByText("Enter a valid email address")).toBeInTheDocument();
      });
    });

    it("rejects email without @ symbol", async () => {
      const user = userEvent.setup();
      renderForgotPassword();

      const emailInput = screen.getByLabelText("Email");
      await user.type(emailInput, "notanemail.com");
      await user.tab();

      await waitFor(() => {
        expect(screen.getByText("Enter a valid email address")).toBeInTheDocument();
      });
    });

    it("rejects email with invalid domain", async () => {
      const user = userEvent.setup();
      renderForgotPassword();

      const emailInput = screen.getByLabelText("Email");
      await user.type(emailInput, "test@");
      await user.tab();

      await waitFor(() => {
        expect(screen.getByText("Enter a valid email address")).toBeInTheDocument();
      });
    });
  });
});

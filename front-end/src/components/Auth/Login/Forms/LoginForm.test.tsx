import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";

import LoginForm from "./LoginForm";

const renderLoginForm = (props = {}) => {
  const defaultProps = {
    isLoginLoading: false,
    submitLogin: jest.fn(),
    loginError: undefined,
    loginErrorTitle: "Login Failed",
  };

  return render(
    <MemoryRouter>
      <LoginForm {...defaultProps} {...props} />
    </MemoryRouter>,
  );
};

describe("LoginForm", () => {
  let mockSubmitLogin: jest.Mock;

  beforeEach(() => {
    mockSubmitLogin = jest.fn();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it("renders login form with all required fields", () => {
    renderLoginForm({ submitLogin: mockSubmitLogin });

    expect(screen.getByRole("heading", { name: "Login" })).toBeInTheDocument();
    expect(screen.getByLabelText("Username")).toBeInTheDocument();
    expect(screen.getByLabelText("Password")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Login" })).toBeInTheDocument();
    expect(screen.getByText("Forgotten password?")).toBeInTheDocument();
  });

  it("submits form with valid credentials", async () => {
    const user = userEvent.setup();
    renderLoginForm({ submitLogin: mockSubmitLogin });

    const usernameInput = screen.getByLabelText("Username");
    const passwordInput = screen.getByLabelText("Password");
    const submitButton = screen.getByRole("button", { name: "Login" });

    await user.type(usernameInput, "testuser");
    await user.type(passwordInput, "password123");
    await user.click(submitButton);

    expect(mockSubmitLogin).toHaveBeenCalledWith("testuser", "password123");
  });

  it("validates username field on blur", async () => {
    const user = userEvent.setup();
    renderLoginForm({ submitLogin: mockSubmitLogin });

    const usernameInput = screen.getByLabelText("Username");

    await user.click(usernameInput);
    await user.tab();

    await waitFor(() => {
      expect(screen.getByText("Username is required")).toBeInTheDocument();
    });
  });

  it("validates password field on blur", async () => {
    const user = userEvent.setup();
    renderLoginForm({ submitLogin: mockSubmitLogin });

    const passwordInput = screen.getByLabelText("Password");

    await user.click(passwordInput);
    await user.tab();

    await waitFor(() => {
      expect(screen.getByText("Password is required")).toBeInTheDocument();
    });
  });

  it("displays login error message", () => {
    renderLoginForm({
      submitLogin: mockSubmitLogin,
      loginError: "Invalid credentials",
    });

    expect(screen.getByText("Login Failed")).toBeInTheDocument();
    expect(screen.getByText("Invalid credentials")).toBeInTheDocument();
  });

  it("disables submit button when loading", () => {
    renderLoginForm({
      submitLogin: mockSubmitLogin,
      isLoginLoading: true,
    });

    const submitButton = screen.getByRole("button", { name: "Logging in..." });
    expect(submitButton).toBeDisabled();
  });

  it("disables submit button when form is invalid", async () => {
    const user = userEvent.setup();
    renderLoginForm({ submitLogin: mockSubmitLogin });

    const usernameInput = screen.getByLabelText("Username");
    const submitButton = screen.getByRole("button", { name: "Login" });

    await user.type(usernameInput, "testuser");

    expect(submitButton).toBeDisabled();
  });

  it("enables submit button when form is valid", async () => {
    const user = userEvent.setup();
    renderLoginForm({ submitLogin: mockSubmitLogin });

    const usernameInput = screen.getByLabelText("Username");
    const passwordInput = screen.getByLabelText("Password");
    const submitButton = screen.getByRole("button", { name: "Login" });

    await user.type(usernameInput, "testuser");
    await user.type(passwordInput, "password123");

    expect(submitButton).not.toBeDisabled();
  });

  it("navigates to forgot password page", () => {
    renderLoginForm({ submitLogin: mockSubmitLogin });

    const forgotPasswordLink = screen.getByText("Forgotten password?");
    expect(forgotPasswordLink.closest("a")).toHaveAttribute("href", "/forgot-password");
  });
});

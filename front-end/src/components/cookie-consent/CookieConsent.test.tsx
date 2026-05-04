import { fireEvent, render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";

import CookieConsent from "./CookieConsent";

describe("CookieConsent", () => {
  const mockLocalStorage = {
    getItem: jest.fn(),
    setItem: jest.fn(),
  };

  beforeAll(() => {
    Object.defineProperty(window, "localStorage", {
      value: mockLocalStorage,
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it("shows when no consent was previously given", () => {
    mockLocalStorage.getItem.mockReturnValue(null);
    render(
      <MemoryRouter>
        <CookieConsent />
      </MemoryRouter>,
    );
    expect(screen.getByTestId("cookie-consent")).toBeInTheDocument();
  });

  it("does not show when consent was previously given", () => {
    mockLocalStorage.getItem.mockReturnValue("accepted");
    render(
      <MemoryRouter>
        <CookieConsent />
      </MemoryRouter>,
    );
    expect(screen.queryByTestId("cookie-consent")).not.toBeInTheDocument();
  });

  it("when accept button is clicked values are stored", () => {
    mockLocalStorage.getItem.mockReturnValue(null);
    render(
      <MemoryRouter>
        <CookieConsent />
      </MemoryRouter>,
    );
    fireEvent.click(screen.getByText("Accept"));
    expect(mockLocalStorage.setItem).toHaveBeenCalledWith("cookie-consent", "accepted");
  });
});

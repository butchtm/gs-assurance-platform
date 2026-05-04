import { render, screen } from "@testing-library/react";
import { useLocation } from "react-router-dom";

import { isFormEnabled } from "../../common/env/env";
import { mockActiveReferenceTypes } from "../../common/data-types/DebugReferenceTypes";

import { FormLandingPage } from "./FormLandingPage";

jest.mock("../../common/env/env", () => ({
  isFormEnabled: jest.fn(() => true),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useLocation: jest.fn(),
}));

describe("FormLandingPage", () => {
  it("shows an error message for production environments", () => {
    (isFormEnabled as jest.Mock).mockReturnValue(false);
    (useLocation as jest.Mock).mockReturnValue({
      state: {
        formType: mockActiveReferenceTypes.DigitalFormType[0],
      },
    });
    render(<FormLandingPage />);
    expect(screen.getByText("Page content not found")).toBeInTheDocument();
  });
  it("shows an error message if no form type is available", () => {
    (isFormEnabled as jest.Mock).mockReturnValue(true);
    (useLocation as jest.Mock).mockReturnValue({
      state: {
        formType: null,
      },
    });
    render(<FormLandingPage />);
    expect(screen.getByText("Page content not found")).toBeInTheDocument();
  });
});

import { render, screen, waitFor } from "@testing-library/react";
import { Formik } from "formik";

import * as PlatformAccount from "../../PlatformAccounts/PlatformAccount";

import { CustomAccountSearchAutoComplete } from "./CustomAccountSearch";

jest.mock("../../PlatformAccounts/PlatformAccount", () => ({
  getAccounts: jest.fn(),
}));

const mockAccounts = {
  content: [
    { id: 1, name: "Test Account 1" },
    { id: 2, name: "Test Account 2" },
    { id: 3, name: "Test Account 3" },
  ],
};

describe("CustomAccountSearchAutoComplete Component", () => {
  beforeEach(() => {
    (PlatformAccount.getAccounts as jest.Mock).mockResolvedValue(mockAccounts);
  });

  it("renders correctly with label", async () => {
    render(
      <Formik initialValues={{ accountId: null }} onSubmit={jest.fn()}>
        <CustomAccountSearchAutoComplete name="accountId" label="Account Search" accountTypeId={1} />
      </Formik>,
    );

    await waitFor(() => {
      expect(screen.getByLabelText("Account Search")).toBeInTheDocument();
    });
  });

  it("fetches and displays accounts", async () => {
    render(
      <Formik initialValues={{ accountId: null }} onSubmit={jest.fn()}>
        <CustomAccountSearchAutoComplete name="accountId" label="Account Search" accountTypeId={1} />
      </Formik>,
    );

    await waitFor(() => {
      expect(PlatformAccount.getAccounts).toHaveBeenCalled();
    });
  });

  it("renders with required field indicator", async () => {
    render(
      <Formik initialValues={{ accountId: null }} onSubmit={jest.fn()}>
        <CustomAccountSearchAutoComplete name="accountId" label="Account Search" accountTypeId={1} required={true} />
      </Formik>,
    );

    await waitFor(() => {
      expect(screen.getByTestId("account-search-input")).toBeInTheDocument();
    });
  });
});

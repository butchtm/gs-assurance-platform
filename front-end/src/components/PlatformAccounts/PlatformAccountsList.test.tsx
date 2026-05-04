import { render, screen, waitFor } from "@testing-library/react";
import React from "react";
import { HelmetProvider } from "react-helmet-async";

import { mockActiveReferenceTypes } from "../../common/data-types/DebugReferenceTypes";
import { mockPlatformAccounts } from "../../common/platform-accounts/DebugPlatformAccounts";
import { AuthContext, AuthContextType } from "../Auth/Login/AuthProvider";

import * as PlatformAccount from "./PlatformAccount";
import { PlatformAccountsList } from "./PlatformAccountsList";

jest.mock("./PlatformAccount");
jest.mock("../../ProtectedComponent", () => {
  return ({ children }: { children: React.ReactNode }) => children;
});
jest.mock("./Modal/EditAccountModal", () => () => null);
jest.mock("../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../common/data-types/DataReferenceTypes"),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
}));
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
}));

describe("PlatformAccountsList", () => {
  it("renders accounts correctly", async () => {
    const mockGetAccounts = jest.spyOn(PlatformAccount, "getAccounts").mockResolvedValue({
      content: mockPlatformAccounts,
      totalPages: 1,
      pageNumber: 1,
      totalElements: mockPlatformAccounts.length,
    });

    render(
      <HelmetProvider>
        <AuthContext.Provider
          value={
            {
              isAuthenticated: true,
              isAdminUser: () => true,
              isArmUser: () => false,
            } as AuthContextType
          }
        >
          <PlatformAccountsList />
        </AuthContext.Provider>
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(mockGetAccounts).toHaveBeenCalledWith({}, 1, 10);
    });

    expect(await screen.findByText("Account One")).toBeInTheDocument();
  });

  it("doesn't allow creation of an account for ARM users", async () => {
    jest.spyOn(PlatformAccount, "getAccounts").mockResolvedValue({
      content: mockPlatformAccounts,
      totalPages: 1,
      pageNumber: 1,
      totalElements: mockPlatformAccounts.length,
    });

    render(
      <HelmetProvider>
        <AuthContext.Provider
          value={
            {
              isAuthenticated: true,
              isAdminUser: () => false,
              isArmUser: () => true,
            } as AuthContextType
          }
        >
          <PlatformAccountsList />
        </AuthContext.Provider>
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(screen.queryByText("Create")).not.toBeInTheDocument();
    });
  });

  it("allows creation of account for Admin users", async () => {
    jest.spyOn(PlatformAccount, "getAccounts").mockResolvedValue({
      content: mockPlatformAccounts,
      totalPages: 1,
      pageNumber: 1,
      totalElements: mockPlatformAccounts.length,
    });

    render(
      <HelmetProvider>
        <AuthContext.Provider
          value={
            {
              isAuthenticated: true,
              isAdminUser: () => true,
              isArmUser: () => false,
            } as AuthContextType
          }
        >
          <PlatformAccountsList />
        </AuthContext.Provider>
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(screen.getByText("Create")).toBeInTheDocument();
    });
  });
});

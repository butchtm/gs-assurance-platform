import React, { ReactElement } from "react";
import { render, RenderOptions } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { ThemeProvider } from "@emotion/react";
import { LocalizationProvider } from "@mui/x-date-pickers";

import { ReferenceTypeContext, ReferenceTypeContextType } from "@/common/data-types/DataReferenceTypes";
import { AuthContext, AuthContextType } from "@/components/Auth/Login/AuthProvider";
import { theme } from "@/theme";

type ProviderOverrides = {
  auth?: Partial<AuthContextType>;
  reference?: Partial<ReferenceTypeContextType>;
};

const AllTheProviders = ({ children, overrides }: { children: React.ReactNode; overrides?: ProviderOverrides }) => {
  const mockAuthProvider = {
    isProjectDeveloperUser: jest.fn(),
    isAdminUser: jest.fn(),
    isArmUser: jest.fn(),
    isRegistryUser: jest.fn(),
    isReviewerUser: jest.fn(),
    ...(overrides?.auth as Record<string, unknown>),
  } as Record<keyof AuthContextType, jest.Mock>;

  const defaultReferenceValue: ReferenceTypeContextType = {
    activeReferenceTypes: {},
    allReferenceTypes: {},
    inactiveReferenceTypes: {},
    referenceMethodologies: [],
    activeRequestTypes: [],
    refreshReferenceData: () => {},
    getCustomReferenceOptions: jest.fn().mockReturnValue([
      { value: 1, label: "Option 1" },
      { value: 2, label: "Option 2" },
    ]),
  };

  const mergedReferenceValue = {
    ...defaultReferenceValue,
    ...(overrides?.reference ?? {}),
  } as ReferenceTypeContextType;

  return (
    <MemoryRouter>
      <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
        <ThemeProvider theme={theme}>
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <ReferenceTypeContext.Provider value={mergedReferenceValue}>{children}</ReferenceTypeContext.Provider>
          </LocalizationProvider>
        </ThemeProvider>
      </AuthContext.Provider>
    </MemoryRouter>
  );
};

export const renderWithProviders = (
  ui: ReactElement,
  { providerOverrides, ...options }: { providerOverrides?: ProviderOverrides } & Omit<RenderOptions, "wrapper"> = {},
) =>
  render(ui, {
    wrapper: ({ children }) => <AllTheProviders overrides={providerOverrides}>{children}</AllTheProviders>,
    ...options,
  });

import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter, Route, Routes, useParams } from "react-router-dom";

import { AuthContext, AuthContextType } from "../../../Auth/Login/AuthProvider";

import { CreditingPeriodDetail } from "./CreditingPeriodDetail";

jest.mock("../../../../common/data-types/DataReferenceTypes", () => ({
  getReferenceValueDescriptionById: (id: string) => `Description for ${id}`,
  useReferenceTypes: () => ({
    activeReferenceTypes: {
      Methodology: [],
    },
  }),
}));

jest.mock("../../Project", () => ({
  getMethodologyHistory: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useParams: jest.fn(),
}));

describe("CreditingPeriodDetail Component", () => {
  const creditingPeriod = {
    periodId: 1,
    startDate: "2025-03-11T11:45:59.512Z",
    endDate: "2030-03-10T11:45:59.512Z",
    methodologies: [
      { id: "m1", version: "v1" },
      { id: "m2", version: "v2" },
    ],
  };

  const project = {
    name: "Test Name",
    projectName: "Test Name",
    gsid: "GSID123",
    accountId: 1,
    id: 1,
  };

  beforeEach(() => {
    (useParams as jest.Mock).mockReturnValue({ periodId: 1 });
  });

  it("renders methodologies table with data when state is provided", async () => {
    render(
      <AuthContext.Provider
        value={
          {
            isAdminUser: () => true,
          } as AuthContextType
        }
      >
        <MemoryRouter
          initialEntries={[
            {
              pathname: "/projects/creditingPeriods/1",
              state: { creditingPeriod: creditingPeriod, project: project },
            },
          ]}
        >
          <CreditingPeriodDetail />
        </MemoryRouter>
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.getByText(/Test Name/)).toBeInTheDocument();

      expect(screen.getByText(/GSID123/)).toBeInTheDocument();

      expect(screen.getByText(/11\/03\/2025/)).toBeInTheDocument();
      expect(screen.getByText(/10\/03\/2030/)).toBeInTheDocument();

      expect(screen.getByText("Description for m1")).toBeInTheDocument();
      expect(screen.getByText("v1")).toBeInTheDocument();
      expect(screen.getByText("Description for m2")).toBeInTheDocument();
      expect(screen.getByText("v2")).toBeInTheDocument();
    });
  });

  it("renders error message when no state is provided", () => {
    render(
      <AuthContext.Provider
        value={
          {
            isAdminUser: () => true,
          } as AuthContextType
        }
      >
        <MemoryRouter initialEntries={[{ pathname: "/projects/creditingPeriods/1" }]}>
          <Routes>
            <Route path="/projects/creditingPeriods/:periodId" element={<CreditingPeriodDetail />} />
          </Routes>
        </MemoryRouter>
      </AuthContext.Provider>,
    );

    expect(
      screen.getByText(/Navigate to specific crediting period methodologies through the project information screen./i),
    ).toBeInTheDocument();
  });

  it.each([
    {
      id: "Admin",
      mockContext: {
        isAdminUser: () => true,
        isArmUser: () => false,
        isProjectDeveloperUser: () => false,
        isSupportUser: () => false,
      } as AuthContextType,
    },
    {
      id: "ARM",
      mockContext: {
        isAdminUser: () => false,
        isArmUser: () => true,
        isProjectDeveloperUser: () => false,
        isSupportUser: () => false,
      } as AuthContextType,
    },
    {
      id: "Project Developer",
      mockContext: {
        isAdminUser: () => false,
        isArmUser: () => false,
        isProjectDeveloperUser: () => true,
        isUserOwnerOf: () => true,
        isSupportUser: () => false,
      } as unknown as AuthContextType,
    },
    {
      id: "Support",
      mockContext: {
        isAdminUser: () => false,
        isArmUser: () => false,
        isProjectDeveloperUser: () => false,
        isSupportUser: () => true,
      } as unknown as AuthContextType,
    },
  ])("should show methodology history for user %s", async (params) => {
    render(
      <AuthContext.Provider value={params.mockContext}>
        <MemoryRouter
          initialEntries={[
            {
              pathname: "/projects/creditingPeriods/1",
              state: { creditingPeriod: creditingPeriod, project: project },
            },
          ]}
        >
          <CreditingPeriodDetail />
        </MemoryRouter>
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.getByText("Methodology History")).toBeInTheDocument();
    });
  });

  it("should not show history for unauthenticated users", async () => {
    render(
      <AuthContext.Provider
        value={
          {
            isAdminUser: () => false,
            isArmUser: () => false,
            isProjectDeveloperUser: () => false,
            isSupportUser: () => false,
          } as AuthContextType
        }
      >
        <MemoryRouter
          initialEntries={[
            {
              pathname: "/projects/creditingPeriods/1",
              state: { creditingPeriod: creditingPeriod, project: project },
            },
          ]}
        >
          <CreditingPeriodDetail />
        </MemoryRouter>
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.queryByText("History")).not.toBeInTheDocument();
    });
  });
});

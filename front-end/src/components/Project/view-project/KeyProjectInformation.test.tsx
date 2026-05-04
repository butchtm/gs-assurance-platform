import { render, screen, waitFor } from "@testing-library/react";
import dayjs from "dayjs";

import { debugProject } from "../../../common/project/DebugProject";
import { debugRequests } from "../../../common/request/DebugRequests";
import { AuthContext, AuthContextType } from "../../Auth/Login/AuthProvider";

import { KeyProjectInformation } from "./KeyProjectInformation";

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
}));

jest.mock("../../requests/Requests", () => ({
  getRequestWithId: jest.fn().mockResolvedValue(debugRequests[0]),
}));

describe("KeyProjectInformation", () => {
  it("renders current crediting period when current date is within crediting period", async () => {
    const today = dayjs.utc();
    const currentStartDate = today.add(-1, "year");
    const currentEndDate = today.add(+1, "year");
    const currentCreditingPeriodProject = {
      ...debugProject,
      creditingPeriods: [
        {
          periodId: 1,
          startDate: currentStartDate.add(-2, "year").format("YYYY-MM-DD"),
          endDate: currentEndDate.add(-1, "year").format("YYYY-MM-DD"),
          methodologies: [],
        },
        {
          periodId: 2,
          startDate: currentStartDate.format("YYYY-MM-DD"),
          endDate: currentEndDate.format("YYYY-MM-DD"),
          methodologies: [],
        },
        {
          periodId: 3,
          startDate: currentStartDate.add(1, "year").format("YYYY-MM-DD"),
          endDate: currentEndDate.add(2, "year").format("YYYY-MM-DD"),
          methodologies: [],
        },
      ],
    };
    render(
      <AuthContext.Provider
        value={
          {
            isAdminUser: () => true,
            isArmUser: () => false,
            isProjectDeveloperUser: () => false,
            isUserOwnerOf: () => false,
          } as unknown as AuthContextType
        }
      >
        <KeyProjectInformation project={currentCreditingPeriodProject}></KeyProjectInformation>
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.getByText("Current Crediting Period")).toBeInTheDocument();
      expect(
        screen.getByText(
          currentStartDate.format("DD/MM/YYYY").toString() + " - " + currentEndDate.format("DD/MM/YYYY").toString(),
        ),
      ).toBeInTheDocument();
    });
  });

  it("renders latest crediting period when current date is NOT within any crediting period", async () => {
    const today = dayjs.utc();
    const latestStartDate = today.add(-2, "year");
    const latestEndDate = today.add(-1, "year");
    const latestCreditingPeriodProject = {
      ...debugProject,
      creditingPeriods: [
        {
          periodId: 1,
          startDate: latestStartDate.add(-2, "year").format("YYYY-MM-DD"),
          endDate: latestEndDate.add(-1, "year").format("YYYY-MM-DD"),
          methodologies: [],
        },
        {
          periodId: 2,
          startDate: latestStartDate.format("YYYY-MM-DD"),
          endDate: latestEndDate.format("YYYY-MM-DD"),
          methodologies: [],
        },
        {
          periodId: 3,
          startDate: today.add(1, "year").format("YYYY-MM-DD"),
          endDate: today.add(3, "year").format("YYYY-MM-DD"),
          methodologies: [],
        },
      ],
    };
    render(
      <AuthContext.Provider
        value={
          {
            isAdminUser: () => true,
            isArmUser: () => false,
            isProjectDeveloperUser: () => false,
            isUserOwnerOf: () => false,
          } as unknown as AuthContextType
        }
      >
        <KeyProjectInformation project={latestCreditingPeriodProject}></KeyProjectInformation>
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      expect(screen.getByText("Latest Crediting Period")).toBeInTheDocument();
      expect(
        screen.getByText(
          latestStartDate.format("DD/MM/YYYY").toString() + " - " + latestEndDate.format("DD/MM/YYYY").toString(),
        ),
      ).toBeInTheDocument();
    });
  });

  it("renders the project notes for the correct user", async () => {
    render(
      <AuthContext.Provider
        value={
          {
            isAdminUser: () => true,
            isArmUser: () => false,
            isProjectDeveloperUser: () => false,
            isUserOwnerOf: () => false,
          } as unknown as AuthContextType
        }
      >
        <KeyProjectInformation project={debugProject}></KeyProjectInformation>
      </AuthContext.Provider>,
    );
    await waitFor(() => {
      expect(screen.getByText("Project Notes")).toBeInTheDocument();
    });
  });

  it("doesn't render project notes for certain users", async () => {
    render(
      <AuthContext.Provider
        value={
          {
            isAdminUser: () => false,
            isArmUser: () => false,
            isProjectDeveloperUser: () => false,
            isUserOwnerOf: () => false,
          } as unknown as AuthContextType
        }
      >
        <KeyProjectInformation project={debugProject}></KeyProjectInformation>
      </AuthContext.Provider>,
    );
    await waitFor(() => {
      expect(screen.queryByText("Project Notes")).not.toBeInTheDocument();
    });
  });
});

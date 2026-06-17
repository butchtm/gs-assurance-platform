import { screen, waitFor } from "@testing-library/react";
import { useParams } from "react-router-dom";

import { renderWithProviders } from "@/test/test-utils";
import { ReferenceTypes } from "@/common/data-types/DataReferenceTypes";
import { mockActiveReferenceTypes } from "@/common/data-types/DebugReferenceTypes";
import { mockPlatformAccounts } from "@/common/platform-accounts/DebugPlatformAccounts";
import { debugProject } from "@/common/project/DebugProject";
import { AuthContextType } from "@/components/Auth/Login/AuthProvider";
import { getAccount, getAccountCompetencies, getAccounts } from "@/components/PlatformAccounts/PlatformAccount";
import { getAllProjects } from "@/components/Project/Project";

import PlatformAccountView from "./PlatformAccountView";

jest.mock("./PlatformAccount", () => ({
  getAccount: jest.fn(),
  getAccounts: jest.fn(),
  getAccountCompetencies: jest.fn(),
  createAccount: jest.fn(),
  updateAccount: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useParams: jest.fn(),
  useNavigate: () => jest.fn(),
}));

jest.mock("../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../common/data-types/DataReferenceTypes"),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
}));

jest.mock("../User/User", () => ({
  getAllActiveUsers: () => [
    { id: 1, userName: "User1", emailAddress: "user1@example.com", isActive: true, accountId: 1 },
  ],
}));

jest.mock("../Project/Project", () => ({
  getAllProjects: jest.fn(),
}));

describe("PlatformAccountsView", () => {
  const mockAuthProvider = {
    isAdminUser: jest.fn(),
  } as Record<keyof AuthContextType, jest.Mock>;

  const requiredReferenceTypes: ReferenceTypes = {
    ProjectLocation: [
      {
        id: 1,
        code: "AF",
        description: "Afghanistan",
      },
      {
        id: 3,
        code: "AL",
        description: "Albania",
      },
    ],
    ProjectType: [],
    ProjectGsids: [],
    EstimatedAnnualEmissionReductionAmount: [],
    ProjectSize: [],
    Methodology: [],
    RequestType: [],
    ProjectAsset: [],
    VvbAssignedIds: [],
    ProjectDeveloperIds: [],
    SustainableDevelopmentGoal: [],
  };

  beforeEach(() => {
    jest.clearAllMocks();
    mockAuthProvider.isAdminUser.mockReturnValue(true);

    (getAccount as jest.Mock).mockResolvedValue(mockPlatformAccounts[0]);
    (getAccounts as jest.Mock).mockResolvedValue({ content: mockPlatformAccounts });
    (useParams as jest.Mock).mockImplementation(() => ({ accountId: 1 }));
    (getAccountCompetencies as jest.Mock).mockResolvedValue({
      estimatedAnnualEmissionReductionAmount: 12345,
      projectGsids: [],
      projectTypeIds: [],
      projectLocationIds: [1, 2],
      projectScaleIds: [],
      methodologyIds: [],
      requestTypeIds: [],
      certifiedProductIds: [],
      vvbAssignedIds: [],
      projectDeveloperIds: [],
      sustainableDevelopmentGoalIds: [],
    });
    (getAllProjects as jest.Mock).mockResolvedValue([debugProject]);
  });

  it("renders correctly", async () => {
    renderWithProviders(<PlatformAccountView />, {
      providerOverrides: {
        auth: {
          isAdminUser: jest.fn().mockReturnValue(true),
        },
        reference: {
          activeReferenceTypes: requiredReferenceTypes,
        },
      },
    });

    expect(await screen.findAllByText("Account One")).toHaveLength(2);
    expect(await screen.findByText("Desc1")).toBeInTheDocument();
  });

  it("renders competencies correctly", async () => {
    renderWithProviders(<PlatformAccountView />, {
      providerOverrides: {
        auth: {
          isAdminUser: jest.fn().mockReturnValue(true),
        },
        reference: {
          activeReferenceTypes: requiredReferenceTypes,
        },
      },
    });

    await waitFor(() => {
      expect(screen.getByText("Estimated Annual Emission Reduction Amount")).toBeInTheDocument();
      expect(screen.getByText("12,345")).toBeInTheDocument();
      expect(screen.getByText("Project Location")).toBeInTheDocument();
      expect(screen.getByText("Afghanistan")).toBeInTheDocument();
    });
  });
});

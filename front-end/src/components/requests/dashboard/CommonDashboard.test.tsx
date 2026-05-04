import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { cloneDeep } from "lodash";
import MockDate from "mockdate";
import { HelmetProvider } from "react-helmet-async";
import { MemoryRouter } from "react-router-dom";
import { toast } from "react-toastify";

import { mockActiveReferenceTypes } from "../../../common/data-types/DebugReferenceTypes";
import { mockCommonDashboardRequestResponse } from "../../../common/request/DebugRequests";
import { AuthContext, AuthContextType } from "../../../components/Auth/Login/AuthProvider";
import { exportCommonDashboardReport, getCommonDashboardRequests } from "../Requests";
import { PermissionGroup } from "../../../PermissionGroup";

import { CommonDashboardTable } from "./CommonDashboardTable";

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../common/data-types/DataReferenceTypes"),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
}));

jest.mock("../Requests", () => ({
  ...jest.requireActual<{ getCommonDashboardRequests: typeof getCommonDashboardRequests }>("../Requests"),
  getCommonDashboardRequests: jest.fn(),
  exportCommonDashboardReport: jest.fn(),
}));

jest.mock("react-toastify", () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

const defaultAuthUser = {
  id: 1,
  userName: "mock-user",
  givenName: "joe",
  familyName: "bloggs",
  emailAddress: "joe.bloggs@outlook.com",
  isActive: true,
  groups: [PermissionGroup.ARM_TEAM],
};
const defaultMockAuthContext: AuthContextType = {
  isAuthenticated: true,
  user: defaultAuthUser,
  login: jest.fn(),
  setNewPassword: jest.fn(),
  logout: jest.fn(),
  isProjectDeveloperUser: jest.fn(),
  isAdminUser: jest.fn(),
  isArmUser: jest.fn(),
  isFinanceUser: jest.fn(),
  isReviewerUser: jest.fn(),
  isRegistryUser: jest.fn(),
  isTacUser: jest.fn(),
  isVvbUser: jest.fn(),
  isUserOwnerOf: jest.fn(),
  isSupportUser: jest.fn(),
  respondToSoftwareTokenMfa: jest.fn(),
  respondToEmailOtp: jest.fn(),
  selectMfaType: jest.fn(),
};

describe("CommonDashboardTable", () => {
  beforeEach(() => {
    MockDate.set("2024-01-22");
    (getCommonDashboardRequests as jest.Mock).mockResolvedValue({
      content: mockCommonDashboardRequestResponse,
      totalElements: mockCommonDashboardRequestResponse.length,
    });
    (exportCommonDashboardReport as jest.Mock).mockResolvedValue({});
  });

  afterAll(() => {
    MockDate.reset();
  });

  test("loads and displays common dashboard requests", async () => {
    render(
      <HelmetProvider>
        <MemoryRouter>
          <CommonDashboardTable />
        </MemoryRouter>
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(screen.getByText("GS1")).toBeInTheDocument();
      expect(screen.getByText("GS2")).toBeInTheDocument();
    });

    expect(screen.getByText("Project 1")).toBeInTheDocument();
    expect(screen.getByText("Project 2")).toBeInTheDocument();

    expect(screen.getByText("Status 1")).toBeInTheDocument();
    expect(screen.getByText("Status 2")).toBeInTheDocument();

    expect(screen.getByText("Request A")).toBeInTheDocument();
    expect(screen.getByText("Request B")).toBeInTheDocument();

    expect(screen.getByText("Invoice 1")).toBeInTheDocument();
    expect(screen.getByText("Invoice 2")).toBeInTheDocument();

    expect(screen.getByText("Type1, Type2")).toBeInTheDocument();
    expect(screen.getByText("Type3, Type4")).toBeInTheDocument();

    expect(screen.getByText("15/01/2024")).toBeInTheDocument();
    expect(screen.getByText("16/01/2024")).toBeInTheDocument();
  });

  test("displays no records message when there are no requests", async () => {
    (getCommonDashboardRequests as jest.Mock).mockResolvedValue({
      content: [],
      totalElements: 0,
    });

    render(
      <HelmetProvider>
        <MemoryRouter>
          <CommonDashboardTable />
        </MemoryRouter>
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(screen.getByText(/No results found/i)).toBeInTheDocument();
    });
  });

  test("should not display export button if user does not have email address configured", async () => {
    const authUser = cloneDeep(defaultAuthUser);
    authUser.emailAddress = "";
    const mockAuthContext = cloneDeep(defaultMockAuthContext);
    mockAuthContext.user = authUser;

    render(
      <HelmetProvider>
        <MemoryRouter>
          <AuthContext.Provider value={mockAuthContext}>
            <CommonDashboardTable />
          </AuthContext.Provider>
        </MemoryRouter>
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(screen.queryByTestId("export-button")).toBeNull();
    });
  });

  test("should not display export button if is not admin or ARM user", async () => {
    const authUser = cloneDeep(defaultAuthUser);
    authUser.groups = [PermissionGroup.SUPPORT];
    const mockAuthContext = cloneDeep(defaultMockAuthContext);
    mockAuthContext.user = authUser;

    render(
      <HelmetProvider>
        <MemoryRouter>
          <AuthContext.Provider value={mockAuthContext}>
            <CommonDashboardTable />
          </AuthContext.Provider>
        </MemoryRouter>
      </HelmetProvider>,
    );

    await waitFor(() => {
      expect(screen.queryByTestId("export-button")).toBeNull();
    });
  });

  test("should display confirmation dialog if export triggered successfully", async () => {
    render(
      <HelmetProvider>
        <MemoryRouter>
          <AuthContext.Provider value={defaultMockAuthContext}>
            <CommonDashboardTable />
          </AuthContext.Provider>
        </MemoryRouter>
      </HelmetProvider>,
    );

    const exportButton = screen.getByTestId("export-button");
    await waitFor(() => {
      expect(exportButton).toBeVisible();
    });

    fireEvent.click(exportButton);

    await waitFor(() => {
      expect(screen.getByText(/Export Report Confirmation/i)).toBeInTheDocument();
    });
  });

  test("should display toast error dialog if export trigger errors", async () => {
    (exportCommonDashboardReport as jest.Mock).mockRejectedValueOnce(new Error("Export failed"));

    render(
      <HelmetProvider>
        <MemoryRouter>
          <AuthContext.Provider value={defaultMockAuthContext}>
            <CommonDashboardTable />
          </AuthContext.Provider>
        </MemoryRouter>
      </HelmetProvider>,
    );

    const exportButton = screen.getByTestId("export-button");
    await waitFor(() => {
      expect(exportButton).toBeVisible();
    });

    fireEvent.click(exportButton);

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith("Failed to export request report. Please try again later.");
    });
  });
});

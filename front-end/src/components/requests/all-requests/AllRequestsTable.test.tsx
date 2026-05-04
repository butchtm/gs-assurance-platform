import { render, screen, waitFor } from "@testing-library/react";
import { HelmetProvider } from "react-helmet-async";
import { MemoryRouter } from "react-router-dom";

import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { AuthContext, AuthContextType } from "../../Auth/Login/AuthProvider";
import * as PlatformAccountModule from "../../PlatformAccounts/PlatformAccount";
import * as RequestsModule from "../Requests";

import { AllRequestsTable } from "./AllRequestsTable";

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  useReferenceTypes: jest.fn(),
}));
jest.mock("../../PlatformAccounts/PlatformAccount", () => ({
  getAccounts: jest.fn(),
}));

jest.mock("../Requests", () => {
  return {
    ...jest.requireActual("../Requests"),
    getAllRequests: jest.fn(),
  };
});

const mockReferenceTypes = {
  activeReferenceTypes: {
    RequestStatus: [
      { id: 1, description: "Pending" },
      { id: 2, description: "Approved" },
    ],
    RequestType: [
      { id: 1, description: "Type A", active: true },
      { id: 2, description: "Type B", active: true },
    ],
  },
};

const mockAllRequestsContent = [
  {
    gsid: "GSID-1",
    projectName: "Project 1",
    projectAccountName: "Dev 1",
    requestName: "Type A",
    batchSize: 10,
    requestStatus: "Pending",
    currentRoundIndex: 1,
    currentRoundEndDate: "2025-07-01",
    reviewerAccountName: "Reviewer 1",
    vvbAccountName: "VVB 1",
    qaqcAccountName: "QAQC 1",
    qaqcAccountUnreadMessages: 4,
    activityName: "Activity 1",
    plannedEndDate: "2025-07-10",
    responsibleAccountName: "Resp 1",
    submittedByGivenName: "John",
    submittedByFamilyName: "Doe",
    lastRequestStatusChangeDate: "2025-06-30",
    invoiceStatus: "Unpaid",
  },
];

const mockAuthProvider = {
  isArmUser: jest.fn(),
  isAdminUser: jest.fn(),
} as Record<keyof AuthContextType, jest.Mock>;

describe("AllRequestsTable", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (useReferenceTypes as jest.Mock).mockReturnValue(mockReferenceTypes);
    (PlatformAccountModule.getAccounts as jest.Mock).mockResolvedValue({
      content: [
        { id: 1, name: "Account 1", accountType: { id: 1 } },
        { id: 2, name: "Account 2", accountType: { id: 2 } },
      ],
    });
    (RequestsModule.getAllRequests as jest.Mock).mockResolvedValue({
      content: mockAllRequestsContent,
      totalElements: 1,
    });
  });

  it("renders table headers", async () => {
    render(
      <HelmetProvider>
        <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
          <MemoryRouter>
            <AllRequestsTable />
          </MemoryRouter>
        </AuthContext.Provider>
      </HelmetProvider>,
    );
    expect(await screen.findByText("GSID")).toBeInTheDocument();
    expect(screen.getByText("Project Name")).toBeInTheDocument();
    expect(screen.getByText("Request Type")).toBeInTheDocument();
    expect(screen.getByText("Round")).toBeInTheDocument();
    expect(screen.getByText("Round of Review End Date")).toBeInTheDocument();
    expect(screen.getByText("Activity")).toBeInTheDocument();
    expect(screen.getByText("Weekdays Remaining")).toBeInTheDocument();
    expect(screen.getByText("Responsibility")).toBeInTheDocument();
    expect(screen.getByText("Last Submitted Date")).toBeInTheDocument();
    expect(screen.getByText("Invoice Status")).toBeInTheDocument();
  });

  it("renders request data", async () => {
    render(
      <HelmetProvider>
        <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
          <MemoryRouter>
            <AllRequestsTable />
          </MemoryRouter>
        </AuthContext.Provider>
      </HelmetProvider>,
    );
    expect(await screen.findByText("GSID-1")).toBeInTheDocument();
    expect(screen.getByText("Project 1")).toBeInTheDocument();
    expect(screen.getByText("Dev 1")).toBeInTheDocument();
    expect(screen.getByText("Type A")).toBeInTheDocument();
    expect(screen.getByText("10")).toBeInTheDocument();
    expect(screen.getByText("Pending")).toBeInTheDocument();
    expect(screen.getByText("Reviewer 1")).toBeInTheDocument();
    expect(screen.getByText("VVB 1")).toBeInTheDocument();
    expect(screen.getByText("QAQC 1")).toBeInTheDocument();
    expect(screen.getByText("Activity 1")).toBeInTheDocument();
    expect(screen.getByText("Resp 1")).toBeInTheDocument();
    expect(screen.getByText("John Doe")).toBeInTheDocument();
    expect(screen.getByText("Unpaid")).toBeInTheDocument();
    expect(screen.getByText(/\(01\/07\/2025\)/)).toBeInTheDocument(); // currentRoundEndDate
    expect(screen.getByText("30/06/2025")).toBeInTheDocument(); // lastRequestStatusChangeDate
    expect(screen.getByText(/\(10\/07\/2025\)/)).toBeInTheDocument(); // plannedEndDate
  });

  it("renders open assignments button for ARM team users", async () => {
    mockAuthProvider.isArmUser.mockReturnValue(true);
    render(
      <HelmetProvider>
        <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
          <MemoryRouter>
            <AllRequestsTable />
          </MemoryRouter>
        </AuthContext.Provider>
      </HelmetProvider>,
    );
    await waitFor(() => {
      expect(screen.getByText("Assignments")).toBeInTheDocument();
      expect(screen.getByTestId("open-assignments-button")).toBeInTheDocument();
    });
  });

  it("does not render open assignments button for non ARM users (admin)", async () => {
    mockAuthProvider.isArmUser.mockReturnValue(false);
    render(
      <HelmetProvider>
        <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
          <MemoryRouter>
            <AllRequestsTable />
          </MemoryRouter>
        </AuthContext.Provider>
      </HelmetProvider>,
    );
    await waitFor(() => {
      expect(screen.queryByText("Assignments")).not.toBeInTheDocument();
      expect(screen.queryByTestId("open-assignments-button")).not.toBeInTheDocument();
    });
  });

  it("shows number of unread messages against a QAQC assignment if available", async () => {
    render(
      <HelmetProvider>
        <AuthContext.Provider value={mockAuthProvider as unknown as AuthContextType}>
          <MemoryRouter>
            <AllRequestsTable />
          </MemoryRouter>
        </AuthContext.Provider>
      </HelmetProvider>,
    );
    await waitFor(() => {
      expect(screen.getByText("4 New")).toBeInTheDocument();
    });
  });
});

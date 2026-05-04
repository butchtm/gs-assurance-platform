import { fireEvent, render, screen, waitFor, within } from "@testing-library/react";
import dayjs from "dayjs";
import MockDate from "mockdate";

import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { PermissionGroup } from "../../../PermissionGroup";
import { AuthContext, AuthContextType } from "../../Auth/Login/AuthProvider";

import {
  AssignmentSummaryResponse,
  exportArmAssignmentSummaryReport,
  exportRoeAssignmentSummaryReport,
  getArmAssignmentSummaryResponse,
  getRoeAssignmentSummaryResponse,
} from "./Assignment";
import { AssignmentSummaryModal } from "./AssignmentSummaryModal";

jest.mock("../../../components/requests/assignment/Assignment", () => ({
  getRoeAssignmentSummaryResponse: jest.fn(),
  getArmAssignmentSummaryResponse: jest.fn(),
  exportRoeAssignmentSummaryReport: jest.fn(),
  exportArmAssignmentSummaryReport: jest.fn(),
}));

jest.mock("../../../common/data-types/DataReferenceTypes", () => ({
  useReferenceTypes: jest.fn(),
}));

const mockReferenceTypes = {
  activeReferenceTypes: {
    AssignmentType: [
      { id: 2, description: "QA/QC", code: "QA/QC" },
      { id: 1, description: "Review", code: "REVIEW" },
    ],
    RequestType: [
      { id: 1, code: "TYPE1", description: "Type1", active: true },
      { id: 2, code: "TYPE2", description: "Type2", active: true },
    ],
    ProjectType: [
      { id: 1, code: "ProjectTypeCode1", description: "ProjectType1", active: true },
      { id: 2, code: "ProjectTypeCode2", description: "ProjectType2", active: true },
    ],
  },
};

const mockARMTabAssignmentSummaryResponse: AssignmentSummaryResponse = {
  assignmentSummary: {
    content: [
      {
        accountName: "ARM Team A",
        upcomingRequests: 10,
        dueRequests: 20,
        overdueRequests: 30,
      },
      {
        accountName: "ARM Team B",
        upcomingRequests: 15,
        dueRequests: 25,
        overdueRequests: 35,
      },
      {
        accountName: "ARM Team C",
        upcomingRequests: 5,
        dueRequests: 10,
        overdueRequests: 15,
      },
    ],
    totalPages: 1,
    pageNumber: 1,
    totalElements: 3,
  },
  assignmentTotals: {
    accountName: "Total",
    upcomingRequests: 30,
    dueRequests: 55,
    overdueRequests: 80,
  },
};

const mockRoEAssignmentSummaryResponse: AssignmentSummaryResponse = {
  assignmentSummary: {
    content: [
      {
        accountName: "Reviewer A",
        dueRequests: 5,
        overdueRequests: 3,
        openRequests: 10,
        closedRequests: 20,
        roundOneRequests: 6,
        inProgressRequests: 3,
      },
      {
        accountName: "Reviewer B",
        dueRequests: 8,
        overdueRequests: 2,
        openRequests: 20,
        closedRequests: 50,
        roundOneRequests: 7,
        inProgressRequests: 11,
      },
      {
        accountName: "Reviewer C",
        dueRequests: 4,
        overdueRequests: 1,
        openRequests: 10,
        closedRequests: 30,
        roundOneRequests: 4,
        inProgressRequests: 2,
      },
    ],
    totalPages: 1,
    pageNumber: 1,
    totalElements: 3,
  },
  assignmentTotals: {
    accountName: "Total",
    dueRequests: 17,
    overdueRequests: 6,
    openRequests: 40,
    closedRequests: 100,
    roundOneRequests: 17,
    inProgressRequests: 16,
  },
};

const mockRoEFilteredAssignmentSummaryResponse: AssignmentSummaryResponse = {
  assignmentSummary: {
    content: [
      {
        accountName: "Reviewer A",
        dueRequests: 2,
        overdueRequests: 1,
        openRequests: 5,
        closedRequests: 10,
        roundOneRequests: 3,
        inProgressRequests: 1,
      },
    ],
    totalPages: 1,
    pageNumber: 1,
    totalElements: 1,
  },
  assignmentTotals: {
    accountName: "Total",
    dueRequests: 2,
    overdueRequests: 1,
    openRequests: 5,
    closedRequests: 10,
    roundOneRequests: 3,
    inProgressRequests: 1,
  },
};

const qaqcAssignmentType = 2;
const armTabFilters = {
  assignmentTypeId: qaqcAssignmentType,
  requestDueBefore: dayjs.utc("2025-10-27T00:00:00.000Z"),
  qaqcAssignedToCallingAccount: false,
};

const reviewAssignmentType = 1;
const roeFilters = {
  assignmentTypeId: reviewAssignmentType,
  qaqcAssignedToCallingAccount: false,
  lastSubmittedAfter: dayjs.utc("2025-09-13T00:00:00.000Z"),
  lastSubmittedBefore: dayjs.utc("2025-10-13T23:59:59.999Z"),
  requestDueBefore: dayjs.utc("2025-10-27T00:00:00.000Z"),
};

describe("AssignmentSummaryModal", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (useReferenceTypes as jest.Mock).mockReturnValue(mockReferenceTypes);
    MockDate.set("2025-10-13");
  });

  afterAll(() => {
    MockDate.reset();
  });

  it("calls the assignment summary endpoint with ROE filters on open", async () => {
    (getArmAssignmentSummaryResponse as jest.Mock).mockResolvedValue(mockARMTabAssignmentSummaryResponse);
    (getRoeAssignmentSummaryResponse as jest.Mock).mockResolvedValue(mockRoEAssignmentSummaryResponse);
    render(<AssignmentSummaryModal isOpen={true} onClose={jest.fn()} />);
    await waitFor(() => {
      const roeTab = screen.getByTestId("roe-summary-tab");
      fireEvent.click(roeTab);
      const pageIndex = 1;
      const pageNumber = 5;
      expect(getRoeAssignmentSummaryResponse).toHaveBeenLastCalledWith(roeFilters, pageIndex, pageNumber);
    });
  });

  it("calls the assignment summary endpoint with ARM filters when switching back to ARM tab", async () => {
    (getRoeAssignmentSummaryResponse as jest.Mock).mockResolvedValue(mockRoEAssignmentSummaryResponse);
    (getArmAssignmentSummaryResponse as jest.Mock).mockResolvedValue(mockARMTabAssignmentSummaryResponse);

    render(<AssignmentSummaryModal isOpen={true} onClose={jest.fn()} />);

    const pageIndex = 1;
    const pageNumber = 5;

    const roeTab = screen.getByTestId("roe-summary-tab");
    fireEvent.click(roeTab);

    await waitFor(() => {
      expect(getRoeAssignmentSummaryResponse).toHaveBeenCalled();
    });

    const armTab = screen.getByTestId("arm-summary-tab");
    fireEvent.click(armTab);

    await waitFor(() => {
      expect(getArmAssignmentSummaryResponse).toHaveBeenLastCalledWith(armTabFilters, pageIndex, pageNumber);
    });
  });

  it("renders table headers for ARM Summary", async () => {
    (getArmAssignmentSummaryResponse as jest.Mock).mockResolvedValue(mockARMTabAssignmentSummaryResponse);
    render(<AssignmentSummaryModal isOpen={true} onClose={jest.fn()} />);

    await waitFor(() => {
      const armTab = screen.getByTestId("arm-summary-tab");
      fireEvent.click(armTab);

      expect(screen.getByTestId("summary-table")).toBeInTheDocument();
      const table = within(screen.getByTestId("summary-table"));
      expect(table.getByText("Name")).toBeInTheDocument();
      expect(table.getByText("Upcoming Requests")).toBeInTheDocument();
      expect(table.getByText("Requests Due")).toBeInTheDocument();
      expect(table.getByText("Overdue Requests")).toBeInTheDocument();
    });
  });

  it("renders table data for ARM Summary", async () => {
    (getArmAssignmentSummaryResponse as jest.Mock).mockResolvedValue(mockARMTabAssignmentSummaryResponse);
    render(<AssignmentSummaryModal isOpen={true} onClose={jest.fn()} />);

    await waitFor(() => {
      const armTab = screen.getByTestId("arm-summary-tab");
      fireEvent.click(armTab);

      expect(screen.getByTestId("summary-table")).toBeInTheDocument();
      const table = within(screen.getByTestId("summary-table"));
      expect(table.getByText("ARM Team A")).toBeInTheDocument();
      expect(table.getByText("ARM Team B")).toBeInTheDocument();
      expect(table.getByText("ARM Team C")).toBeInTheDocument();
      expect(table.getAllByText("10")).toHaveLength(2);
      expect(table.getAllByText("20")).toHaveLength(1);
      expect(table.getByText("25")).toBeInTheDocument();
      expect(table.getByText("30")).toBeInTheDocument();
      expect(table.getByText("35")).toBeInTheDocument();
    });
  });

  it("renders summary totals for ARM Summary", async () => {
    (getArmAssignmentSummaryResponse as jest.Mock).mockResolvedValue(mockARMTabAssignmentSummaryResponse);
    render(<AssignmentSummaryModal isOpen={true} onClose={jest.fn()} />);

    await waitFor(() => {
      const armTab = screen.getByTestId("arm-summary-tab");
      fireEvent.click(armTab);

      expect(screen.getByTestId("summary-totals")).toBeInTheDocument();
      const totals = within(screen.getByTestId("summary-totals"));
      expect(totals.getByText("Totals")).toBeInTheDocument();
      expect(totals.getByText("Upcoming")).toBeInTheDocument();
      expect(totals.getByTestId("summary-totals-upcoming")).toHaveTextContent("30");
      expect(totals.getByText("Due")).toBeInTheDocument();
      expect(totals.getByTestId("summary-totals-due")).toHaveTextContent("55");
      expect(totals.getByText("Overdue")).toBeInTheDocument();
      expect(totals.getByTestId("summary-totals-overdue")).toHaveTextContent("80");
    });
  });

  it("calls the assignment summary endpoint with RoE filters", async () => {
    (getArmAssignmentSummaryResponse as jest.Mock).mockResolvedValue(mockARMTabAssignmentSummaryResponse);
    (getRoeAssignmentSummaryResponse as jest.Mock).mockResolvedValue(mockRoEAssignmentSummaryResponse);
    render(<AssignmentSummaryModal isOpen={true} onClose={jest.fn()} />);

    const pageIndex = 1;
    const pageNumber = 5;
    await waitFor(() => {
      const tab = screen.getByTestId("roe-summary-tab");
      fireEvent.click(tab);

      expect(getRoeAssignmentSummaryResponse).toHaveBeenLastCalledWith(roeFilters, pageIndex, pageNumber);
    });
  });

  it("renders table headers for RoE Summary", async () => {
    (getArmAssignmentSummaryResponse as jest.Mock).mockResolvedValue(mockARMTabAssignmentSummaryResponse);
    (getRoeAssignmentSummaryResponse as jest.Mock).mockResolvedValue(mockRoEAssignmentSummaryResponse);
    render(<AssignmentSummaryModal isOpen={true} onClose={jest.fn()} />);

    await waitFor(() => {
      const tab = screen.getByTestId("roe-summary-tab");
      fireEvent.click(tab);

      expect(screen.getByTestId("summary-table")).toBeInTheDocument();
      const table = within(screen.getByTestId("summary-table"));
      expect(table.getByText("Name")).toBeInTheDocument();
      expect(table.getByText("Requests Due")).toBeInTheDocument();
      expect(table.getByText("Overdue Requests")).toBeInTheDocument();
      expect(table.getByText("Active")).toBeInTheDocument();
      expect(table.getByText("Round 1")).toBeInTheDocument();
      expect(table.getByText("Open")).toBeInTheDocument();
      expect(table.getByText("Closed")).toBeInTheDocument();
    });
  });

  it("renders table data for RoE Summary", async () => {
    (getArmAssignmentSummaryResponse as jest.Mock).mockResolvedValue(mockARMTabAssignmentSummaryResponse);
    (getRoeAssignmentSummaryResponse as jest.Mock).mockResolvedValue(mockRoEAssignmentSummaryResponse);
    render(<AssignmentSummaryModal isOpen={true} onClose={jest.fn()} />);

    await waitFor(() => {
      const tab = screen.getByTestId("roe-summary-tab");
      fireEvent.click(tab);

      expect(screen.getByTestId("summary-table")).toBeInTheDocument();
      const table = within(screen.getByTestId("summary-table"));
      expect(table.getByText("Reviewer A")).toBeInTheDocument();
      expect(table.getByText("Reviewer B")).toBeInTheDocument();
      expect(table.getByText("Reviewer C")).toBeInTheDocument();
      expect(table.getAllByText("10")).toHaveLength(2);
      expect(table.getAllByText("20")).toHaveLength(2);
      expect(table.getByText("30")).toBeInTheDocument();
      expect(table.getByText("50")).toBeInTheDocument();
      expect(table.getByText("6")).toBeInTheDocument();
      expect(table.getByText("7")).toBeInTheDocument();
      expect(table.getByText("11")).toBeInTheDocument();
    });
  });

  it("renders summary totals for RoE Summary", async () => {
    (getArmAssignmentSummaryResponse as jest.Mock).mockResolvedValue(mockARMTabAssignmentSummaryResponse);
    (getRoeAssignmentSummaryResponse as jest.Mock).mockResolvedValue(mockRoEAssignmentSummaryResponse);
    render(<AssignmentSummaryModal isOpen={true} onClose={jest.fn()} />);

    await waitFor(() => {
      const tab = screen.getByTestId("roe-summary-tab");
      fireEvent.click(tab);

      expect(screen.getByTestId("summary-totals")).toBeInTheDocument();
      const totals = within(screen.getByTestId("summary-totals"));
      expect(totals.getByText("Totals")).toBeInTheDocument();
      expect(totals.getByText("Due")).toBeInTheDocument();
      expect(totals.getByTestId("summary-totals-due")).toHaveTextContent("17");
      expect(totals.getByText("Overdue")).toBeInTheDocument();
      expect(totals.getByTestId("summary-totals-overdue")).toHaveTextContent("6");
      expect(totals.getByText("Round 1")).toBeInTheDocument();
      expect(totals.getByTestId("summary-totals-round-1")).toHaveTextContent("17");
      expect(totals.getByText("Active")).toBeInTheDocument();
      expect(totals.getByTestId("summary-totals-active")).toHaveTextContent("16");
      expect(totals.getByText("Open")).toBeInTheDocument();
      expect(totals.getByTestId("summary-totals-open")).toHaveTextContent("40");
      expect(totals.getByText("Closed")).toBeInTheDocument();
      expect(totals.getByTestId("summary-totals-closed")).toHaveTextContent("100");
    });
  });

  it("calls report export endpoint with RoE variables when export button on RoE summary tab is clicked", async () => {
    (getArmAssignmentSummaryResponse as jest.Mock).mockResolvedValue(mockARMTabAssignmentSummaryResponse);
    (getRoeAssignmentSummaryResponse as jest.Mock).mockResolvedValue(mockRoEAssignmentSummaryResponse);
    render(<AssignmentSummaryModal isOpen={true} onClose={jest.fn()} />);

    await waitFor(() => {
      const tab = screen.getByTestId("roe-summary-tab");
      fireEvent.click(tab);

      const exportButton = screen.getByTestId("export-summary-button");
      fireEvent.click(exportButton);
      expect(exportRoeAssignmentSummaryReport).toHaveBeenCalledWith(roeFilters);
    });
  });

  it("calls report export endpoint with ARM variables when export button on ARM summary tab is clicked", async () => {
    (getArmAssignmentSummaryResponse as jest.Mock).mockResolvedValue(mockARMTabAssignmentSummaryResponse);
    render(<AssignmentSummaryModal isOpen={true} onClose={jest.fn()} />);

    await waitFor(() => {
      const tab = screen.getByTestId("arm-summary-tab");
      fireEvent.click(tab);

      const exportButton = screen.getByTestId("export-summary-button");
      fireEvent.click(exportButton);
      expect(exportArmAssignmentSummaryReport).toHaveBeenCalledWith(armTabFilters);
    });
  });

  it("calls the assignment summary endpoint with RoE parameters when 'QA/QC Assigned to Me' is activated", async () => {
    (getArmAssignmentSummaryResponse as jest.Mock).mockResolvedValue(mockARMTabAssignmentSummaryResponse);
    (getRoeAssignmentSummaryResponse as jest.Mock)
      .mockResolvedValueOnce(mockRoEAssignmentSummaryResponse)
      .mockResolvedValueOnce(mockRoEFilteredAssignmentSummaryResponse);
    const mockAuthProvider = {
      isArmUser: jest.fn(() => true),
      isAuthenticated: true,
      user: {
        groups: [PermissionGroup.ARM_TEAM],
      },
    } as unknown as AuthContextType;

    render(
      <AuthContext.Provider value={mockAuthProvider}>
        <AssignmentSummaryModal isOpen={true} onClose={jest.fn()} />
      </AuthContext.Provider>,
    );

    await waitFor(() => {
      const roeTab = screen.getByTestId("roe-summary-tab");
      fireEvent.click(roeTab);
    });

    await waitFor(() => {
      const toggleButton = screen.getByTestId("qaqc-assigned-to-calling-user-switch");
      fireEvent.click(toggleButton);
    });

    const updatedRoeFilters = { ...roeFilters, qaqcAssignedToCallingAccount: true };
    const pageIndex = 1;
    const pageSize = 5;
    await waitFor(() => {
      expect(getRoeAssignmentSummaryResponse).toHaveBeenLastCalledWith(updatedRoeFilters, pageIndex, pageSize);
    });
  });
});

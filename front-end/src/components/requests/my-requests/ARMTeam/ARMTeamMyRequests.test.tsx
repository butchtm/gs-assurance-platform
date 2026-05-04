import { render, screen, waitFor } from "@testing-library/react";
import MockDate from "mockdate";

import { DashboardFilters } from "../../CommonRequestFilters";
import { getAssignedRequests } from "../../Requests";

import ARMTeamRequestsTable from "./ARMTeamRequestsTable";

jest.mock("../../Requests", () => ({
  ...jest.requireActual("../../Requests"),
  getAssignedRequests: jest.fn(),
}));

const mockFilters: DashboardFilters = {
  requestStatusIds: [1, 2],
  projectIds: [1, 2],
  projectAccountIds: [1, 2],
  reviewerAccountIds: [1, 2],
  vvbAccountIds: [1, 2],
};

const mockRequestsResponse = {
  content: [
    {
      requestId: 1,
      gsid: "GSID1",
      projectName: "Project 1",
      projectAccountName: "Developer 1",
      requestName: "Request 1",
      batchSize: 10,
      requestStatus: "Status 1",
      currentRoundIndex: 1,
      assignmentCreatedDate: "2024-07-18",
      dueDate: "2024-07-25",
      reviewerAccountName: "Reviewer 1",
      vvbAccountName: "VVB 1",
      latestActivityName: "Assigning a CC",
      latestActivityPlannedEndDate: "2024-07-29T13:56:01.408771Z",
      latestActivityResponsibleAccountName: "GS ARM Team",
      submittedByGivenName: "Joe",
      submittedByFamilyName: "Blogs",
      invoiceStatus: "Triggered",
      projectRisk: true,
      unreadMessages: 3,
    },
    {
      requestId: 2,
      gsid: "GSID2",
      projectName: "Project 2",
      projectAccountName: "Developer 2",
      requestName: "Request 2",
      batchSize: 20,
      requestStatus: "Status 2",
      currentRoundIndex: 2,
      assignmentCreatedDate: "2024-07-19",
      dueDate: "2024-07-26",
      reviewerAccountName: "Reviewer 2",
      vvbAccountName: "VVB 2",
      latestActivityName: "Assigning a Reviewer",
      latestActivityPlannedEndDate: "2024-07-10T13:56:01.408771Z",
      latestActivityResponsibleAccountName: "GS ARM Team",
      submittedByGivenName: "Jane",
      submittedByFamilyName: "Doe",
      invoiceStatus: "Generated",
    },
  ],
  totalPages: 1,
};

describe("ARMTeamRequestsTable", () => {
  beforeEach(() => {
    MockDate.set("2024-07-16");
    (getAssignedRequests as jest.Mock).mockResolvedValue(mockRequestsResponse);
  });

  afterAll(() => {
    MockDate.reset();
  });

  test("loads and displays requests", async () => {
    render(<ARMTeamRequestsTable filters={mockFilters} openSummaryModal={false} handleSummaryModal={jest.fn()} />);

    await waitFor(() => {
      expect(screen.getByText("GSID1")).toBeInTheDocument();
      expect(screen.getByText("GSID2")).toBeInTheDocument();
    });

    expect(screen.getByText("Project 1")).toBeInTheDocument();
    expect(screen.getByText("Project 2")).toBeInTheDocument();
    expect(screen.getByText("Developer 1")).toBeInTheDocument();
    expect(screen.getByText("Developer 2")).toBeInTheDocument();
    expect(screen.getByText("Request 1")).toBeInTheDocument();
    expect(screen.getByText("Request 2")).toBeInTheDocument();
    expect(screen.getByText("Status 1")).toBeInTheDocument();
    expect(screen.getByText("Status 2")).toBeInTheDocument();
    expect(screen.getByText("10")).toBeInTheDocument();
    expect(screen.getByText("20")).toBeInTheDocument();
    expect(screen.getByText("Assigning a CC")).toBeInTheDocument();
    expect(screen.getByText("Assigning a Reviewer")).toBeInTheDocument();
    expect(screen.getAllByText("GS ARM Team").length).toBe(2);
    expect(screen.getByText("29/07/2024 (9 days)")).toBeInTheDocument();
    expect(screen.getByText("10/07/2024 (4 days ago)")).toBeInTheDocument();
    expect(screen.getByText("Joe Blogs")).toBeInTheDocument();
    expect(screen.getByText("Jane Doe")).toBeInTheDocument();
    expect(screen.getByText("Triggered")).toBeInTheDocument();
    expect(screen.getByText("Generated")).toBeInTheDocument();
  });

  test("Adds high risk flag to request row if project is high risk", async () => {
    render(<ARMTeamRequestsTable filters={mockFilters} openSummaryModal={false} handleSummaryModal={jest.fn()} />);

    await waitFor(() => {
      expect(screen.getByText("High Risk")).toBeInTheDocument();
    });
  });
  test("Adds new messages flag to request row if unread messages are available", async () => {
    render(<ARMTeamRequestsTable filters={mockFilters} openSummaryModal={false} handleSummaryModal={jest.fn()} />);

    await waitFor(() => {
      expect(screen.getByText("3 New")).toBeInTheDocument();
    });
  });
});

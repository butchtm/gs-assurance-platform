import { render, screen, waitFor } from "@testing-library/react";
import MockDate from "mockdate";

import { mockActiveReferenceTypes } from "../../../../common/data-types/DebugReferenceTypes";
import { DashboardFilters } from "../../CommonRequestFilters";
import { getProjectDeveloperRequests, ProjectDeveloperRequestsResponse } from "../../Requests";

import ProjectDeveloperRequestsTable from "./ProjectDeveloperRequestsTable";

jest.mock("../../Requests", () => ({
  ...jest.requireActual("../../Requests"),
  getProjectDeveloperRequests: jest.fn(),
}));

const mockFilters: DashboardFilters = {
  requestStatusIds: [1, 2],
  projectAccountIds: [1, 2],
  reviewerAccountIds: [1, 2],
  vvbAccountIds: [1, 2],
};

const mockRequestsResponse = {
  content: [
    {
      batched: false,
      carbonStream: mockActiveReferenceTypes.CarbonStream[0],
      currentRequestStatus: mockActiveReferenceTypes.RequestStatus[10], // In progress
      currentRoundIndex: 0,
      currentRoundEndDate: "2024-01-18",
      id: 1,
      invoiceStatus: mockActiveReferenceTypes.InvoiceStatus[0],
      gsid: "GS1",
      projectLocations: [mockActiveReferenceTypes.ProjectLocation[0], mockActiveReferenceTypes.ProjectLocation[1]],
      projectName: "Project 1",
      projectTypes: [mockActiveReferenceTypes.ProjectType[0], mockActiveReferenceTypes.ProjectType[1]],
      requestName: "Request 1",
      requiredDocumentStatus: {
        provided: 2,
        required: 5,
      },
      reviewerAccountId: 1,
      reviewerAccountName: "Reviewer 1",
      vvbAccountId: 1,
      vvbAccountName: "VVB 1",
      submittedByGivenName: "Joe",
      submittedByFamilyName: "Blogs",
      unreadMessages: 4,
    },
    {
      batched: true,
      carbonStream: mockActiveReferenceTypes.CarbonStream[1],
      currentRequestStatus: mockActiveReferenceTypes.RequestStatus[9], // Completeness check
      currentRoundIndex: 2,
      currentRoundEndDate: "2024-01-26",
      id: 2,
      invoiceStatus: mockActiveReferenceTypes.InvoiceStatus[1],
      gsid: "GS2",
      projectLocations: [mockActiveReferenceTypes.ProjectLocation[1], mockActiveReferenceTypes.ProjectLocation[0]],
      projectName: "Project 2",
      projectTypes: [mockActiveReferenceTypes.ProjectType[1], mockActiveReferenceTypes.ProjectType[0]],
      requestName: "Request 2",
      requiredDocumentStatus: {
        provided: 0,
        required: 3,
      },
      reviewerAccountId: 2,
      reviewerAccountName: "Reviewer 2",
      vvbAccountId: 2,
      vvbAccountName: "VVB 2",
      submittedByGivenName: "Jane",
      submittedByFamilyName: "Doe",
    },
  ] as ProjectDeveloperRequestsResponse[],
  totalPages: 1,
};

describe("ProjectDeveloperRequestsTable", () => {
  beforeEach(() => {
    MockDate.set("2024-01-22");
    (getProjectDeveloperRequests as jest.Mock).mockResolvedValue(mockRequestsResponse);
  });

  afterAll(() => {
    MockDate.reset();
  });

  test("loads and displays requests", async () => {
    render(<ProjectDeveloperRequestsTable filters={mockFilters} />);

    await waitFor(() => {
      expect(screen.getByText("GS1")).toBeInTheDocument();
      expect(screen.getByText("GS2")).toBeInTheDocument();
    });

    expect(screen.getByText("Project 1")).toBeInTheDocument();
    expect(screen.getByText("Project 2")).toBeInTheDocument();

    expect(screen.getByText("Request 1")).toBeInTheDocument();
    expect(screen.getByText("Request 2")).toBeInTheDocument();

    expect(screen.getByText("VVB 1")).toBeInTheDocument();
    expect(screen.getByText("VVB 2")).toBeInTheDocument();

    expect(screen.getByText("Yes")).toBeInTheDocument();
    expect(screen.getByText("No")).toBeInTheDocument();

    expect(screen.getByText("0")).toBeInTheDocument();
    expect(screen.getByText("2")).toBeInTheDocument();

    expect(screen.getByText("18/01/2024 (2 days ago)")).toBeInTheDocument();
    expect(screen.getByText("26/01/2024 (4 days)")).toBeInTheDocument();

    expect(screen.getByText("In Progress")).toBeInTheDocument();
    expect(screen.getByText("Completeness Check")).toBeInTheDocument();

    expect(screen.getByText("ProjectType1, ProjectType2")).toBeInTheDocument();
    expect(screen.getByText("ProjectType2, ProjectType1")).toBeInTheDocument();

    expect(screen.getByText("ProjectLocation1, ProjectLocation2")).toBeInTheDocument();
    expect(screen.getByText("ProjectLocation2, ProjectLocation1")).toBeInTheDocument();

    expect(screen.getByText("CarbonStream1")).toBeInTheDocument();
    expect(screen.getByText("CarbonStream2")).toBeInTheDocument();

    expect(screen.getByText("2/5")).toBeInTheDocument();
    expect(screen.getByText("0/3")).toBeInTheDocument();

    expect(screen.getByText("Invoice status 1")).toBeInTheDocument();
    expect(screen.getByText("Invoice status 2")).toBeInTheDocument();
  });

  test('displays "No requests found" when there are no requests', async () => {
    (getProjectDeveloperRequests as jest.Mock).mockResolvedValue({ content: [], totalPages: 0 });

    render(<ProjectDeveloperRequestsTable filters={mockFilters} />);

    await waitFor(() => {
      expect(screen.getByText("No requests found")).toBeInTheDocument();
    });
  });

  test("Adds new messages flag to request row if unread messages are available", async () => {
    render(<ProjectDeveloperRequestsTable filters={mockFilters} />);
    await waitFor(() => {
      expect(screen.getByText("4 New")).toBeInTheDocument();
    });
  });
});

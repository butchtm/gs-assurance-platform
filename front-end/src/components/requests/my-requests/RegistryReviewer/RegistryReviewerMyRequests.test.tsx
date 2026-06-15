import { cleanup, screen, waitFor } from "@testing-library/react";
import MockDate from "mockdate";

import { renderWithProviders } from "@/test/test-utils";
import { mockActiveReferenceTypes } from "@/common/data-types/DebugReferenceTypes";
import { getAssignedRequests } from "@/components/requests/Requests";

import RegistryReviewerMyRequests from "./RegistryReviewerMyRequests";

jest.mock("../../Requests", () => ({
  ...jest.requireActual("../../Requests"),
  getAssignedRequests: jest.fn(),
  getAssignedRequestProjects: jest.fn(),
  getAssignedRequestAccounts: jest.fn(),
}));

jest.mock("../../../../common/data-types/DataReferenceTypes", () => ({
  ...jest.requireActual("../../../../common/data-types/DataReferenceTypes"),
  useReferenceTypes: () => ({
    activeReferenceTypes: mockActiveReferenceTypes,
  }),
}));

const mockRequestsResponse = {
  content: [
    {
      requestId: 1,
      assignmentId: 101,
      gsid: "GSID1",
      projectName: "Project 1",
      projectAccountName: "Developer 1",
      requestName: "Request 1",
      assignmentCreatedDate: "2024-07-18",
      projectLocations: [{ description: "Location 1" }],
      currentRoundIndex: 1,
      currentRoundEndDate: "2024-07-23",
      dueDate: "2024-07-24",
      projectTypes: [{ description: "Type 1" }],
      projectCarbonStream: { description: "Stream 1" },
      vvbAccountName: "VVB Account",
      invoiceStatus: "Paid",
      latestActivityName: "Round 1 Review",
      latestActivityPlannedEndDate: "2024-07-29T13:56:01.408771Z",
      latestActivityResponsibleAccountId: 1,
      latestActivityResponsibleAccountName: "Reviewer 1",
    },
    {
      requestId: 2,
      assignmentId: 102,
      gsid: "GSID2",
      projectName: "Project 2",
      projectAccountName: "Developer 2",
      requestName: "Request 2",
      assignmentCreatedDate: "2024-07-19",
      projectLocations: [{ description: "Location 2" }],
      currentRoundIndex: 2,
      currentRoundEndDate: "2024-07-24",
      dueDate: "2024-07-25",
      projectTypes: [{ description: "Type 2" }],
      projectCarbonStream: { description: "Stream 2" },
      vvbAccountName: "VVB Account 2",
      latestActivityName: "Round 2 Review",
      latestActivityPlannedEndDate: "2024-07-10T13:56:01.408771Z",
      latestActivityResponsibleAccountId: 1,
      latestActivityResponsibleAccountName: "Reviewer 2",
    },
  ],
  totalPages: 1,
};

describe("RegistryReviewerMyRequests", () => {
  beforeEach(() => {
    MockDate.set("2024-07-16");
    cleanup();
    (getAssignedRequests as jest.Mock).mockResolvedValue(mockRequestsResponse);
  });

  afterAll(() => {
    MockDate.reset();
  });

  it("loads and displays requests", async () => {
    renderWithProviders(<RegistryReviewerMyRequests />);

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
    expect(screen.getByText("Location 1")).toBeInTheDocument();
    expect(screen.getByText("Location 2")).toBeInTheDocument();
    expect(screen.getByText("Type 1")).toBeInTheDocument();
    expect(screen.getByText("Type 2")).toBeInTheDocument();
    expect(screen.getByText("Stream 1")).toBeInTheDocument();
    expect(screen.getByText("Stream 2")).toBeInTheDocument();
    expect(screen.getByText("VVB Account")).toBeInTheDocument();
    expect(screen.getByText("VVB Account 2")).toBeInTheDocument();
  });

  it('displays "No Assigned Requests found." when there are no requests', async () => {
    (getAssignedRequests as jest.Mock).mockResolvedValue({ content: [], totalPages: 0 });
    renderWithProviders(<RegistryReviewerMyRequests />, {
      providerOverrides: {
        auth: {
          isRegistryUser: jest.fn().mockReturnValue(true),
        },
      },
    });

    await waitFor(() => {
      expect(screen.getByText("No Assigned Requests found.")).toBeInTheDocument();
    });
  });

  it("should display invoice status for registry users", async () => {
    renderWithProviders(<RegistryReviewerMyRequests />, {
      providerOverrides: {
        auth: {
          isRegistryUser: jest.fn().mockReturnValue(true),
        },
      },
    });

    await waitFor(() => {
      expect(screen.getByText("GSID1")).toBeInTheDocument();
      expect(screen.getByText("GSID2")).toBeInTheDocument();
    });

    expect(screen.getByText("Invoice Status")).toBeInTheDocument();
    expect(screen.getByText("Paid")).toBeInTheDocument();
  });

  it("should not display invoice status for non-registry users", async () => {
    renderWithProviders(<RegistryReviewerMyRequests />, {
      providerOverrides: {
        auth: {
          isRegistryUser: jest.fn().mockReturnValue(false),
        },
      },
    });

    await waitFor(() => {
      expect(screen.getByText("GSID1")).toBeInTheDocument();
      expect(screen.getByText("GSID2")).toBeInTheDocument();
    });

    expect(screen.queryByText("Invoice Status")).not.toBeInTheDocument();
    expect(screen.queryByText("Paid")).not.toBeInTheDocument();
  });

  it("should display activity information for reviewer users", async () => {
    renderWithProviders(<RegistryReviewerMyRequests />, {
      providerOverrides: {
        auth: {
          isReviewerUser: jest.fn().mockReturnValue(true),
        },
      },
    });

    await waitFor(() => {
      expect(screen.getByText("GSID1")).toBeInTheDocument();
      expect(screen.getByText("GSID2")).toBeInTheDocument();
    });

    expect(screen.getByText("Round 1 Review")).toBeInTheDocument();
    expect(screen.getByText("Round 2 Review")).toBeInTheDocument();
    expect(screen.getByText("Reviewer 1")).toBeInTheDocument();
    expect(screen.getByText("Reviewer 2")).toBeInTheDocument();
    expect(screen.getByText("29/07/2024 (9 days)")).toBeInTheDocument();
    expect(screen.getByText("10/07/2024 (4 days ago)")).toBeInTheDocument();
  });
});

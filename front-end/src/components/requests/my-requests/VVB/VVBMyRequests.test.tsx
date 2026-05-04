import { render, screen, waitFor } from "@testing-library/react";
import MockDate from "mockdate";
import { MemoryRouter } from "react-router-dom";

import { mockActiveReferenceTypes } from "../../../../common/data-types/DebugReferenceTypes";
import { getAssignedRequests } from "../../Requests";

import VVBMyRequests from "./VVBMyRequests";

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
      requestName: "Request 1",
      requestStatus: "Complete",
      projectAccountName: "Developer 1",
      assignmentCreatedDate: "2024-07-18",
      projectLocations: [{ description: "Location 1" }],
      currentRoundIndex: 9,
      currentRoundEndDate: "2024-07-23",
      projectTypes: [{ description: "Wind" }],
      projectCarbonStream: { description: "GS CER" },
      assignmentStatus: "Accepted",
      submittedByGivenName: "Joe",
      submittedByFamilyName: "Blogs",
      unreadMessages: 3,
    },
    {
      requestId: 2,
      assignmentId: 102,
      gsid: "GSID2",
      projectName: "Project 2",
      requestName: "Request 2",
      requestStatus: "In Preparation",
      projectAccountName: "Developer 2",
      assignmentCreatedDate: "2024-07-19",
      projectLocations: [{ description: "Location 2" }],
      currentRoundIndex: 3,
      currentRoundEndDate: "2024-07-24",
      projectTypes: [{ description: "Biogas - Heat" }],
      projectCarbonStream: { description: "VER" },
      assignmentStatus: "Declined",
      submittedByGivenName: "Jane",
      submittedByFamilyName: "Doe",
    },
    {
      requestId: 3,
      assignmentId: 103,
      gsid: "GSID3",
      projectName: "Project 3",
      requestName: "Request 3",
      requestStatus: "In Audit",
      projectAccountName: "Developer 3",
      assignmentCreatedDate: "2024-07-20",
      projectLocations: [{ description: "Location 3" }],
      currentRoundIndex: 0,
      currentRoundEndDate: "",
      projectTypes: [{ description: "Agriculture" }],
      projectCarbonStream: { description: "Reporting Only" },
      assignmentStatus: "Pending",
      submittedByGivenName: "Bobbie",
      submittedByFamilyName: "Brown",
    },
  ],
  totalPages: 1,
};

describe("VVBMyRequests", () => {
  beforeEach(() => {
    MockDate.set("2024-07-22");
    (getAssignedRequests as jest.Mock).mockResolvedValue(mockRequestsResponse);
  });

  afterAll(() => {
    MockDate.reset();
  });

  test("loads and displays requests", async () => {
    render(
      <MemoryRouter>
        <VVBMyRequests />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText("GSID1")).toBeInTheDocument();
      expect(screen.getByText("GSID2")).toBeInTheDocument();
      expect(screen.getByText("GSID3")).toBeInTheDocument();
    });

    expect(screen.getByText("Project 1")).toBeInTheDocument();
    expect(screen.getByText("Project 2")).toBeInTheDocument();
    expect(screen.getByText("Project 3")).toBeInTheDocument();
    expect(screen.getByText("Request 1")).toBeInTheDocument();
    expect(screen.getByText("Request 2")).toBeInTheDocument();
    expect(screen.getByText("Request 3")).toBeInTheDocument();
    expect(screen.getByText("In Audit")).toBeInTheDocument();
    expect(screen.getByText("In Preparation")).toBeInTheDocument();
    expect(screen.getByText("Developer 1")).toBeInTheDocument();
    expect(screen.getByText("Developer 2")).toBeInTheDocument();
    expect(screen.getByText("Developer 3")).toBeInTheDocument();
    expect(screen.getByText("18/07/2024")).toBeInTheDocument();
    expect(screen.getByText("19/07/2024")).toBeInTheDocument();
    expect(screen.getByText("20/07/2024")).toBeInTheDocument();
    expect(screen.getByText("Location 1")).toBeInTheDocument();
    expect(screen.getByText("Location 2")).toBeInTheDocument();
    expect(screen.getByText("Location 3")).toBeInTheDocument();
    expect(screen.getByText("9")).toBeInTheDocument();
    expect(screen.getByText("3")).toBeInTheDocument();
    expect(screen.getByText("0")).toBeInTheDocument();
    expect(screen.getByText("23/07/2024 (1 day)")).toBeInTheDocument();
    expect(screen.getByText("24/07/2024 (2 days)")).toBeInTheDocument();
    expect(screen.getByText("Wind")).toBeInTheDocument();
    expect(screen.getByText("Biogas - Heat")).toBeInTheDocument();
    expect(screen.getByText("GS CER")).toBeInTheDocument();
    expect(screen.getByText("VER")).toBeInTheDocument();
    expect(screen.getByText("Accepted")).toBeInTheDocument();
    expect(screen.getByText("Pending")).toBeInTheDocument();
    expect(screen.getByText("Declined")).toBeInTheDocument();
  });

  test('displays "No Assigned Requests found." when there are no requests', async () => {
    (getAssignedRequests as jest.Mock).mockResolvedValue({ content: [], totalPages: 0 });

    render(
      <MemoryRouter>
        <VVBMyRequests />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText("No Assigned Requests found.")).toBeInTheDocument();
    });
  });
  test("Adds new messages flag to request row if unread messages are available", async () => {
    render(
      <MemoryRouter>
        <VVBMyRequests />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText("3 New")).toBeInTheDocument();
    });
  });
});
